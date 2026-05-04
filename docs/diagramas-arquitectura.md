# Fase 4 — Diagramas de Arquitectura

Todos los diagramas están en formato **Mermaid** (copiar directo a mermaid.live o cualquier editor compatible).

---

## Diagrama 1 — Contexto de Microservicios

Línea sólida `-->` = comunicación síncrona (REST/Feign).  
Línea punteada `-.->` = comunicación asíncrona (RabbitMQ).

```mermaid
graph TB
    subgraph Cliente
        UI[Cliente / MenuConsola]
    end

    subgraph Gateway["API Gateway :8080"]
        GW[Spring Cloud Gateway]
    end

    subgraph Core["BCs Core"]
        CIRC["circulacion-service\n:8081\nPostgreSQL circulacion_db"]
        MULT["multas-service\n:8084\nPostgreSQL multas_db"]
        RES["reservas-service\n:8088\nPostgreSQL reservas_db"]
    end

    subgraph Supporting["BCs Supporting"]
        MAT["materiales-service\n:8082\nPostgreSQL materiales_db"]
        USR["usuarios-service\n:8083\nPostgreSQL usuarios_db"]
        COB["cobros-service\n:8089\nPostgreSQL cobros_db"]
        EXT["prestamos-externos-service\n:8085\nPostgreSQL prestamos_externos_db"]
    end

    subgraph Generic["BCs Genéricos"]
        NOTI["notificaciones-service\n:8086\nMongoDB notificaciones_db"]
        REP["reportes-service\n:8087\nPostgreSQL reportes_db"]
    end

    subgraph Infra["Infraestructura"]
        RMQ["RabbitMQ\nExchange: biblioteca.events\n:5672 / UI :15672"]
        EUR["Eureka Server\n:8761"]
    end

    UI --> GW

    %% Llamadas síncronas desde circulacion
    GW --> CIRC
    CIRC -->|"GET /materiales/{id}/disponibilidad"| MAT
    CIRC -->|"GET /usuarios/{id}/estado"| USR
    CIRC -->|"GET /usuarios/{id}/deuda-pendiente"| MULT
    CIRC -->|"POST /solicitudes-externas"| EXT

    %% Llamadas síncronas desde gateway a otros servicios
    GW --> MAT
    GW --> USR
    GW --> COB
    GW --> RES
    GW --> NOTI
    GW --> REP

    %% Publicación de eventos (asíncrono)
    CIRC -.->|"prestamo.registrado\nmaterial.devuelto\nprestamo.renovado\nrenovacion.rechazada"| RMQ
    MULT -.->|"multa.generada"| RMQ
    COB  -.->|"multa.pagada\nusuario.desbloqueo.peticion"| RMQ
    RES  -.->|"reserva.creada\nreserva.notificada\nreserva.expirada\nreserva.cancelada"| RMQ
    USR  -.->|"usuario.bloqueado.deuda"| RMQ

    %% Consumo de eventos (asíncrono)
    RMQ -.->|"material.devuelto"| MAT
    RMQ -.->|"material.devuelto"| MULT
    RMQ -.->|"material.devuelto"| RES
    RMQ -.->|"multa.generada"| COB
    RMQ -.->|"multa.pagada"| MULT
    RMQ -.->|"usuario.bloqueo.peticion\nusuario.desbloqueo.peticion"| USR
    RMQ -.->|"todos los eventos"| NOTI
    RMQ -.->|"prestamo.registrado\nmaterial.devuelto\nmulta.generada\nmulta.pagada"| REP

    %% Service Discovery
    CIRC & MAT & USR & MULT & COB & RES & EXT & NOTI & REP -.->|"registro"| EUR
```

---

## Diagrama 2a — Flujo: Registro de Préstamo

```mermaid
sequenceDiagram
    actor Cliente
    participant GW as API Gateway
    participant CIRC as circulacion-service
    participant USR as usuarios-service
    participant MAT as materiales-service
    participant EXT as prestamos-externos-service
    participant RMQ as RabbitMQ
    participant NOTI as notificaciones-service

    Cliente->>GW: POST /prestamos
    GW->>CIRC: POST /prestamos

    CIRC->>USR: GET /usuarios/{id}/estado
    USR-->>CIRC: EstadoUsuarioDTO (activo=true)

    CIRC->>MAT: GET /materiales/{id}/disponibilidad
    MAT-->>CIRC: DisponibilidadDTO (disponible=true)

    CIRC->>USR: GET /usuarios/{id}/limite-prestamos
    USR-->>CIRC: LimitePrestamoDTO (limiteMaximo=5)

    Note over CIRC: Crea Prestamo, calcula fechaDevolucion<br/>según tipoUsuario

    CIRC->>MAT: PUT /materiales/{id}/estado {estado: PRESTADO}
    MAT-->>CIRC: 200 OK

    alt tipoPrestamo == INTERBIBLIOTECARIO
        CIRC->>EXT: POST /solicitudes-externas
        EXT-->>CIRC: SolicitudExternaEntity
    end

    CIRC->>RMQ: publish prestamo.registrado
    RMQ-->>NOTI: prestamo.registrado
    Note over NOTI: Guarda Notificacion<br/>"Préstamo registrado correctamente"

    CIRC-->>GW: 201 ResultadoOperacion
    GW-->>Cliente: 201 Created
```

---

## Diagrama 2b — Flujo: Devolución con Multa y Bloqueo

```mermaid
sequenceDiagram
    actor Cliente
    participant CIRC as circulacion-service
    participant RMQ as RabbitMQ
    participant MAT as materiales-service
    participant MULT as multas-service
    participant COB as cobros-service
    participant USR as usuarios-service
    participant RES as reservas-service
    participant NOTI as notificaciones-service

    Cliente->>CIRC: POST /prestamos/{id}/devolucion {esUsable: false}

    Note over CIRC: prestamo.devolver(now)<br/>calcula diasRetraso

    CIRC->>MAT: PUT /materiales/{id}/estado {estado: EN_REPARACION}

    CIRC->>RMQ: publish material.devuelto\n(diasRetraso=14, esUsable=false)

    par Procesamiento paralelo de eventos
        RMQ-->>MULT: material.devuelto
        Note over MULT: diasRetraso>0 → crea MultaPorRetraso<br/>esUsable=false → crea MultaPorDano
        MULT->>RMQ: publish multa.generada (x2)

        RMQ-->>RES: material.devuelto
        Note over RES: Busca reserva en cola<br/>posicion==1, EN_ESPERA
        RES->>RMQ: publish reserva.notificada
    end

    RMQ-->>COB: multa.generada
    Note over COB: Registra DeudaPendiente

    RMQ-->>USR: multa.generada
    Note over USR: Evalúa umbral deuda >= 50.000<br/>→ bloquearPorDeuda()
    USR->>RMQ: publish usuario.bloqueado.deuda

    RMQ-->>NOTI: multa.generada + reserva.notificada\n+ usuario.bloqueado.deuda
    Note over NOTI: Guarda 3 notificaciones

    CIRC-->>Cliente: 200 ResultadoOperacion
```

---

## Diagrama 2c — Flujo: Pago de Multa y Desbloqueo

```mermaid
sequenceDiagram
    actor Cliente
    participant COB as cobros-service
    participant RMQ as RabbitMQ
    participant MULT as multas-service
    participant USR as usuarios-service (ACL)
    participant NOTI as notificaciones-service

    Cliente->>COB: POST /pagos {multaId, usuarioId, monto}

    Note over COB: RegistroPago.crear(multaId, usuarioId, monto)<br/>Marca DeudaPendiente como pagada

    COB->>RMQ: publish multa.pagada\n(multaId, montoPagado, fechaPago)

    RMQ-->>MULT: multa.pagada
    Note over MULT: multa.pagar(fechaPago)<br/>EstadoMulta → PAGADA

    Note over COB: Suma deudas restantes del usuario

    alt deudaTotal == 0
        COB->>RMQ: publish usuario.desbloqueo.peticion\n(usuarioId, origen="cobros-service")

        RMQ-->>USR: usuario.desbloqueo.peticion
        Note over USR: ACL: traduce PeticionDesbloqueo<br/>→ usuario.desbloquear()<br/>EstadoUsuario → ACTIVO

        USR->>RMQ: publish usuario.bloqueado.deuda\n(como confirmación de desbloqueo)
    end

    RMQ-->>NOTI: multa.pagada
    Note over NOTI: Guarda notificación\n"Multa pagada exitosamente"

    COB-->>Cliente: 201 RegistroPagoEntity
```

---

## Diagrama 2d — Flujo: Creación de Reserva y Notificación de Disponibilidad

```mermaid
sequenceDiagram
    actor Cliente
    participant RES as reservas-service
    participant RMQ as RabbitMQ
    participant NOTI as notificaciones-service
    participant CIRC as circulacion-service

    Cliente->>RES: POST /reservas {idUsuario, idMaterial, sede}

    Note over RES: Calcula posicion = activas + 1<br/>reserva.registrar(posicion)<br/>EmiteReservaCreada

    RES->>RMQ: publish reserva.creada (posicion=2)
    RMQ-->>NOTI: reserva.creada
    Note over NOTI: "Reserva en posición 2"

    RES-->>Cliente: 201 ReservaEntity

    Note right of CIRC: Más tarde: otro usuario devuelve el material

    CIRC->>RMQ: publish material.devuelto (materialId)

    RMQ-->>RES: material.devuelto
    Note over RES: Busca reserva posicion==1 EN_ESPERA<br/>reserva.notificarDisponibilidad(now)<br/>→ EstadoReserva = NOTIFICADA<br/>→ fechaExpiracion = now + 24h

    RES->>RMQ: publish reserva.notificada\n(reservaId, usuarioId, fechaExpiracion)
    RMQ-->>NOTI: reserva.notificada
    Note over NOTI: "Material disponible.\nTienes 24h para recogerlo."

    Note over RES: Pasadas 24h sin recogida...

    Note over RES: @Scheduled — LimpiezaReservasScheduler<br/>reserva.expirar()<br/>→ EstadoReserva = CANCELADA
    RES->>RMQ: publish reserva.expirada
    RMQ-->>NOTI: reserva.expirada
    Note over NOTI: "Tu reserva ha expirado."
```

---

## Diagrama 3 — Capas DDD: circulacion-service

```mermaid
graph LR
    subgraph Presentación
        CC["CirculacionController\nPOST /prestamos\nPOST /prestamos/{id}/devolucion\nPOST /prestamos/{id}/renovacion\nGET /prestamos"]
    end

    subgraph Aplicación
        CS["CirculacionService\nregistrarPrestamo()\nregistrarDevolucion()\nrenovarPrestamo()"]
        DTOs["DTOs\nRegistrarPrestamoRequest\nDevolverMaterialRequest\nResultadoOperacion\nDisponibilidadDTO\nEstadoUsuarioDTO\nLimitePrestamoDTO\nDeudaPendienteDTO"]
    end

    subgraph Dominio
        P["Prestamo (Agregado Raíz)\ncrear() — Factory Method\nreconstruir() — Reconstitución\nrenovar(fecha, maxRenovaciones)\ndevolver(fecha)\ncalcularDiasRetraso()\nisActivo()"]
    end

    subgraph Infraestructura
        subgraph Persistencia
            PE["PrestamoEntity\n@Entity prestamos\nfromDomain() / toDomain()"]
            REPO["PrestamoJpaRepository\nfindByIdUsuario()\nfindByIdMaterialAndEstado()\ncountByIdUsuarioAndEstado()"]
        end
        subgraph Mensajería
            EP["EventoPublisher\npublicarPrestamoRegistrado()\npublicarMaterialDevuelto()\npublicarPrestamoRenovado()\npublicarRenovacionRechazada()"]
            RMQCFG["RabbitMQConfig\nTopicExchange biblioteca.events\nJackson2JsonMessageConverter\nRabbitTemplate"]
        end
        subgraph Clientes["Clientes Feign (REST)"]
            MC["MaterialesClient\nGET /disponibilidad\nPUT /estado"]
            UC["UsuariosClient\nGET /estado\nGET /limite-prestamos"]
            MLC["MultasClient\nGET /deuda-pendiente"]
            EC["PrestamosExternosClient\nPOST /solicitudes-externas"]
        end
    end

    CC --> CS
    CS --> DTOs
    CS --> P
    CS --> PE
    CS --> REPO
    CS --> EP
    CS --> MC
    CS --> UC
    CS --> MLC
    CS --> EC
    PE --> P
    EP --> RMQCFG
```
