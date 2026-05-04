# Prompt para Claude Code — Migración de Elysium a Microservicios con DDD

---

## Rol y contexto

Actúa como un arquitecto de software senior especializado en Domain-Driven Design (DDD), migración de monolitos a microservicios, y refactorización de código Java. Tienes experiencia probada en Spring Boot, comunicación basada en eventos, patrones de integración empresarial, y diseño por contrato.

Voy a proporcionarte el código fuente completo de **Elysium**, un sistema de gestión bibliotecaria universitaria actualmente implementado como monolito en Java 17 con Maven. También recibirás el documento DDD completo que describe los 9 bounded contexts, agregados, eventos de dominio, servicios de dominio, lenguaje ubicuo, y lógica de dominio con diseño por contrato (precondiciones/invariantes/postcondiciones).

---

## Fase 0 — Validación de comprensión (obligatoria antes de cualquier cambio)

Antes de proponer o escribir cualquier código, **confirma que has comprendido el proyecto** respondiendo con un resumen estructurado que incluya:

1. **Estructura actual del monolito**: capas identificadas (presentación, aplicación, dominio, infraestructura), punto de entrada, Composition Root en `Main.java`.
2. **Patrones de diseño implementados**: Facade (3 fachadas), Abstract Factory (repositorios), Factory Method (préstamos), Builder fluent (materiales, usuarios, préstamos), Strategy (validación de reglas, cálculo de multas, costo de daño), Notification Pattern (`ResultadoValidacion`), Parameter Object (contextos).
3. **Patrones conscientemente descartados**: Template Method, Decorator, Adapter, Value Objects para IDs, Observer, Singleton, Command. **No reimplementes ninguno de estos sin consultar**.
4. **Los 9 Bounded Contexts** con su clasificación (Core/Supporting/Generic):
   - BC1: Circulación Bibliográfica (Core)
   - BC2: Gestión de Materiales (Supporting)
   - BC3: Gestión de Usuarios (Supporting)
   - BC4: Cálculo de Multas (Core)
   - BC5: Préstamos Interbibliotecarios (Supporting)
   - BC6: Notificaciones (Generic)
   - BC7: Reportes y Estadísticas (Generic)
   - BC8: Gestión de Reservas (Core)
   - BC9: Cobro de Multas (Supporting)
5. **Agregados raíz**: Prestamo (BC1), Material (BC2), Usuario (BC3), Multa (BC4), SolicitudExterna (BC5), Notificacion (BC6), Reserva (BC8), RegistroPago (BC9).
6. **Eventos de dominio** definidos y sus consumidores principales.
7. **Servicios de dominio**: PoliticaRenovacion (BC1), PoliticaPlazo (BC1), GestorColaReservas (BC8), GestorUmbralBloqueo (BC4), EvaluadorInfraccion (BC4), PoliticaBloqueo (BC9), PoliticaPlazoInterbibliotecario (BC5).

**No procedas a la Fase 1 hasta que yo confirme que tu resumen es correcto.**

---

## Fase 1 — Plan de migración por fases

### 1.1. Mapeo BC → Microservicio

Propón la distribución exacta de microservicios basada en los 9 bounded contexts. Para cada microservicio:

- **Nombre del servicio** (ej. `circulacion-service`, `multas-service`)
- **BC(s) que contiene** y justificación si se fusionan dos BCs en uno
- **Agregado(s) raíz** que gestiona
- **Base de datos propia** (tipo sugerido: PostgreSQL, MongoDB, etc.)
- **API pública** (endpoints REST principales)
- **Eventos que publica** y **eventos que consume**
- **Comunicación síncrona** necesaria (queries a otros servicios)

### 1.2. Decisiones arquitectónicas

Para cada decisión, justifica con referencia al documento DDD:

- **Message broker**: tecnología sugerida (RabbitMQ, Kafka, etc.) y por qué.
- **API Gateway**: si aplica, qué responsabilidades delega.
- **Service Discovery**: necesario o no según el tamaño del sistema.
- **Consistencia transaccional**: patrón Saga (orquestación vs. coreografía) para los flujos que cruzan BCs, específicamente:
  - Registro de préstamo (BC1 consulta BC2 y BC3 síncronamente)
  - Devolución con multa (BC1 emite MaterialDevuelto → BC4 genera multa → BC9 evalúa bloqueo → BC3 bloquea usuario)
  - Pago de multa y desbloqueo (BC9 → BC3)
  - Reserva y notificación (BC8 ← MaterialDevuelto de BC1)
- **Anti-Corruption Layer**: dónde se necesita (ej. BC3 traduce PeticionBloqueoUsuario de BC4/BC9 a su propio modelo).

### 1.3. Plan de fases de migración

Propón un plan incremental en 4-6 fases donde cada fase deja el sistema funcional. Sugerencia de orden:

1. **Fase 1**: Extraer BC6 (Notificaciones) y BC7 (Reportes) — son genéricos, bajo riesgo.
2. **Fase 2**: Extraer BC2 (Materiales) y BC3 (Usuarios) — son proveedores.
3. **Fase 3**: Extraer BC4 (Cálculo de Multas) y BC9 (Cobro de Multas) — se extraen juntos porque comparten el flujo de sanciones.
4. **Fase 4**: Extraer BC8 (Gestión de Reservas) — depende de MaterialDevuelto de BC1.
5. **Fase 5**: Extraer BC5 (Préstamos Interbibliotecarios) — Gateway context.
6. **Fase 6**: BC1 queda como el último servicio (Circulación), ahora desacoplado.

Para cada fase indica: qué se mueve, qué se adapta, qué tests deben pasar, y qué riesgo tiene.

---

## Fase 2 — Refactorización del dominio (dominio anémico → enriquecido)

Antes de extraer microservicios, el dominio debe enriquecerse. Aplica estos cambios **dentro del monolito actual**:

### 2.1. Agregado Prestamo

- Eliminar `SetFechaDevolucionReal()` e `IncrementarRenovaciones()`.
- Crear `prestamo.renovar(nuevaFecha)` con precondiciones: EstadoPrestamo == ACTIVO, renovacionesUsadas < límite. Emite `PrestamoRenovado`.
- Crear `prestamo.devolver(fechaReal, evaluacion)` con precondiciones: EstadoPrestamo es ACTIVO o RENOVADO. Emite `MaterialDevuelto`.
- Introducir VOs: `PeriodoPrestamo`, `RenovacionesUsadas`, `PrestamoId`.

### 2.2. Agregado Reserva

- Eliminar `SetPosicionCola()`.
- Crear `reserva.notificarDisponibilidad(fecha)` con precondición: EstadoReserva == EN_ESPERA, posición == 1. Emite `ReservaNotificada`.
- Crear `reserva.expirar()` con precondición: EstadoReserva == NOTIFICADA, 24h transcurridas. Emite `ReservaExpirada`.
- Crear `reserva.cancelar()` con precondición: EstadoReserva es EN_ESPERA o NOTIFICADA. Emite `ReservaCancelada`.
- Introducir VOs: `PosicionCola`, `PeriodoNotificacion`, `ReservaId`.

### 2.3. Agregado Multa

- Eliminar `SetFechaPago()`.
- Crear `multa.pagar(fechaPago)` con precondición: EstadoMulta == PENDIENTE. Emite `MultaPagada`.
- Crear `multa.condonar()` con la misma protección de estado.
- Introducir VOs: `Dinero`, `MultaId`, `PorcentajeRecargo`.

### 2.4. Calculadores de multa

- Eliminar `IRepositorio<Prestamo>` e `IRepositorio<Usuario>` de `CalculadorMultaPorRetraso` y `CalculadorMultaPorPerdida`.
- Los datos necesarios (diasRetraso, tipoUsuario, valorMaterial) deben resolverse en el servicio de aplicación y pasarse como parámetros en `ContextoMulta`.

### 2.5. Eventos de dominio

- Crear interfaz `IDomainEvent` con `occurredOn()` y `eventType()`.
- Cada agregado raíz mantiene una lista interna `List<IDomainEvent> domainEvents`.
- El servicio de aplicación extrae los eventos tras persistir el agregado y los publica.
- Eventos a implementar: `PrestamoRegistrado`, `MaterialDevuelto`, `PrestamoRenovado`, `RenovacionRechazada`, `MultaGenerada`, `MultaPagada`, `UsuarioBloqueadoPorDeuda`, `PeticionDesbloqueoUsuario`, `ReservaCreada`, `ReservaNotificada`, `ReservaExpirada`, `ReservaCancelada`.

---

## Fase 3 — Extracción de microservicios

Para cada microservicio extraído:

### 3.1. Estructura de paquetes

Genera la estructura de carpetas siguiendo el estándar DDD por capa:

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

### 3.2. Comunicación entre servicios

- **Eventos asíncronos** (vía message broker) para todo lo definido en §4.4 del documento DDD.
- **Consultas síncronas** (REST) solo donde el documento indica relación síncrona:
  - BC1 → BC2: `ConsultarDisponibilidad`
  - BC1 → BC3: `ConsultarEstadoUsuario`
  - BC1 → BC5: `SolicitarMaterialExterno`
  - BC1 → BC4: `ConsultarDeudaPendiente` (antes de renovar)
- **Anti-Corruption Layer** en BC3 para traducir `PeticionBloqueoUsuario` al modelo interno de Usuario.

### 3.3. Contratos de eventos

Para cada evento de dominio, define el contrato JSON con:
- `eventId` (UUID)
- `eventType` (nombre del evento)
- `occurredOn` (timestamp ISO 8601)
- `payload` (campos específicos según el documento DDD)
- `aggregateId` (ID del agregado raíz que lo emitió)
- `version` (para evolución del esquema)

---

## Fase 4 — Diagramas de arquitectura

Genera los siguientes diagramas en formato **Mermaid** (listos para copiar):

1. **Diagrama de contexto de microservicios**: muestra los 9 servicios, sus bases de datos, el message broker, y las flechas de comunicación (síncrona = línea sólida, asíncrona = línea punteada).
2. **Diagrama de flujo de eventos** para cada flujo principal:
   - Registro de préstamo
   - Devolución con daño y generación de multa
   - Pago de multa y desbloqueo
   - Creación de reserva y notificación
3. **Diagrama de capas DDD** de un microservicio ejemplo (BC1) mostrando las clases concretas de Elysium en cada capa.

---

## Reglas de interacción

- **No modifiques patrones ya implementados** (Facade, Abstract Factory, Factory Method, Builder, Strategy) sin justificación explícita. Si un patrón debe adaptarse para microservicios, explica por qué.
- **No reimplementes patrones descartados** (Template Method, Decorator, Adapter, Observer, Singleton, Command) sin consultar.
- **Mantén la trazabilidad**: cada clase, método o evento debe poder rastrearse al documento DDD (sección y tabla correspondiente).
- **Usa el lenguaje ubicuo**: los nombres de clases, métodos, variables y eventos deben coincidir con los términos definidos en las tablas de Lenguaje Ubicuo del documento.
- **Genera código compilable**: todo el código Java debe compilar contra Java 17 con Maven. Usa Spring Boot 3.x como framework base para los microservicios.
- **Tests**: para cada refactorización del dominio (Fase 2), genera al menos un test unitario que verifique la invariante protegida.

---

## Archivos que recibirás

Después de que confirmes la Fase 0, te enviaré:

1. **DDD_Elysium_v5_organized.pdf** — Documento DDD completo con bounded contexts, agregados, eventos, lógica de dominio, servicios de dominio, lenguaje ubicuo, y diagramas de secuencia.
2. **Código fuente** — Estructura de paquetes y clases clave del monolito actual.
3. **Sistema_Biblioteca_patrones.pdf** — Diagrama UML con la estructura actual de patrones y relaciones.

---

## Formato de respuesta esperado

Para cada fase, estructura tu respuesta así:

```
## Fase N — [Nombre]

### Cambios propuestos
[Lista de cambios concretos con justificación DDD]

### Código
[Código Java completo para cada clase nueva o modificada]

### Tests
[Tests unitarios para invariantes protegidas]

### Diagrama Mermaid
[Diagrama si aplica]

### Riesgos y mitigación
[Qué puede salir mal y cómo prevenirlo]
```

---

## Comienza ahora

Lee todo el contexto proporcionado y responde con la **Fase 0 — Validación de comprensión**. No escribas código hasta que yo confirme que tu resumen es correcto.
