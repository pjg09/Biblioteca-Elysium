# Respuestas y Justificaciones Arquitectónicas (Alineadas a las 4 Capas)

Para comprender mejor las decisiones tomadas en el diseño y los patrones aplicados, organizaremos las respuestas a las correcciones basándonos en la arquitectura de **4 capas**.

---

## A) Capa de Presentación (Consola)
Responsabilidad: *Interactuar con el usuario, mostrar menús, capturar entrada y mostrar errores de forma amigable.*

### 1. Desacoplamiento de Reportes (SRP)
Anteriormente, `MenuConsola` calculaba estadísticas y tenía lógica de negocio mezclada (violando SRP). Ahora, la consola delega la obtención de datos al servicio `IServicioReportes`, al que solo invoca para presentar resultados. La consola **nunca accede a repositorios directamente**.

### 2. Manejo de Errores Robusto
Todas las operaciones del menú están envueltas en bloques `try-catch` que capturan `BibliotecaException` y `IllegalArgumentException`. Esto previene que el programa termine abruptamente y permite al usuario reintentar operaciones con mensajes claros de error.

### 3. Simplificación de Menús y Entradas
- **Devoluciones**: reducidas de 4 opciones redundantes (simple / inspección / daños / historial) a 2 opciones (devolver + historial). La inspección se maneja como un sub-flujo dentro de la devolución.
- **Búsquedas consolidadas**: Material, Usuarios, Préstamos y Reservas usan una sola opción de búsqueda multipropósito, en lugar de opciones separadas por criterio (título/autor/ISBN).
- **Inputs simplificados**: Se eliminaron parámetros redundantes. Por ejemplo, para renovar un préstamo solo se pide el `idPrestamo` (no también `idUsuario`), porque el servicio puede derivar los datos faltantes desde la entidad.
- **Configurar tarifas eliminado**: Las tarifas están preconfiguradas internamente en los servicios. No es necesaria una UI de configuración para un proyecto académico; si se requiriera, se manejaría con un archivo de configuración externo.

---

## B) Capa de Aplicación (Servicios y Casos de Uso)
Responsabilidad: *Orquestar los casos de uso, transacciones, seguridad, y llamar a los repositorios o entidades del dominio.*

### 4. Patrón Facade — `BibliotecaFacade`
Todos los flujos principales pasan por la Fachada: `procesarSolicitudMaterial()`, `devolverMaterialSinInspeccion()` y `renovarPrestamo()`. La consola solo conoce a la Fachada, que oculta la complejidad de que existen `PrestamoService`, `ReservaService`, `DevolucionService` e `IRenovacionService`.

**Justificación SOLID:**
- **SRP**: La Fachada solo orquesta; no contiene lógica de negocio.
- **DIP**: Depende de interfaces (`IPrestamoService`, `IRenovacionService`, etc.), no de implementaciones concretas.

### 5. Chain of Responsibility — `ValidadorReglasService`
En lugar de llenar los servicios con sentencias `if`, se inyectan reglas como `ReglaUsuarioActivo`, `ReglaMaterialDisponible` y `ReglaLimiteNoExcedido` al `ValidadorReglasService`. Este itera la `List<IReglaValidacion>`, y cada regla decide si la operación es válida.

**Justificación SOLID:**
- **OCP**: Agregar una nueva regla (ej. "No tener préstamos vencidos para reservar") no modifica el servicio ni las reglas existentes.
- **SRP**: Cada regla valida exactamente un aspecto; el servicio solo coordina.

### 6. `ResultadoValidacion` — Notification Pattern
En lugar de lanzar excepciones para errores de negocio, `ResultadoValidacion` agrupa todos los errores encontrados y los devuelve como un objeto. Si un usuario está bloqueado **y** tiene el límite excedido, el sistema muestra ambos errores a la vez. Esto sigue el patrón **Notification** (Martin Fowler), mejorando la experiencia del usuario.

### 7. Decorator en Notificaciones — `INotificacionService`
En lugar de un servicio "Dios" que acople envíos de SMS, correos y logs, el patrón **Decorator** permite apilar comportamientos en tiempo de ejecución:

```
NotificacionConReintentosDecorator
  └── NotificacionConLoggingDecorator
        └── NotificacionEmailService (base)
```

**Justificación SOLID:**
- **SRP**: Cada decorator hace una sola cosa (logging o reintentos).
- **OCP**: Nuevos comportamientos se agregan creando un nuevo decorator, sin modificar los existentes.
- **DIP**: Todos implementan `INotificacionService`; el consumidor no sabe cuántas capas hay.

### 8. Convivencia de Factory y Builder (Transacciones)
- La **Factory** (`IPrestamoFactory`) decide *el tipo* de instancia polimórfica a crear (`PrestamoNormal` vs `PrestamoInterbibliotecario`) según el contexto.
- El **Builder** (`PrestamoBuilder`) construye el objeto cuando hay muchos parámetros, usando la API fluida.

Estos patrones cooperan: la Factory utiliza internamente al Builder para ensamblar el objeto. No es redundancia; cada uno resuelve un problema distinto (*qué* tipo crear vs *cómo* construirlo).

### 9. Abstract Factory — `IBibliotecaFactory`
`IBibliotecaFactory` permite crear familias completas de servicios según el tipo de biblioteca:

| Método | BibliotecaPublicaFactory | BibliotecaUniversitariaFactory |
|--------|--------------------------|-------------------------------|
| `crearPoliticaTiempoService()` | `PoliticaTiempoPorTipoService` | `PoliticaTiempoPorTipoService` |
| `crearNotificacionService()` | Email (smtp.publica.gov) | Email (smtp.universidad.edu) |

**Justificación SOLID:**
- **OCP**: Agregar un tipo nuevo de biblioteca (ej. `BibliotecaEspecializadaFactory`) no modifica el código existente.
- **DIP**: Los servicios consumen interfaces, nunca las fábricas concretas.

### 10. Template Method — `ProcesadorTransaccionTemplate`
El procesamiento de préstamos y reservas comparte pasos comunes (validar → verificar disponibilidad → crear transacción → guardar → notificar). `ProcesadorTransaccionTemplate` define el esqueleto del algoritmo, y `ProcesadorPrestamo`/`ProcesadorReserva` implementan los pasos variantes.

**Justificación SOLID:**
- **OCP**: Los pasos invariantes están protegidos; solo se extienden los pasos variantes.
- **DIP**: `PrestamoService` y `ReservaService` delegan la orquestación al template, no la implementan directamente.

### 11. Objetos de Contexto — Parameter Object Pattern
Para evitar pasar muchos parámetros entre métodos (`String idUsuario, String idMaterial, ...`), se usan objetos de contexto:
- **`ContextoMulta`**: agrupa `idPrestamo`, `idUsuario`, `idMaterial`, `tipoMulta` y `evaluacion` para el motor de multas.
- **`ContextoValidacion`**: agrupa `idUsuario`, `idMaterial`, `idPrestamo` y `tipoOperacion` para las reglas de validación.
- **`ContextoCreacionPrestamo`**: agrupa los datos necesarios para que las factorías creen un préstamo.

### 12. `ICalculadorCostoDano` — Strategy Pattern
`CalculadorMultaPorDano` recibe su motor de costos vía `ICalculadorCostoDanoService` (interfaz), no la implementación directa. Esto es **Strategy + DIP**: hoy se cobra por tipo de daño y gravedad, pero mañana podría calcularse por porcentaje del valor del material o vía API externa. Sin interfaz, habría que reescribir la clase cada vez.

### 13. Cadena de Calculadores de Multa — `GestorMultasService`
`GestorMultasService` mantiene una `List<ICalculadorMulta>` (retraso, daño, pérdida). Al recibir un `ContextoMulta`, itera los calculadores y el primero que `puedeCalcular()` procesa la solicitud. Esto es **Chain of Responsibility** aplicado al cálculo de multas.

### 14. `CalculadorMultaPorPerdida` — Precio del Material
El calculador obtiene el valor del material directamente de `material.getPrecio()`. El porcentaje de recargo varía por tipo de usuario (Estudiante 20%, Profesor 10%, Investigador 0%, Público 30%). Esto está resuelto mediante un `switch` sobre `TipoUsuario` directamente en el calculador.

---

## C) Capa de Dominio (Entidades y Reglas de Negocio)
Responsabilidad: *Representar los conceptos del negocio. Es la capa más pura, no conoce infraestructura ni presentación.*

### 15. Value Objects para IDs — Anti-patrón Primitive Obsession
Usar `String` para IDs es el anti-patrón *Primitive Obsession*. Todos los IDs del sistema usan Value Objects:
- `IdUsuario` — identifica a `Usuario`
- `IdMaterial` — identifica a `Material`
- `IdTransaccion` — identifica a `Transaccion` (clase base)
- `IdPrestamo` extends `IdTransaccion` — especialización para préstamos
- `IdReserva` extends `IdTransaccion` — especialización para reservas
- `IdMulta` — identifica a `Multa`

**Nota sobre `Multa`:** En el código actual, `Multa` usa `IdMulta` para su propio ID, pero aún utiliza `String` para `idPrestamo` e `idUsuario`. Esto es una **decisión consciente** porque la multa referencia IDs externos que recibe como texto plano desde el contexto de creación, sin necesidad de construir el Value Object completo.

**Justificación SOLID:**
- **SRP**: La validación del formato del ID se centraliza en una sola clase.
- **OCP**: Cambiar el formato de ID (de "USR-XXXXXX" a UUID) requiere modificar una sola clase.
- **Type Safety**: Intercambiar un `IdUsuario` por un `IdMaterial` produce un error de compilación, no un bug silencioso en producción.

### 16. Builders Fluentes sin Director
Los builders (`MaterialBuilder`, `UsuarioBuilder`, `PrestamoBuilder`, `MultaBuilder`) usan el patrón **Fluent Builder** (Joshua Bloch, Effective Java), no el GoF clásico con Director.

**¿Por qué no hay Director?** Un Director se necesita cuando existen "recetas" predefinidas para construir el mismo objeto de formas radicalmente distintas. En nuestro caso, cada builder construye su objeto de una sola forma configurable, por lo que el Director sería una capa de indirección innecesaria (**YAGNI**).

**¿Por qué sí hay interfaces (`IBuilderMulta`, `IBuilderPrestamo`, `IBuilderUsuario`)?** Las interfaces de los builders permiten que el código consumidor dependa de la abstracción, no de la implementación concreta. Esto facilita el testing (mockear builders) y hace posible intercambiar implementaciones.

### 17. Reglas de Negocio de Reservas
La entidad `Reserva` y su servicio implementan las siguientes reglas:
1. **Solo materiales físicos**: Se bloquean reservas de `EBOOK` en `ReservaService`.
2. **Solo si no está disponible**: Si el material está `DISPONIBLE`, se sugiere hacer un préstamo directo.
3. **Expiración de 24 horas**: Al notificar disponibilidad, se establece `fechaExpiracion = now + 24h`.
4. **Cola de prioridad**: Cada reserva tiene `posicionCola` que se actualiza al cancelar/expirar una reserva anterior.
5. **Limpieza automática**: `limpiarReservasExpiradas()` cancela reservas vencidas y notifica al siguiente en cola.
6. **Restricciones de usuario**: Usuarios con multas, bloqueos o suspensiones no pueden reservar (validado a través del `ValidadorReglasService`).

### 18. `ReglaLimiteNoExcedido`
Esta regla consulta al `ILimitePrestamoService` si el usuario superó el máximo de materiales permitidos según su tipo (ej. Estudiantes = 3, Profesores = 5). Funciona como un freno automático centralizado antes de procesar transacciones, evitando lógica duplicada en cada servicio.

---

## D) Capa de Infraestructura (Repositorios, Persistencia)
Responsabilidad: *Implementar interfaces definidas en el dominio para guardar, leer y buscar datos.*

### 19. Abstract Factory de Repositorios — `IRepositorioFactory`
Para no esparcir `new RepositorioMaterialEnMemoria()` por los servicios o en `Main`, se usa el patrón **Abstract Factory** con `IRepositorioFactory`.

- `RepositorioEnMemoriaFactory` crea repositorios en memoria (desarrollo/pruebas).
- En producción, se crearía un `RepositorioSQLFactory` que implemente la misma interfaz.

**Justificación SOLID:**
- **DIP**: Los servicios dependen de `IRepositorio<T>`, nunca de la implementación concreta.
- **OCP**: Cambiar de en-memoria a SQL requiere solo una nueva Factory y configuración, cero cambios en servicios.

### 20. Adapter — `NotificacionEmailAdapter`
Transforma la interfaz de `NotificacionEmailService` (un servicio externo con su propia API) hacia la interfaz `INotificacionService` que esperan los servicios internos. Esto es el patrón **Adapter** puro, permitiendo integrar servicios externos sin contaminar el dominio.

---

## Resumen de Patrones Implementados

| Patrón | Ubicación | Principio SOLID |
|--------|-----------|-----------------|
| **Facade** | `BibliotecaFacade` | SRP, DIP |
| **Abstract Factory** | `IBibliotecaFactory`, `IRepositorioFactory` | OCP, DIP |
| **Factory Method** | `IPrestamoFactory` | OCP, LSP |
| **Builder (Fluent)** | `MaterialBuilder`, `UsuarioBuilder`, `PrestamoBuilder`, `MultaBuilder` | SRP |
| **Template Method** | `ProcesadorTransaccionTemplate` | OCP, DIP |
| **Chain of Responsibility** | `ValidadorReglasService`, `GestorMultasService` | OCP, SRP |
| **Strategy** | `ICalculadorCostoDanoService`, `IReglaValidacion` | OCP, DIP |
| **Decorator** | `NotificacionConLoggingDecorator`, `NotificacionConReintentosDecorator` | OCP, SRP |
| **Adapter** | `NotificacionEmailAdapter` | DIP, ISP |
| **Value Object** | `IdUsuario`, `IdMaterial`, `IdTransaccion`, `IdMulta` | SRP, Type Safety |
| **Parameter Object** | `ContextoMulta`, `ContextoValidacion`, `ContextoCreacionPrestamo` | SRP |
| **Notification** | `ResultadoValidacion` | SRP |
