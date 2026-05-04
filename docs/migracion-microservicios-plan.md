# Plan de Migración: Elysium Monolito → Microservicios con DDD

## Estado del documento

| Campo | Valor |
|---|---|
| Fase actual | **M1–M6 COMPLETADAS** — todos los microservicios implementados |
| Próxima acción | Levantar con `docker-compose up --build` desde `microservicios/` |
| Prerequisito | Docker Desktop activo · `mvn clean install -DskipTests` en `microservicios/` para construir JARs antes del build de imágenes |

---

## Contexto del proyecto

**Elysium** es un sistema de gestión bibliotecaria universitaria implementado como monolito en Java 17 con Maven, sin framework web, sin base de datos real (todo en memoria), con punto de entrada en `Main.java` (Composition Root manual).

El objetivo es migrar a 9 microservicios usando DDD, Spring Boot 3.x, RabbitMQ como message broker y PostgreSQL como base de datos por servicio.

El documento DDD de referencia está en `docs/DDDElysium.pdf`.

---

## Fase 0 — Comprensión del monolito (COMPLETADA)

### Capas actuales

| Capa | Paquete | Clases clave |
|---|---|---|
| Presentación | `consola/` | `MenuConsola` |
| Fachada (Aplicación) | `servicios/` | `BibliotecaFacade`, `ConsultaFacade`, `AdministracionFacade` |
| Servicios | `servicios/implementaciones/` | `PrestamoService`, `DevolucionService`, `RenovacionService`, `ReservaService`, `GestorMultasService`, `GestorBloqueoService`, `ValidadorReglasService` |
| Dominio | `dominio/` | Entidades, value objects, estados, builders, factories, enums, excepciones |
| Infraestructura | `repositorios/` | 5 repositorios en memoria + `RepositorioEnMemoriaFactory` |

### Patrones implementados (NO modificar sin consulta)

| Patrón | Dónde |
|---|---|
| **Facade** | `BibliotecaFacade`, `ConsultaFacade`, `AdministracionFacade` |
| **Abstract Factory** | `IRepositorioFactory` / `RepositorioEnMemoriaFactory` |
| **Factory Method** | `IPrestamoFactory` / `PrestamoNormalFactory`, `PrestamoInterbibliotecarioFactory` |
| **Builder fluent** | `MaterialBuilder`, `UsuarioBuilder`, `PrestamoBuilder`, `DirectorCreacion`; también `ContextoMulta.Builder` |
| **Strategy** | `ICalculadorMulta` (3 impls), `IReglaValidacion` (5 impls) registradas dinámicamente |
| **Notification Pattern** | `ResultadoValidacion` — acumula múltiples errores de validación |
| **Parameter Object** | `ContextoMulta`, `ContextoValidacion`, `ContextoCreacionPrestamo` |
| **State** | `IEstadoPrestamo` → `PrestamoActivoState`, `PrestamoCompletadoState` |

### Patrones conscientemente descartados (NO reimplementar)

Template Method, Decorator, Adapter, Value Objects para IDs, Observer, Singleton, Command.

> `ConfiguracionBiblioteca` (Singleton existente en `dominio/config/`) fue **descartado** — ignorar esa clase.

### Problemas críticos identificados (a resolver en Fase 2)

1. **Dominio anémico**: `Prestamo` tiene `setFechaDevolucionReal()`, `incrementarRenovaciones()`; `Reserva` tiene `setPosicionCola()`; `Multa` tiene `setFechaPago()`. Ningún agregado protege invariantes propias.

2. **Calculadores con repositorios**: `CalculadorMultaPorRetraso` inyecta `IRepositorio<Prestamo>` y `IRepositorio<Usuario>`; `CalculadorMultaPorPerdida` inyecta `IRepositorio<Material>` y `IRepositorio<Usuario>`. Violan el principio de que los servicios de dominio operan sobre datos ya resueltos.

3. **BC4 y BC9 fusionados**: `GestorMultasService` mezcla cálculo de multas (BC4) con cobro/pago (BC9). Requieren separación antes de la extracción a microservicios.

4. **Sin infraestructura de eventos**: No existe `IDomainEvent`, no hay `List<IDomainEvent>` en agregados, no hay publicación de eventos. Todo el flujo es síncrono y acoplado por dependencias directas.

---

## Los 9 Bounded Contexts

| # | Nombre | Clasificación | Microservicio | Código actual |
|---|---|---|---|---|
| BC1 | Circulación Bibliográfica | **Core** | `circulacion-service` | `PrestamoService`, `DevolucionService`, `RenovacionService` |
| BC2 | Gestión de Materiales | Supporting | `materiales-service` | Entidades `Material/*`, `RepositorioMaterialEnMemoria` |
| BC3 | Gestión de Usuarios | Supporting | `usuarios-service` | Entidades `Usuario/*`, `GestorBloqueoService`, `LimitePorTipoUsuarioService` |
| BC4 | Cálculo de Multas | **Core** | `multas-service` | `GestorMultasService`, calculadores, entidades `Multa/*` |
| BC5 | Préstamos Interbibliotecarios | Supporting | `prestamos-externos-service` | `PrestamoInterbibliotecario`, `PrestamoInterbibliotecarioFactory` |
| BC6 | Notificaciones | Generic | `notificaciones-service` | `NotificacionEmailService`, `SistemaEmailExterno` |
| BC7 | Reportes y Estadísticas | Generic | `reportes-service` | `ServicioReportes` |
| BC8 | Gestión de Reservas | **Core** | `reservas-service` | `ReservaService`, entidades `Reserva/*` |
| BC9 | Cobro de Multas | Supporting | `cobros-service` | Flujo de pago en `GestorMultasService` + `GestorBloqueoService` |

### Agregados raíz por servicio

| Servicio | Agregado(s) | Nota |
|---|---|---|
| `circulacion-service` | `Prestamo` | `PrestamoNormal` y `PrestamoInterbibliotecario` como subtipos |
| `materiales-service` | `Material` | `Libro`, `DVD`, `Revista`, `EBook` |
| `usuarios-service` | `Usuario` | `Estudiante`, `Profesor`, `Investigador`, `PublicoGeneral` |
| `multas-service` | `Multa` | `MultaPorRetraso`, `MultaPorDano`, `MultaPorPerdida` |
| `cobros-service` | `RegistroPago` | No existe aún — debe crearse |
| `reservas-service` | `Reserva` | `ReservaNormal`, `ReservaInterbibliotecaria` |
| `notificaciones-service` | `Notificacion` | No existe aún — debe crearse |
| `prestamos-externos-service` | `SolicitudExterna` | No existe aún — debe crearse |

### Servicios de dominio (DDD objetivo vs. estado actual)

| Servicio de dominio | BC | Estado actual |
|---|---|---|
| `PoliticaRenovacion` | BC1 | Embebido en `RenovacionService` — no extraído |
| `PoliticaPlazo` | BC1 | `PoliticaTiempoPorTipoService` (existe como servicio de aplicación) |
| `GestorColaReservas` | BC8 | Parcialmente en `ReservaService` — `setPosicionCola()` externo |
| `GestorUmbralBloqueo` | BC4 | En `GestorBloqueoService`, mezclado con BC3 |
| `EvaluadorInfraccion` | BC4 | Distribuido entre `GestorMultasService` y `DevolucionService` |
| `PoliticaBloqueo` | BC9 | En `GestorBloqueoService`, acoplado a repo de usuarios |
| `PoliticaPlazoInterbibliotecario` | BC5 | Implícita: `+20 días` hardcodeados en constructor — debe formalizarse |

---

## Fase 1 — Plan de migración por fases (COMPLETADA — aprobado)

### 1.1 Decisiones arquitectónicas

| Decisión | Elección | Justificación |
|---|---|---|
| Message Broker | **RabbitMQ** con exchanges `topic` | Volumen universitario moderado; Kafka sería sobredimensionado |
| API Gateway | **Spring Cloud Gateway** | Routing, JWT, rate limiting, logging centralizado |
| Service Discovery | **Spring Cloud Eureka** | 9 servicios con posible escala horizontal |
| Base de datos | **PostgreSQL** por servicio | Excepto `notificaciones-service` → MongoDB |

### 1.2 Patrones de consistencia por flujo

#### Registro de préstamo — Orquestación síncrona (no Saga)
```
circulacion-service
  ├── GET usuarios-service/usuarios/{id}/estado          [síncrono]
  ├── GET materiales-service/materiales/{id}/disponibilidad [síncrono]
  └── si OK → persiste Prestamo → publica PrestamoRegistrado
```

#### Devolución con multa — Saga por coreografía
```
[1] circulacion-service   persiste → publica MaterialDevuelto
[2] materiales-service    consume MaterialDevuelto → actualiza EstadoMaterial
[3] multas-service        consume MaterialDevuelto → calcula → persiste Multa → publica MultaGenerada
[4] cobros-service        consume MultaGenerada → registra deuda en RegistroPago
[5] usuarios-service      consume MultaGenerada → si deuda >= 50.000 → publica UsuarioBloqueadoPorDeuda
[6] reservas-service      consume MaterialDevuelto → si hay cola → publica ReservaNotificada
[7] notificaciones-service consume MultaGenerada + ReservaNotificada + UsuarioBloqueadoPorDeuda
```

#### Pago de multa y desbloqueo — Saga por coreografía
```
[1] cobros-service        POST /pagos → persiste RegistroPago → publica MultaPagada
[2] multas-service        consume MultaPagada → marca Multa como PAGADA
[3] cobros-service        si deuda total == 0 → publica PeticionDesbloqueoUsuario
[4] usuarios-service ACL  consume PeticionDesbloqueoUsuario → desbloquea → publica UsuarioDesbloqueado
[5] notificaciones-service consume MultaPagada + UsuarioDesbloqueado
```

#### Anti-Corruption Layer en `usuarios-service`
`PeticionBloqueoUsuario` y `PeticionDesbloqueoUsuario` (vocabulario de BC9) son traducidos internamente por `SancionesTraductor` al comando `BloquearUsuarioPorDeuda(usuarioId, motivo)` del modelo de BC3. BC3 no expone un endpoint REST de bloqueo consumible directamente desde BC4 o BC9.

### 1.3 Fases de migración

| Fase | BCs | Servicios | Riesgo | Prerequisito |
|---|---|---|---|---|
| **M1** | BC6, BC7 | `notificaciones-service`, `reportes-service` | Bajo | Ninguno |
| **M2** | BC2, BC3 | `materiales-service`, `usuarios-service` | Medio | M1 completa |
| **M3** | BC4, BC9 | `multas-service`, `cobros-service` | **Alto** | **Fase 2 del dominio** |
| **M4** | BC8 | `reservas-service` | Medio | **Fase 2 del dominio**, M2 |
| **M5** | BC5 | `prestamos-externos-service` | Medio | M2, `PoliticaPlazoInterbibliotecario` formalizada |
| **M6** | BC1 | `circulacion-service` | Bajo | M1–M5 completas |

---

## Fase 2 — Enriquecimiento del dominio (PENDIENTE — próxima a implementar)

> Esta fase ocurre **dentro del monolito actual** antes de cualquier extracción de microservicio.

### 2.1 Agregado `Prestamo`

**Cambios**:
- Eliminar `setFechaDevolucionReal()` e `incrementarRenovaciones()` como setters públicos
- Crear `prestamo.renovar(nuevaFecha)`:
  - Precondición: `EstadoPrestamo == ACTIVO`, `renovacionesUsadas < límite`
  - Emite: `PrestamoRenovado`
- Crear `prestamo.devolver(fechaReal, evaluacion)`:
  - Precondición: `EstadoPrestamo` es ACTIVO o RENOVADO
  - Emite: `MaterialDevuelto`
- Introducir VOs: `PeriodoPrestamo`, `RenovacionesUsadas`, `PrestamoId`

**Clases afectadas**: `Prestamo.java`, `RenovacionService.java` (líneas 162–163), `DevolucionService.java` (línea 81)

### 2.2 Agregado `Reserva`

**Cambios**:
- Eliminar `setPosicionCola()`
- Crear `reserva.notificarDisponibilidad(fecha)`:
  - Precondición: `EstadoReserva == EN_ESPERA`, `posicion == 1`
  - Emite: `ReservaNotificada`
- Crear `reserva.expirar()`:
  - Precondición: `EstadoReserva == NOTIFICADA`, 24h transcurridas
  - Emite: `ReservaExpirada`
- Crear `reserva.cancelar()`:
  - Precondición: `EstadoReserva` es EN_ESPERA o NOTIFICADA
  - Emite: `ReservaCancelada`
- Introducir VOs: `PosicionCola`, `PeriodoNotificacion`, `ReservaId`

**Clases afectadas**: `Reserva.java`, `ReservaService.java`

### 2.3 Agregado `Multa`

**Cambios**:
- Eliminar `setFechaPago()` (actualmente en `Multa.java` línea 46)
- Crear `multa.pagar(fechaPago)`:
  - Precondición: `EstadoMulta == PENDIENTE`
  - Emite: `MultaPagada`
- Crear `multa.condonar()`:
  - Precondición: `EstadoMulta == PENDIENTE`
  - Emite: `MultaCondonada`
- Introducir VOs: `Dinero`, `MultaId`, `PorcentajeRecargo`

**Clases afectadas**: `Multa.java`, `GestorMultasService.java`

### 2.4 Calculadores de multa

**Cambios**:
- Eliminar `IRepositorio<Prestamo>` e `IRepositorio<Usuario>` de `CalculadorMultaPorRetraso`
- Eliminar `IRepositorio<Material>` e `IRepositorio<Usuario>` de `CalculadorMultaPorPerdida`
- Enriquecer `ContextoMulta` con: `diasRetraso`, `tipoUsuario`, `valorMaterial`
- Los datos se resuelven en el servicio de aplicación y se pasan al calculador

**Clases afectadas**: `CalculadorMultaPorRetraso.java`, `CalculadorMultaPorPerdida.java`, `ContextoMulta.java`, `DevolucionService.java`

### 2.5 Infraestructura de eventos de dominio

**Clases a crear**:

```
dominio/eventos/
├── IDomainEvent.java           (interface: occurredOn(), eventType(), aggregateId())
├── PrestamoRegistrado.java
├── MaterialDevuelto.java
├── PrestamoRenovado.java
├── RenovacionRechazada.java
├── MultaGenerada.java
├── MultaPagada.java
├── UsuarioBloqueadoPorDeuda.java
├── PeticionDesbloqueoUsuario.java
├── ReservaCreada.java
├── ReservaNotificada.java
├── ReservaExpirada.java
└── ReservaCancelada.java
```

**Mecánica**:
- Cada agregado raíz mantiene `List<IDomainEvent> domainEvents` (privado)
- Los eventos se añaden internamente al ejecutar comportamiento (`renovar`, `devolver`, etc.)
- El servicio de aplicación extrae los eventos con `prestamo.pullEvents()` tras persistir y los publica
- En el monolito, la publicación es síncrona (log + invocación directa); en microservicios, vía RabbitMQ

### 2.6 Tests requeridos por la Fase 2

Para cada invariante protegida, se requiere al menos un test unitario:

| Clase | Invariante a testear |
|---|---|
| `Prestamo` | `renovar()` lanza excepción si `EstadoPrestamo != ACTIVO` |
| `Prestamo` | `renovar()` lanza excepción si `renovacionesUsadas >= límite` |
| `Prestamo` | `devolver()` emite evento `MaterialDevuelto` |
| `Reserva` | `notificarDisponibilidad()` lanza excepción si `posicion != 1` |
| `Reserva` | `cancelar()` lanza excepción si estado es COMPLETADA |
| `Multa` | `pagar()` lanza excepción si `EstadoMulta != PENDIENTE` |
| `Multa` | `pagar()` emite evento `MultaPagada` |
| `CalculadorMultaPorRetraso` | Calcula monto correcto dado `diasRetraso` y `tipoUsuario` en contexto |

---

## Fase 3 — Extracción de microservicios (PENDIENTE)

### Estructura de paquetes estándar por servicio

```
{nombre-service}/
├── src/main/java/com/biblioteca/{bc}/
│   ├── dominio/
│   │   ├── entidades/
│   │   ├── valueobjects/
│   │   ├── eventos/
│   │   ├── servicios/
│   │   └── excepciones/
│   ├── aplicacion/
│   │   ├── comandos/
│   │   ├── queries/
│   │   ├── handlers/
│   │   └── dto/
│   ├── infraestructura/
│   │   ├── persistencia/
│   │   ├── mensajeria/
│   │   ├── api/
│   │   └── configuracion/
│   └── presentacion/
│       └── controladores/
├── src/main/resources/
│   └── application.yml
└── pom.xml
```

### Contratos de eventos de dominio (JSON)

Todos los eventos siguen esta estructura base:

```json
{
  "eventId": "uuid-v4",
  "eventType": "NombreDelEvento",
  "occurredOn": "2026-05-04T10:30:00Z",
  "aggregateId": "PRE-000001",
  "version": 1,
  "payload": { }
}
```

#### `MaterialDevuelto`
```json
{
  "payload": {
    "prestamoId": "PRE-000001",
    "materialId": "MAT-000002",
    "usuarioId": "USR-000003",
    "fechaDevolucion": "2026-05-04T10:30:00Z",
    "fechaDevolucionEsperada": "2026-04-20T10:30:00Z",
    "diasRetraso": 14,
    "evaluacion": {
      "esUsable": false,
      "danos": [{ "tipo": "PAGINAS_RASGADAS", "gravedad": "MODERADO" }]
    }
  }
}
```

#### `MultaGenerada`
```json
{
  "payload": {
    "multaId": "MUL-abc12345",
    "prestamoId": "PRE-000001",
    "usuarioId": "USR-000003",
    "tipoMulta": "POR_RETRASO",
    "montoTotal": 14000.0,
    "diasRetraso": 14,
    "tipoUsuario": "ESTUDIANTE"
  }
}
```

#### `PeticionBloqueoUsuario`
```json
{
  "payload": {
    "usuarioId": "USR-000003",
    "montoDeudaTotal": 64000.0,
    "origen": "cobros-service",
    "multasIncluidas": ["MUL-abc12345", "MUL-def67890"]
  }
}
```

#### `ReservaNotificada`
```json
{
  "payload": {
    "reservaId": "RES-000001",
    "usuarioId": "USR-000004",
    "materialId": "MAT-000001",
    "tituloMaterial": "Cien años de soledad",
    "fechaNotificacion": "2026-05-04T10:30:00Z",
    "fechaExpiracionReserva": "2026-05-05T10:30:00Z"
  }
}
```

---

## Reglas de trabajo para futuras sesiones

1. **No modificar los patrones ya implementados** (Facade, Abstract Factory, Factory Method, Builder, Strategy) sin justificación DDD explícita.
2. **No reimplementar los patrones descartados** (Template Method, Decorator, Adapter, Observer, Singleton, Command).
3. **Lenguaje ubicuo**: los nombres de clases, métodos y eventos deben coincidir con los términos del documento `DDDElysium.pdf`.
4. **Trazabilidad DDD**: cada clase o evento nuevo debe poder referenciarse a una sección del documento DDD.
5. **IDs como String**: los IDs son `String` planos (`"PRE-000001"`). No introducir `PrestamoId`, `UsuarioId` como tipos VO a menos que el usuario lo autorice explícitamente.
6. **Fase 2 es prerequisito de M3 y M4**: no iniciar extracción de `multas-service` ni `reservas-service` si el dominio sigue siendo anémico.
7. **Idempotencia en handlers**: cada handler de evento debe verificar el `eventId` antes de procesar para soportar reintentos desde la DLQ.
