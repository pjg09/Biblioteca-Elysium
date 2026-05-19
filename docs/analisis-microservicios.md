# Análisis de Estructura - Microservicios Biblioteca Elysium

**Fecha**: 17 de mayo de 2026  
**Visión General**: La arquitectura está en **fase inicial de migración DDD** con 9 servicios especializados + commons. Cada servicio sigue una estructura de capas (Aplicación, Dominio, Infraestructura) pero con patrones divergentes de implementación.

---

## 📋 Tabla Resumen Rápida

| Servicio | Controlador | Servicios | Patrón Predominante | Dominio |
|----------|-----------|-----------|-----------------|---------|
| **usuarios-service** | UsuarioController | UsuarioService | CRUD + DTOs | Usuario (simple) |
| **materiales-service** | MaterialController | MaterialService | CRUD + DTOs | Material (simple) |
| **circulacion-service** | CirculacionController | CirculacionService | Orquestador + Clientes | Prestamo (complejo) |
| **multas-service** | MultaController | MultaService | CRUD + Calculador | Multa + CalculadorMultaService |
| **cobros-service** | CobroController | CobroService | Eventos + RabbitMQ | RegistroPago (dominio débil) |
| **reservas-service** | ReservaController | ReservaService | Eventos + RabbitMQ | Reserva (con lógica cola) |
| **notificaciones-service** | NotificacionController | EventosBibliotecaHandler | Event Handler | Notificacion (reactive) |
| **prestamos-externos-service** | SolicitudExternaController | SolicitudExternaService | CRUD | SolicitudExterna (simple) |
| **reportes-service** | ReportesController | EventosEstadisticaHandler | Event Handler | RegistroEstadistica (log) |

**biblioteca-commons**: Enumeraciones + Eventos base (13 clases de eventos + IDomainEvent)

---

## 🔍 ANÁLISIS POR SERVICIO

### 1. **USUARIOS-SERVICE** (Puerto 8083)

#### Controladores REST
```
GET    /usuarios                      → listarTodos()
POST   /usuarios                      → registrar(CrearUsuarioRequest)
GET    /usuarios/{id}                 → obtenerPorId(String id)
GET    /usuarios/{id}/estado          → consultarEstado(String id)        [⭐ Sincrónico]
GET    /usuarios/{id}/limite-prestamos → consultarLimite(String id)       [⭐ Sincrónico]
```

#### Servicios/Clases de Negocio
- `UsuarioService` (aplicacion/):
  - CRUD básico contra `UsuarioJpaRepository`
  - Métodos clave `consultarEstado()` y `consultarLimite()` (consultados sincrónicamente por circulacion-service)
  - DTOs: `CrearUsuarioRequest`, `EstadoUsuarioDTO`, `LimitePrestamoDTO`

#### Entidad de Dominio
- `Usuario` (dominio/Usuario.java):
  - **Atributos**: id, nombre, email, tipoUsuario, estadoUsuario, limiteMaximoPrestamos
  - **Métodos de comportamiento**: `isActivo()`, `bloquearPorDeuda()`, `desbloquear()`
  - ✅ Agregado con lógica de negocio básica
  - ⚠️ Dominio débil: no tiene `reconstruir()` estático

#### Persistencia
- `UsuarioEntity` (infraestructura/persistencia/):
  - Entidad JPA directa (no existe mapping domain ↔ entity)
  - `UsuarioJpaRepository` (Spring Data JPA)

#### Patrones Detectados
- ❌ **NO hay Facade**
- ❌ **NO hay State, Strategy, Builder**
- ✅ DTO pattern (separación de representación)
- 🟡 **Dominio débil**: Usuario es poco más que un POJO

#### Criticidad Arquitectónica
- 🔴 **Punto de sincronía fuerte**: circulacion-service depende sincrónicamente de `/usuarios/{id}/estado` y `/limite-prestamos`
- Riesgo de cascadas de fallos

---

### 2. **MATERIALES-SERVICE** (Puerto 8082)

#### Controladores REST
```
GET    /materiales                   → listarTodos()
POST   /materiales                   → agregarMaterial(CrearMaterialRequest)
GET    /materiales/{id}              → obtenerPorId(String id)
GET    /materiales/{id}/disponibilidad → consultarDisponibilidad(String id)  [⭐ Sincrónico]
GET    /materiales/tipo/{tipo}       → listarPorTipo(String tipo)
PUT    /materiales/{id}/estado       → actualizarEstado(String id, estado)
```

#### Servicios/Clases de Negocio
- `MaterialService` (aplicacion/):
  - CRUD contra `MaterialJpaRepository`
  - `consultarDisponibilidad()` es sincrónico (llamado por circulacion-service)
  - DTOs: `CrearMaterialRequest`, `DisponibilidadDTO`

#### Entidad de Dominio
- `Material` (dominio/Material.java):
  - **Atributos**: id, titulo, autor, tipo (LIBRO_NORMAL | BESTSELLER | REFERENCIA | DVD | REVISTA | EBOOK), estado (DISPONIBLE | PRESTADO | RESERVADO | EN_REPARACION | PERDIDO), precio
  - **Métodos de comportamiento**: `marcarComoPrestado()`, `marcarComoDisponible()`, `marcarComoReservado()`, `marcarComoEnReparacion()`, `isDisponible()`
  - ✅ Agregado con lógica clara
  - ⚠️ No tiene `reconstruir()` estático

#### Persistencia
- `MaterialEntity` + `MaterialJpaRepository`
- Queries personalizadas: `findByTipo(String tipo)`

#### Patrones Detectados
- ❌ **NO hay Facade**
- ❌ **NO hay State, Strategy, Builder**
- ✅ DTO pattern
- 🟡 **Dominio simple**: buena lógica de cambios de estado

#### Criticidad Arquitectónica
- 🔴 **Punto de sincronía fuerte**: circulacion-service depende sincrónicamente de `/materiales/{id}/disponibilidad`

---

### 3. **CIRCULACION-SERVICE** (Puerto 8081) ⭐ **MÁS COMPLEJO**

#### Controladores REST
```
POST   /prestamos                    → registrarPrestamo(RegistrarPrestamoRequest)
PUT    /prestamos/{id}/devolver      → devolverMaterial(String id, DevolverMaterialRequest)
PUT    /prestamos/{id}/renovar       → renovarPrestamo(String id, RenovarRequest)
GET    /prestamos/{id}               → obtenerPorId(String id)
GET    /prestamos/usuario/{idUser}   → listarPorUsuario(String idUsuario)
```

#### Servicios/Clases de Negocio
- `CirculacionService` (aplicacion/):
  - **Orquestador central** que coordina múltiples clientes:
    - `MaterialesClient` → `/materiales/{id}/disponibilidad` [**Sincrónico - Riesgo**]
    - `UsuariosClient` → `/usuarios/{id}/estado` + `/limite-prestamos` [**Sincrónico - Riesgo**]
    - `MultasClient` → `/multas/usuario/{id}/deuda-pendiente` [**Sincrónico**]
    - `PrestamosExternosClient` → para interbibliotecarios [**Sincrónico**]
  - Método `registrarPrestamo()`: valida estado usuario → disponibilidad material → límite de préstamos → verifica deuda → crea préstamo → publica evento
  - DTOs: `RegistrarPrestamoRequest`, `DevolverMaterialRequest`, `ResultadoOperacion`, etc.
  - **Publica eventos** vía `EventoPublisher` (RabbitMQ)

#### Entidad de Dominio
- `Prestamo` (dominio/Prestamo.java):
  - **Atributos**: id, idUsuario, idMaterial, fechaPrestamo, fechaDevolucionEsperada, fechaDevolucionReal, renovacionesUsadas, estado (ACTIVO | COMPLETADO | CANCELADO), tipoPrestamo (NORMAL | INTERBIBLIOTECARIO), sede
  - **Métodos de comportamiento**: 
    - `crear()` (factory static)
    - `reconstruir()` (para persistencia) ✅ **BIEN HECHO**
    - `renovar(fechaNueva, maxRenovaciones)` con validaciones
    - `devolver(fecha)` con cambio de estado
    - `calcularDiasRetraso()`
    - `isActivo()`
  - ✅ **Agregado RICO**: protege invariantes, factory methods

#### Persistencia
- `PrestamoEntity` + `PrestamoJpaRepository`
- Queries: `findByIdUsuario()`, `findByEstado()`, etc.

#### Clientes Síncronos (Infraestructura)
- `MaterialesClient.consultarDisponibilidad(String id)` → GET /materiales/{id}/disponibilidad
- `UsuariosClient.consultarEstado(String id)` → GET /usuarios/{id}/estado
- `UsuariosClient.consultarLimite(String id)` → GET /usuarios/{id}/limite-prestamos
- `MultasClient.consultarDeudaPendiente(String id)` → GET /multas/usuario/{id}/deuda-pendiente
- `PrestamosExternosClient.crearSolicitud()` → POST para préstamos interbibliotecarios

#### Patrones Detectados
- ❌ **NO hay Facade explícito** (CirculacionService es de facto un orquestador/Facade)
- ❌ **NO hay State pattern** (Estados son strings, no objetos)
- ❌ **NO hay Strategy**
- ✅ **Factory pattern** en `Prestamo.crear()` y `Prestamo.reconstruir()`
- ✅ **Event-driven** parcial (publica eventos pero no es event sourcing)
- ❌ **Builder pattern**: No hay; podrían mejorar DTOs con builder

#### Problemas de Diseño Detectados
1. **Sincronía cascada peligrosa**: `registrarPrestamo()` hace N llamadas síncronas que pueden fallar
2. **Bajo acoplamiento entre servicios**: CirculacionService es fuertemente acoplado a Usuarios + Materiales
3. **Sin Circuit Breaker visible**: Los clientes no tienen reintentos ni fallback
4. **Eventos publicados pero no procesados uniformemente**: Solo algunos servicios escuchan

#### Criticidad Arquitectónica
- 🔴 **CRÍTICA**: Este servicio es el corazón de la arquitectura y tiene múltiples puntos de fallo sincrónico

---

### 4. **MULTAS-SERVICE** (Puerto 8084)

#### Controladores REST
```
GET    /multas/{id}                  → obtenerPorId(String id)
GET    /multas/usuario/{idUsuario}   → obtenerPorUsuario(String idUsuario)
GET    /multas/usuario/{id}/deuda    → consultarDeudaPendiente(String idUsuario)  [⭐ Sincrónico]
GET    /multas/prestamo/{id}         → obtenerPorPrestamo(String idPrestamo)
GET    /multas/usuario/{id}/estado   → obtenerPorUsuarioYEstado(String idUsuario, String estado)
```

#### Servicios/Clases de Negocio
- `MultaService` (aplicacion/):
  - CRUD contra `MultaJpaRepository`
  - `consultarDeudaPendiente()` consultado sincrónicamente por circulacion-service
  - DTOs: `DeudaPendienteDTO`

- `CalculadorMultaService` (dominio/):
  - **Implementa Strategy pattern** de cálculo de multas:
    - `calcularMontoRetraso(diasRetraso, tipoUsuario)` 
    - `calcularMontoPerdida(valorMaterial, tipoUsuario)` 
    - `calcularMontoDano(valorMaterial, gravedad)`
  - Lógica de multiplicadores por tipo de usuario (ESTUDIANTE, PROFESOR, INVESTIGADOR, PUBLICO_GENERAL)
  - ✅ **Strategy pattern bien implementado**

#### Entidad de Dominio
- `Multa` (dominio/Multa.java):
  - **Atributos**: id, idPrestamo, idUsuario, tipoMulta, montoTotal, estado (PENDIENTE | PAGADA | CONDONADA), fechaGeneracion, fechaPago, motivo
  - **Métodos de comportamiento**: 
    - `esPendiente()`
    - `pagar(fecha)` con validación de estado
    - `condonar()` con validación de estado
  - ✅ **Agregado con invariantes protegidas**
  - ⚠️ No tiene `reconstruir()` estático para persistencia

#### Persistencia
- `MultaEntity` + `MultaJpaRepository`
- Queries personalizadas: `findByIdUsuario()`, `findByIdUsuarioAndEstado()`, `findByIdPrestamo()`

#### Patrones Detectados
- ❌ **NO hay Facade**
- ✅ **Strategy pattern**: `CalculadorMultaService` implementa distintas estrategias de cálculo
- ❌ **NO hay State, Builder**
- ✅ DTO pattern

#### Criticidad Arquitectónica
- 🔴 **Punto de sincronía**: circulacion-service consulta deuda pendiente antes de crear préstamo
- 🟡 **Acoplamiento fuerte**: MultaService accede directamente al repositorio sin abstracción adicional

---

### 5. **COBROS-SERVICE** (Puerto 8089)

#### Controladores REST
```
POST   /cobros                       → registrarPago(RegistrarPagoRequest)
GET    /cobros/{id}                  → obtenerPorId(String id)
GET    /cobros/usuario/{id}          → obtenerPorUsuario(String idUsuario)
PUT    /cobros/{id}/estado           → actualizarEstado(String id, nuevoEstado)
```

#### Servicios/Clases de Negocio
- `CobroService` (aplicacion/):
  - Coordina: `RegistroPagoJpaRepository` + `DeudaPendienteJpaRepository`
  - `registrarPago()`: 
    1. Crea registro de pago
    2. Publica evento `multa.pagada` a RabbitMQ
    3. Marca deuda como pagada en `DeudaPendienteEntity`
    4. Calcula deuda restante
  - ✅ **Event publisher pattern** bien implementado

#### Entidad de Dominio
- `RegistroPago` (dominio/RegistroPago.java):
  - **Atributos**: id, multaId, usuarioId, monto, fechaPago
  - **Métodos**: `crear()` (factory)
  - ⚠️ **Dominio muy débil**: solo wrapper de datos

#### Persistencia
- `RegistroPagoEntity` + `DeudaPendienteEntity`
- `RegistroPagoJpaRepository` + `DeudaPendienteJpaRepository`
- Query: `findByUsuarioIdAndPagado()`

#### Mensajería
- **RabbitMQ integration**:
  - Exchange: `biblioteca.events` (configurado en `RabbitMQConfig`)
  - Routing key: `multa.pagada`
  - Publica evento con: multaId, usuarioId, montoPagado, fechaPago

#### Patrones Detectados
- ❌ **NO hay Facade**
- ✅ **Event publisher pattern**
- ❌ **NO hay State, Strategy, Builder**
- 🟡 **Dominio débil**: `RegistroPago` es casi anémico

#### Criticidad Arquitectónica
- 🟡 **Acoplamiento a dos repositorios**: debe mantener sincronía entre `RegistroPago` y `DeudaPendiente`
- 🟡 **Dependencia de RabbitMQ**: si falla el mensaje, puede perder notificación de pago

---

### 6. **RESERVAS-SERVICE** (Puerto 8088)

#### Controladores REST
```
POST   /reservas                     → crearReserva(CrearReservaRequest)
GET    /reservas/{id}                → obtenerPorId(String id)
GET    /reservas/material/{id}       → obtenerPorMaterial(String idMaterial)
GET    /reservas/usuario/{id}        → obtenerPorUsuario(String idUsuario)
PUT    /reservas/{id}/notificar      → notificarDisponibilidad(String id)
PUT    /reservas/{id}/cancelar       → cancelarReserva(String id)
```

#### Servicios/Clases de Negocio
- `ReservaService` (aplicacion/):
  - Gestión de cola de reservas
  - `crearReserva()`: 
    - Calcula posición = total de reservas activas + 1
    - Persiste reserva en estado `EN_ESPERA`
    - Publica evento `reserva.creada`
  - Métodos de gestión: `notificarDisponibilidad()`, `cancelarReserva()`
  - ✅ **Event publisher pattern**

- `LimpiezaReservasScheduler` (aplicacion/):
  - ✅ **Scheduler pattern**: limpia reservas expiradas
  - Cron job para `expirar()` reservas

#### Entidad de Dominio
- `Reserva` (dominio/Reserva.java):
  - **Atributos**: id, idUsuario, idMaterial, posicionCola, estadoReserva (EN_ESPERA | NOTIFICADA | COMPLETADA | CANCELADA), estadoTransaccion, fechaReserva, fechaNotificacion, sede
  - **Métodos de comportamiento**:
    - `registrar(posicion)`
    - `cancelar()`
    - `notificarDisponibilidad(fecha)`
    - `expirar()`
  - ✅ **Agregado con lógica de máquina de estados**
  - ⚠️ Doble enum de estados: `estadoReserva` + `estadoTransaccion` (confuso)

#### Persistencia
- `ReservaEntity` + `ReservaJpaRepository`
- Queries: `findByIdMaterialAndEstadoReservaIn()`, `findByIdUsuario()`, etc.

#### Mensajería
- Publica eventos:
  - `reserva.creada`
  - `reserva.notificada`
  - `reserva.cancelada`
  - `reserva.expirada`

#### Patrones Detectados
- ❌ **NO hay Facade explícito**
- 🟡 **State pattern incompleto**: usa enums `EstadoReserva` pero no implementa `IEstadoPrestamo`
- ✅ **Scheduler pattern**: `LimpiezaReservasScheduler`
- ✅ **Event publisher pattern**
- ❌ **NO hay Strategy, Builder**

#### Criticidad Arquitectónica
- 🟡 **Lógica de cola duplicada**: cada vez que crea reserva recalcula posición (N+1)
- 🟡 **Estados confusos**: `estadoReserva` (reserva lifecycle) vs `estadoTransaccion` (¿para qué?)

---

### 7. **NOTIFICACIONES-SERVICE** (Puerto 8086)

#### Controladores REST
```
GET    /notificaciones               → obtenerNotificaciones(usuarioId, noLeidas?)
PUT    /notificaciones/{id}/leer     → marcarLeida(String id)
```

#### Servicios/Clases de Negocio
- `EventosBibliotecaHandler` (aplicacion/):
  - ✅ **Event handler pattern**: escucha en RabbitMQ
  - `@RabbitListener(queues = "notificaciones.queue")`
  - Procesa eventos de dominio y crea notificaciones

#### Entidad de Dominio
- `Notificacion` (dominio/Notificacion.java):
  - **Atributos**: id, usuarioId, tipo, titulo, mensaje, leida, fechaCreacion
  - **Métodos**: `marcarLeida()`
  - ⚠️ **Dominio muy simple**: más un log que agregado

#### Persistencia
- `Notificacion` (es entidad JPA directa)
- `NotificacionRepository` (Spring Data)
- Queries: `findByUsuarioIdAndLeida()`, `findByUsuarioIdOrderByFechaCreacionDesc()`

#### Mensajería
- **Consumer RabbitMQ**:
  - Queue: `notificaciones.queue`
  - Escucha eventos como `PrestamoRegistrado`, `MaterialDevuelto`, `ReservaNotificada`, etc.
  - Extrae datos y crea registros `Notificacion`

#### Patrones Detectados
- ✅ **Event handler pattern**: reactive a eventos
- ❌ **NO hay Facade, State, Strategy, Builder**
- 🟡 **Dominio anémico**: Notificacion es mainly data holder

#### Criticidad Arquitectónica
- 🟢 **Baja criticidad**: servicio pasivo, no bloquea a nadie
- 🟡 **Sin garantía de delivery**: si falla durante procesar evento, puede perder notificaciones

---

### 8. **PRESTAMOS-EXTERNOS-SERVICE** (Puerto 8085)

#### Controladores REST
```
POST   /solicitudes-externas         → crearSolicitud(CrearSolicitudRequest)
GET    /solicitudes-externas/{id}    → obtenerPorId(String id)
GET    /solicitudes-externas/prestamo/{prestamoId} → obtenerPorPrestamo(String prestamoId)
PUT    /solicitudes-externas/{id}/autorizar → autorizarSolicitud(String id)
PUT    /solicitudes-externas/{id}/rechazar → rechazarSolicitud(String id)
```

#### Servicios/Clases de Negocio
- `SolicitudExternaService` (aplicacion/):
  - CRUD para solicitudes de interbibliotecario
  - `crearSolicitud()`: coordinado sincronicamente desde `CirculacionService` cuando `tipoPrestamo = INTERBIBLIOTECARIO`
  - DTOs: `CrearSolicitudRequest`

#### Entidad de Dominio
- `SolicitudExterna` (dominio/SolicitudExterna.java):
  - **Atributos**: id, prestamoId, idUsuario, idMaterial, bibliotecaOrigen, bibliotecaDestino, costoTransporte, estado
  - **Métodos**: `crear()` (factory)
  - ⚠️ **Dominio débil**: wrapper de datos

#### Persistencia
- `SolicitudExternaEntity` + `SolicitudExternaJpaRepository`

#### Patrones Detectados
- ❌ **NO hay Facade, State, Strategy, Builder**
- ✅ Factory pattern en `SolicitudExterna.crear()`

#### Criticidad Arquitectónica
- 🟡 **Acoplamiento síncrono a circulacion-service**: se llama directamente desde registrar préstamo
- 🟢 **Baja lógica de negocio**: principalmente CRUD

---

### 9. **REPORTES-SERVICE** (Puerto 8087)

#### Controladores REST
```
GET    /reportes                     → obtenerTodos()
GET    /reportes/{id}                → obtenerPorId(String id)
GET    /reportes/tipo/{tipo}         → obtenerPorTipo(String tipo)
GET    /reportes/estadisticas        → generarEstadisticas()
```

#### Servicios/Clases de Negocio
- `EventosEstadisticaHandler` (aplicacion/):
  - ✅ **Event handler pattern**: escucha eventos en RabbitMQ
  - `@RabbitListener(queues = "reportes.queue")`
  - Procesa: `PrestamoRegistrado`, `MaterialDevuelto`, `MultaGenerada`, `MultaPagada`, `ReservaNotificada`, etc.
  - Construye registros de estadística desde eventos

#### Entidad de Dominio
- `RegistroEstadistica` (dominio/RegistroEstadistica.java):
  - **Atributos**: tipo (PRESTAMO | DEVOLUCION | MULTA | PAGO | RESERVA), descripcion, valor (numérico para agregaciones)
  - ⚠️ **Dominio anémico**: es un log/audit trail

#### Persistencia
- `RegistroEstadistica` (entidad JPA)
- `EstadisticaRepository` (Spring Data)

#### Mensajería
- **Consumer RabbitMQ**:
  - Queue: `reportes.queue`
  - Procesa eventos y crea registros de estadística

#### Patrones Detectados
- ✅ **Event handler pattern**
- 🟡 **Event sourcing incompleto**: solo escucha, no reconstruye estado desde eventos
- ❌ **NO hay Facade, State, Strategy, Builder**

#### Criticidad Arquitectónica
- 🟢 **Baja criticidad**: servicio de lectura, reporting only
- 🟢 **Sin bloqueos**: puede fallar sin afectar operaciones críticas

---

## 📚 BIBLIOTECA-COMMONS

### Contenido
```
enumeraciones/
  ├─ TipoMaterial.java      (LIBRO_NORMAL, BESTSELLER, REFERENCIA, DVD, REVISTA, EBOOK)
  ├─ TipoUsuario.java       (ESTUDIANTE, PROFESOR, INVESTIGADOR, PUBLICO_GENERAL)
  └─ TipoMulta.java         (RETRASO, PERDIDA, DAÑO)

eventos/
  ├─ IDomainEvent.java      (interfaz base)
  ├─ PrestamoRegistrado.java
  ├─ MaterialDevuelto.java
  ├─ PrestamoRenovado.java
  ├─ RenovacionRechazada.java
  ├─ MultaGenerada.java
  ├─ MultaPagada.java
  ├─ MultaCondonada.java
  ├─ UsuarioBloqueadoPorDeuda.java
  ├─ PeticionDesbloqueoUsuario.java
  ├─ ReservaCreada.java
  ├─ ReservaNotificada.java
  ├─ ReservaCancelada.java
  └─ ReservaExpirada.java
```

### Descripción
- ✅ **Depósito centralizado de eventos de dominio** (13 clases + interfaz)
- ✅ **Enumeraciones compartidas** por todos los servicios
- 🟡 **Sin implementación de EventBus o EventPublisher** (cada servicio maneja RabbitMQ independientemente)
- 🟡 **Sin Value Objects compartidos** (ej: Resultado<T>, Evaluacion)
- 🟡 **Sin DTOs compartidos** (cada servicio define sus propios)

---

## 🏗️ PATRÓN DE DISEÑO POR SERVICIO

### Matriz de Patrones Implementados

| Patrón | Usuarios | Materiales | Circulación | Multas | Cobros | Reservas | Notif | ExtPrest | Reportes |
|--------|----------|-----------|------------|--------|--------|----------|-------|----------|----------|
| **Facade** | ❌ | ❌ | 🟡* | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ |
| **State** | ❌ | ❌ | ❌ | ❌ | ❌ | 🟡 | ❌ | ❌ | ❌ |
| **Strategy** | ❌ | ❌ | ❌ | ✅ | ❌ | ❌ | ❌ | ❌ | ❌ |
| **Builder** | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ |
| **Factory** | ❌ | ❌ | ✅ | ❌ | ✅ | ✅ | ❌ | ✅ | ❌ |
| **Repository** | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| **DTO** | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ❌ | ✅ | ❌ |
| **Event Handler** | ❌ | ❌ | ✅ | ❌ | ✅ | ✅ | ✅ | ❌ | ✅ |

**Legend**: ✅ = Implementado bien | 🟡 = Parcial/Incompleto | ❌ = No implementado | *CirculacionService es un orquestador

---

## 🔌 COMUNICACIÓN INTER-SERVICIOS

### Sincrónica (REST + OpenFeign)

```
CirculacionService
  ├─→ MaterialesClient.consultarDisponibilidad()     [GET /materiales/{id}/disponibilidad]
  ├─→ UsuariosClient.consultarEstado()              [GET /usuarios/{id}/estado]
  ├─→ UsuariosClient.consultarLimite()              [GET /usuarios/{id}/limite-prestamos]
  ├─→ MultasClient.consultarDeudaPendiente()        [GET /multas/usuario/{id}/deuda]
  └─→ PrestamosExternosClient.crearSolicitud()      [POST /solicitudes-externas]
```

**⚠️ Problema**: 5 llamadas síncronas consecutivas en `registrarPrestamo()` = cascada de fallos

### Asincrónica (RabbitMQ)

**Exchange**: `biblioteca.events` (tipo: topic)

**Productores** (publican eventos):
- `CirculacionService` → `PrestamoRegistrado`, `MaterialDevuelto`
- `ReservaService` → `ReservaCreada`, `ReservaNotificada`, `ReservaCancelada`, `ReservaExpirada`
- `CobroService` → `MultaPagada`
- (Otros servicios podrían publicar pero no lo hacen actualmente)

**Consumidores** (escuchan eventos):
- `EventosBibliotecaHandler` (notificaciones-service) → genera `Notificacion` en BD
- `EventosEstadisticaHandler` (reportes-service) → genera `RegistroEstadistica` en BD

**Routing keys** (utilizados):
```
prestamo.registrado          → PrestamoRegistrado
material.devuelto            → MaterialDevuelto
reserva.creada              → ReservaCreada
reserva.notificada          → ReservaNotificada
reserva.cancelada           → ReservaCancelada
reserva.expirada            → ReservaExpirada
multa.pagada                → MultaPagada
```

---

## 🚨 PROBLEMAS DETECTADOS

### 1. **Sincronía Peligrosa** (CRÍTICO)
- `registrarPrestamo()` hace 4-5 llamadas REST síncronas seguidas
- Si cualquiera falla → toda la operación falla
- **Sin Circuit Breaker, retry, timeout explícitos**
- **Sin fallback policies**

### 2. **Falta de Facade Pattern** (MODERADO)
- Ningún servicio implementa Facade real
- `CirculacionService` actúa como orquestador pero no hay abstracción de interfaz
- Cada controlador conecta directamente con servicios

### 3. **State Pattern Incompleto** (MODERADO)
- `Reserva` usa enum `EstadoReserva` pero no implementa `IEstadoPrestamo` (del backend monolítico)
- `Prestamo` usa strings para estado, no objetos
- Inconsistencia con SOLID principles

### 4. **Builder Pattern Ausente** (MENOR)
- DTOs complejos creados manualmente (sin builder)
- `RegistrarPrestamoRequest` podría ser más legible con builder

### 5. **Dominio Débil en Varios Servicios** (MODERADO)
- `RegistroPago`: casi anémico, solo datos
- `Notificacion`: log-like, no agregado real
- `RegistroEstadistica`: audit trail, sin lógica

### 6. **Duplicación de Lógica** (MENOR)
- `consultarDisponibilidad()` en `MaterialService` + consulta sincrónica
- `consultarEstado()` en `UsuarioService` + consulta sincrónica
- Podrían combinarse o tener una única responsabilidad

### 7. **Acoplamiento a RabbitMQ** (MODERADO)
- `EventoPublisher` acoplado a implementación específica de mensajería
- Si se cambia a Kafka, todos los servicios se ven afectados
- Debería haber abstracción

### 8. **Sin Evento Sourcing Real** (MENOR)
- Reportes-service escucha eventos pero no reconstruye estado
- Eventos se usan para audit trail, no para replicación de estado

### 9. **Configuración Dispersa de RabbitMQ** (MENOR)
- Exchange, queues, routing keys no centralizados
- Cada servicio define sus propios (duplicación)

### 10. **Falta de Saga Pattern** (CRÍTICO para futuro)
- `registrarPrestamo()` es una transacción distribuida implícita
- Si usuario se bloquea después de crear préstamo, hay inconsistencia
- Debería usar Saga pattern para compensación

---

## ✅ FORTALEZAS

1. ✅ **Separación de capas clara**: Aplicación, Dominio, Infraestructura
2. ✅ **Agregados con identidad**: Prestamo, Reserva, Multa, Usuario, Material
3. ✅ **Factory methods**: `Prestamo.crear()`, `Prestamo.reconstruir()`
4. ✅ **Event-driven parcial**: RabbitMQ para comunicación asincrónica
5. ✅ **DTOs para separación**: APIs exponen DTOs, no entidades
6. ✅ **Scheduler pattern**: `LimpiezaReservasScheduler` para tareas cron
7. ✅ **Strategy pattern en MultaService**: `CalculadorMultaService` permite extensión
8. ✅ **Queries personalizadas**: Spring Data JPA queries bien diseñadas

---

## 🎯 RECOMENDACIONES INMEDIATAS

### Corto Plazo (Semanas 1-2)
1. **Agregar patrón Facade** en cada servicio que coordine internamente
2. **Implementar Circuit Breaker** para llamadas síncronas (usar Spring Cloud Resilience4j)
3. **Agregar timeouts explícitos** a clientes REST
4. **Centralizar configuración RabbitMQ** en biblioteca-commons

### Mediano Plazo (Semanas 3-4)
1. **Implementar Saga pattern** para `registrarPrestamo()` (compensación)
2. **Agregar State pattern completo**: implementar interfaces `IEstadoPrestamo`, etc.
3. **Mejorar dominio débil**: enriquecer `RegistroPago`, `Notificacion`
4. **Agregar Builder pattern** a DTOs complejos

### Largo Plazo (Mes+)
1. **Considerar Event Sourcing real**: guardar eventos, no snapshot
2. **Migrar de REST sincrónico a async**: usar completable futures o reactive
3. **Implementar CQRS**: separar lectura (reportes) de escritura (operaciones)
4. **Agregar transacciones distribuidas**: Eventuate, Axon, etc.

---

## 📊 TABLA RESUMEN DE SOLIDEZ ARQUITECTÓNICA

| Aspecto | Calificación | Observación |
|---------|-------------|------------|
| **Separación de capas** | ✅ Excelente | Cada servicio tiene Aplicación, Dominio, Infraestructura |
| **Cohesión de agregados** | ✅ Buena | Prestamo, Reserva, Multa tienen lógica clara |
| **Acoplamiento** | 🔴 Crítico | Sincronía cascada en registrarPrestamo() |
| **Comunicación async** | 🟡 Parcial | RabbitMQ usado, pero solo para audit |
| **Resiliencia** | 🔴 Débil | Sin circuit breaker, retry, timeout |
| **Patrones DDD** | 🟡 Incompleto | Value Objects ausentes, Facade falta |
| **Patrones GoF** | 🟡 Disperso | Solo Strategy en Multas, Factory en algunos |
| **Testabilidad** | 🟡 Media | Clientes síncronos difíciles de mockear |
| **Escalabilidad** | 🟡 Limitada | Sincronía bloquea threads |
| **Mantenibilidad** | 🟡 Media | Código claro pero inconsistencias |

**Puntuación Global**: 6/10 - Buena base arquitectónica, pero necesita hardening para producción

---

## 🔍 CONCLUSIÓN

Los microservicios están en **fase inicial sólida** con separación clara de capas y dominio emergente. Sin embargo, el **acoplamiento sincrónico es crítico** y debe resolverse antes de ir a producción. La arquitectura beneficiaría de:

1. **Patrón Facade** en cada servicio
2. **Saga pattern** para transacciones distribuidas
3. **Circuit Breaker** en clientes REST
4. **Event Sourcing** o al menos garantía de delivery en RabbitMQ

La base está bien; falta **hardening y resilience**.

