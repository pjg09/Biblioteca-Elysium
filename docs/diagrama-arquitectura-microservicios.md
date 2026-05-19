# Diagrama de Arquitectura - Microservicios Biblioteca

## Comunicación Inter-Servicios

```mermaid
graph TB
    subgraph "Cliente"
        CLI["Cliente REST"]
    end
    
    subgraph "API Gateway"
        GW["Eureka Service Discovery<br/>Port 8761"]
    end
    
    subgraph "Servicios Core"
        US["👤 UsuariosService<br/>Port 8083"]
        MS["📚 MaterialesService<br/>Port 8082"]
        CS["🔄 CirculacionService<br/>Port 8081"]
        MuS["⚠️ MultasService<br/>Port 8084"]
    end
    
    subgraph "Servicios Soporte"
        RS["📋 ReservasService<br/>Port 8088"]
        CS2["💳 CobroService<br/>Port 8089"]
        PS["🌐 PrestamosExternos<br/>Port 8085"]
    end
    
    subgraph "Servicios Listeners"
        NS["📧 NotificacionesService<br/>Port 8086"]
        ReS["📊 ReportesService<br/>Port 8087"]
    end
    
    subgraph "Mensajería"
        RMQ["🐰 RabbitMQ<br/>Exchange: biblioteca.events<br/>Port 15672"]
    end
    
    CLI -->|"1. POST /prestamos"| CS
    
    CS -->|"2. GET /usuarios/{id}/estado [SYNC]"| US
    CS -->|"3. GET /materiales/{id}/disponibilidad [SYNC]"| MS
    CS -->|"4. GET /multas/usuario/{id}/deuda [SYNC]"| MuS
    CS -->|"5. POST /solicitudes-externas [SYNC]"| PS
    
    CS -->|"6. Publish: prestamo.registrado"| RMQ
    RS -->|"Publish: reserva.creada/notificada"| RMQ
    CS2 -->|"Publish: multa.pagada"| RMQ
    
    RMQ -->|"Listen: prestamo.registrado"| NS
    RMQ -->|"Listen: material.devuelto"| NS
    RMQ -->|"Listen: reserva.notificada"| NS
    
    RMQ -->|"Listen: All events"| ReS
    
    classDef critical fill:#ff6b6b,stroke:#c92a2a,color:#fff
    classDef important fill:#ffd43b,stroke:#f59f00,color:#000
    classDef support fill:#74c0fc,stroke:#1971c2,color:#fff
    classDef listener fill:#51cf66,stroke:#2b8a3e,color:#fff
    
    class CS critical
    class US,MS,MuS important
    class RS,CS2,PS support
    class NS,ReS listener
```

## Flujo de Registrar Préstamo (CRÍTICO - 🔴)

```mermaid
sequenceDiagram
    participant CLI as Cliente
    participant CS as CirculacionService
    participant US as UsuariosService
    participant MS as MaterialesService
    participant MuS as MultasService
    participant PS as PrestamosExternos
    participant DB as BD Prestamo
    participant RMQ as RabbitMQ

    CLI->>CS: POST /prestamos<br/>(usuario, material)
    
    Note over CS: ⏱️ SYNC CALLS START
    CS->>US: GET /usuarios/{id}/estado
    US-->>CS: EstadoUsuarioDTO
    
    CS->>MS: GET /materiales/{id}/disponibilidad
    MS-->>CS: DisponibilidadDTO
    
    CS->>MuS: GET /multas/usuario/{id}/deuda
    MuS-->>CS: DeudaPendienteDTO
    
    CS->>PS: POST /solicitudes-externas (si interbibliotecario)
    PS-->>CS: SolicitudExternaEntity
    Note over CS: ⏱️ SYNC CALLS END
    
    Note over CS: ✅ Validaciones OK → Crear Prestamo
    
    CS->>DB: save(PrestamoEntity)
    DB-->>CS: Saved
    
    CS->>RMQ: Publish: prestamo.registrado
    RMQ-->>CS: Ack
    
    CS-->>CLI: ResultadoOperacion(exito)
    
    Note over RMQ: 🔄 ASYNC - No bloquea cliente
    RMQ->>RMQ: Listeners async
```

## Arquitectura de Base de Datos

```mermaid
graph LR
    subgraph "Usuarios Service"
        UDB["usuarios_bd<br/>UsuarioEntity"]
    end
    
    subgraph "Materiales Service"
        MDB["materiales_bd<br/>MaterialEntity"]
    end
    
    subgraph "Circulacion Service"
        CDB["circulacion_bd<br/>PrestamoEntity"]
    end
    
    subgraph "Multas Service"
        MuDB["multas_bd<br/>MultaEntity"]
    end
    
    subgraph "Cobros Service"
        CoDBP["cobros_bd<br/>RegistroPagoEntity<br/>DeudaPendienteEntity"]
    end
    
    subgraph "Reservas Service"
        RDB["reservas_bd<br/>ReservaEntity"]
    end
    
    subgraph "Notificaciones Service"
        NDB["notificaciones_bd<br/>NotificacionEntity"]
    end
    
    subgraph "Reportes Service"
        ReDB["reportes_bd<br/>RegistroEstadisticaEntity"]
    end
    
    Note: Database per Service (Poliglota)
```

## Flujo de Eventos (RabbitMQ)

```mermaid
graph LR
    subgraph "Productores"
        CS["CirculacionService<br/>publica:<br/>- prestamo.registrado<br/>- material.devuelto"]
        RS["ReservaService<br/>publica:<br/>- reserva.creada<br/>- reserva.notificada<br/>- reserva.cancelada"]
        CoS["CobroService<br/>publica:<br/>- multa.pagada"]
    end
    
    subgraph "RabbitMQ"
        EX["Exchange:<br/>biblioteca.events<br/>type: topic"]
    end
    
    subgraph "Consumidores"
        NS["NotificacionesService<br/>queue: notificaciones.queue<br/>→ crea Notificacion"]
        ReS["ReportesService<br/>queue: reportes.queue<br/>→ crea RegistroEstadistica"]
    end
    
    CS -->|routing keys| EX
    RS -->|routing keys| EX
    CoS -->|routing keys| EX
    
    EX -->|binding| NS
    EX -->|binding| ReS
    
    style CS fill:#ff6b6b
    style RS fill:#74c0fc
    style CoS fill:#74c0fc
    style EX fill:#ffd43b
    style NS fill:#51cf66
    style ReS fill:#51cf66
```

## Patrón de Capas en Cada Servicio

```mermaid
graph TD
    subgraph "Presentación"
        CTRL["@RestController<br/>CirculacionController"]
    end
    
    subgraph "Aplicación"
        SVC["@Service<br/>CirculacionService<br/>(Orquestación)"]
        DTO["DTOs<br/>RegistrarPrestamoRequest<br/>ResultadoOperacion"]
    end
    
    subgraph "Dominio"
        AGG["Agregado Root<br/>Prestamo<br/>+ Factory methods"]
        SVCDOM["Servicios de Dominio<br/>(si aplica)"]
    end
    
    subgraph "Infraestructura"
        API["API REST Clients<br/>UsuariosClient<br/>MaterialesClient<br/>MultasClient"]
        PERS["Persistencia<br/>PrestamoEntity<br/>PrestamoJpaRepository"]
        MSG["Mensajería<br/>EventoPublisher<br/>RabbitMQ"]
    end
    
    CTRL -->|usa| SVC
    SVC -->|transforma| DTO
    SVC -->|crea/actualiza| AGG
    AGG -->|accede via| PERS
    SVC -->|llama| API
    SVC -->|publica| MSG
    
    style CTRL fill:#87ceeb
    style SVC fill:#87ceeb
    style AGG fill:#90ee90
    style PERS fill:#dda0dd
    style API fill:#ff6b6b
    style MSG fill:#ffd43b
```

## Estado de Madurez por Servicio

```mermaid
graph LR
    subgraph "Nivel 1: CRUD Basic"
        U["👤 Usuarios"]
        M["📚 Materiales"]
        PE["🌐 Préstamos Externos"]
    end
    
    subgraph "Nivel 2: CRUD + Lógica"
        Mu["⚠️ Multas<br/>(Strategy)"]
        C["💳 Cobros<br/>(Publisher)"]
    end
    
    subgraph "Nivel 3: Orquestador Complejo"
        CS["🔄 Circulación<br/>(5 sync calls)"]
    end
    
    subgraph "Nivel 4: Domain Model"
        R["📋 Reservas<br/>(Cola + Scheduler)"]
    end
    
    subgraph "Nivel 5: Event Listener"
        N["📧 Notificaciones"]
        Re["📊 Reportes"]
    end
    
    style U fill:#c3e7ff
    style M fill:#c3e7ff
    style PE fill:#c3e7ff
    style Mu fill:#fff4c3
    style C fill:#fff4c3
    style CS fill:#ffc3c3
    style R fill:#c3ffc3
    style N fill:#e7d4ff
    style Re fill:#e7d4ff
```

## Matriz de Dependencias Síncronas

```mermaid
graph LR
    CS["CirculacionService"]
    US["UsuariosService"]
    MS["MaterialesService"]
    MuS["MultasService"]
    PS["PrestamosExternos"]
    
    CS -->|sync| US
    CS -->|sync| MS
    CS -->|sync| MuS
    CS -->|sync| PS
    
    US -->|no dependencias| US
    MS -->|no dependencias| MS
    MuS -->|no dependencias| MuS
    PS -->|no dependencias| PS
    
    style CS fill:#ff6b6b,stroke:#c92a2a
    style US fill:#ffd43b
    style MS fill:#ffd43b
    style MuS fill:#ffd43b
    style PS fill:#74c0fc
    
    linkStyle 0,1,2,3 stroke:#ff6b6b,stroke-width:3px
```

## Problemas de Diseño Actuales

```
┌─────────────────────────────────────────────────────────────────┐
│                    ARQUITECTURA ACTUAL                           │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  🔴 CRÍTICO                                                     │
│  ├─ Sincronía cascada: CS → US → MS → MuS → PS                │
│  ├─ Sin Circuit Breaker → timeout = request bloqueado          │
│  ├─ Sin Saga pattern → transacciones inconsistentes            │
│  └─ Cascada de fallos → 1 servicio down = todo down            │
│                                                                  │
│  🟡 MODERADO                                                    │
│  ├─ Sin Facade pattern (organización interna débil)            │
│  ├─ State pattern incompleto (enums vs objetos)                │
│  ├─ Dominio débil en: RegistroPago, Notificacion              │
│  └─ RabbitMQ config dispersa (duplicación)                     │
│                                                                  │
│  🟢 MENOR                                                       │
│  ├─ Sin Builder pattern en DTOs                                │
│  ├─ Sin garantía de delivery en async (fire-forget)           │
│  └─ Logging distribuido faltante                               │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

## Mejoras Recomendadas (Roadmap)

```mermaid
gantt
    title Roadmap de Mejoras Arquitectónicas
    dateFormat YYYY-MM-DD
    
    section Week 1
    Circuit Breaker :crit, w1a, 2026-05-17, 1d
    Facade Pattern :w1b, 2026-05-17, 2d
    Config RabbitMQ Central :w1c, 2026-05-18, 1d
    
    section Week 2
    Saga Pattern :crit, w2a, 2026-05-24, 3d
    State Pattern Completo :w2b, 2026-05-24, 2d
    Mejorar Dominio Débil :w2c, 2026-05-25, 1d
    
    section Week 3
    Event Sourcing :w3a, 2026-05-31, 5d
    CQRS Separation :w3b, 2026-05-31, 3d
    Distributed Tracing :w3c, 2026-06-02, 2d
```

