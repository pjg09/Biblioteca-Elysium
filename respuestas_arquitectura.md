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

### 5. Strategy (colección de estrategias) — `ValidadorReglasService`

> **¿Por qué NO es Chain of Responsibility?**
> Según la definición canónica (Alexander Shvets, *Sumérgete en los Patrones de Diseño*), Chain of Responsibility requiere que **cada handler tenga referencia al siguiente** (`setNext(handler)`) y que **cada handler decida autónomamente** si procesa o delega. En nuestro caso, el `ValidadorReglasService` actúa como **orquestador centralizado** que itera una lista de reglas — los handlers no se conocen entre sí ni se enlazan.

> **¿Por qué NO es Composite?**
> Según Shvets, Composite requiere componer objetos en **estructuras de árbol** (recursión padre-hijo). `ValidadorReglasService` mantiene una **lista plana** de reglas, no un árbol. No hay anidación ni recursión.

**Lo que realmente tenemos es el patrón Strategy con una colección:**

Cada `IReglaValidacion` (`ReglaUsuarioActivo`, `ReglaMaterialDisponible`, `ReglaLimiteNoExcedido`) es una **estrategia de validación** independiente e intercambiable. Todas comparten la misma interfaz (`validar(contexto) : ResultadoValidacion`) pero implementan algoritmos distintos. El `ValidadorReglasService` mantiene una `List<IReglaValidacion>`, las ejecuta todas y combina sus resultados mediante `ResultadoValidacion.combinar()`.

```
ValidadorReglasService (Contexto con colección de estrategias)
  ├── ReglaUsuarioActivo        (Strategy 1)
  ├── ReglaMaterialDisponible   (Strategy 2)
  └── ReglaLimiteNoExcedido     (Strategy 3)
         ↓ cada una retorna ResultadoValidacion
         ↓ se combinan con .combinar()
```

**Justificación SOLID:**
- **OCP**: Agregar una nueva regla no modifica el servicio ni las reglas existentes.
- **SRP**: Cada regla valida exactamente un aspecto; el servicio solo coordina.
- **DIP**: El servicio depende de la interfaz `IReglaValidacion`, no de implementaciones concretas.

### 6. Strategy — `GestorMultasService` + `ICalculadorMulta`

**Problema que resuelve:** El sistema necesita calcular multas de naturaleza completamente distinta (por retraso, por daño físico al material, por pérdida). Si `DevolucionService` tuviera que decidir con `if/else` qué lógica de cálculo aplicar, cada nuevo tipo de multa obligaría a modificar esa clase, violandoOCP.

**Implementación:** `GestorMultasService` mantiene una `List<ICalculadorMulta>` con tres estrategias registradas. Al recibir un `ContextoMulta`, itera la lista y selecciona la **primera** estrategia cuyo `puedeCalcular(contexto)` retorne `true`:

```java
// GestorMultasService selecciona UNA estrategia
for (ICalculadorMulta calculador : calculadores) {
    if (calculador.puedeCalcular(contexto)) {
        return calculador.calcular(contexto);  // Solo uno responde
    }
}
```

Es equivalente a un `switch` refactorizado en objetos:
- `CalculadorMultaPorRetraso` → calcula multa basándose en días de retraso × tarifa por tipo de material
- `CalculadorMultaPorDano` → calcula multa basándose en el costo de reparación de los daños
- `CalculadorMultaPorPerdida` → calcula multa basándose en el precio del material + recargo por tipo de usuario

Cada calculador implementa la interfaz `ICalculadorMulta` con dos métodos: `puedeCalcular(contexto)` (determina si este calculador aplica) y `calcular(contexto)` (ejecuta su algoritmo específico).

> **¿Por qué NO es Chain of Responsibility?**
> No hay handlers enlazados con `setNext()`. Es `GestorMultasService` quien itera y selecciona centralizadamente, no los handlers quienes se delegan entre sí.

**Justificación SOLID:**
- **OCP**: Agregar un nuevo tipo de multa requiere crear un nuevo calculador y registrarlo, sin modificar el gestor ni los calculadores existentes.
- **SRP**: Cada calculador conoce únicamente la lógica de su tipo de multa. El gestor solo itera y coordina.
- **DIP**: `GestorMultasService` depende de la interfaz `ICalculadorMulta`, no de implementaciones concretas.

### 7. `ResultadoValidacion` — Notification Pattern
En lugar de lanzar excepciones para errores de negocio, `ResultadoValidacion` agrupa todos los errores encontrados y los devuelve como un objeto. Si un usuario está bloqueado **y** tiene el límite excedido, el sistema muestra ambos errores a la vez. Esto sigue el patrón **Notification** (Martin Fowler).

### 8. Decorator en Notificaciones — `INotificacionService`
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

### 9. Convivencia de Abstract Factory y Builder (Transacciones)
- La **Abstract Factory** (`IPrestamoFactory`) es una interfaz con múltiples implementaciones concretas (`PrestamoNormalFactory`, `PrestamoInterbibliotecarioFactory`). `PrestamoService` mantiene un `Map<String, IPrestamoFactory>` y selecciona la fábrica concreta según el tipo de préstamo.
- El **Builder** (`PrestamoBuilder`) construye el objeto cuando hay muchos parámetros, usando la API fluida.

> **¿Por qué NO es Factory Method?**
> Según Shvets, Factory Method requiere que una **superclase defina un método fábrica** que las **subclases sobrescriban** (usa herencia). `IPrestamoFactory` es una interfaz independiente con implementaciones separadas — no hay superclase ni override. Esto corresponde a **Abstract Factory simplificada** (una interfaz de fábrica con múltiples implementaciones concretas).

Estos patrones cooperan: la Abstract Factory utiliza internamente al Builder para ensamblar el objeto. No es redundancia; cada uno resuelve un problema distinto (*qué* tipo crear vs *cómo* construirlo).

### 10. Abstract Factory — `IBibliotecaFactory`
`IBibliotecaFactory` permite crear familias completas de servicios según el tipo de biblioteca:

| Método | BibliotecaPublicaFactory | BibliotecaUniversitariaFactory |
|--------|--------------------------|-------------------------------|
| `crearPoliticaTiempoService()` | `PoliticaTiempoPorTipoService` | `PoliticaTiempoPorTipoService` |
| `crearNotificacionService()` | Email (smtp.publica.gov) | Email (smtp.universidad.edu) |

**Justificación SOLID:**
- **OCP**: Agregar un tipo nuevo de biblioteca no modifica el código existente.
- **DIP**: Los servicios consumen interfaces, nunca las fábricas concretas.

### 11. Template Method — `ProcesadorTransaccionTemplate`
El procesamiento de préstamos y reservas comparte pasos comunes (validar → verificar disponibilidad → crear transacción → guardar → notificar). `ProcesadorTransaccionTemplate` define el esqueleto del algoritmo, y `ProcesadorPrestamo`/`ProcesadorReserva` implementan los pasos variantes.

**Justificación SOLID:**
- **OCP**: Los pasos invariantes están protegidos; solo se extienden los pasos variantes.
- **DIP**: `PrestamoService` y `ReservaService` delegan al template.

### 12. Objetos de Contexto — Parameter Object Pattern
Para evitar pasar muchos parámetros entre métodos, se usan objetos de contexto:
- **`ContextoMulta`**: agrupa datos para el motor de multas.
- **`ContextoValidacion`**: agrupa datos para las reglas de validación.
- **`ContextoCreacionPrestamo`**: agrupa datos para las factorías de préstamos.

### 13. Strategy — `ICalculadorCostoDanoService` (dentro de `CalculadorMultaPorDano`)

**Problema que resuelve:** La multa por daño requiere calcular el costo de reparación de cada daño encontrado en el material. Este cálculo depende del tipo de daño (`PAGINAS_RASGADAS`, `MANCHAS`, `CUBIERTA_DANADA`, `RAYONES`, `NO_FUNCIONAL`) y de su gravedad (`LEVE`, `MODERADO`, `GRAVE`, `IRREPARABLE`). Si el algoritmo de cálculo estuviera codificado directamente dentro de `CalculadorMultaPorDano`, cualquier cambio en la estructura de tarifas obligaría a modificar la clase del calculador, acoplando la política de precios con la lógica de generación de multas.

**Implementación:** La interfaz `ICalculadorCostoDanoService` define dos métodos: `calcularCosto(Dano dano)` para un daño individual y `calcularCostoTotal(List<Dano> danos)` para acumular el costo de todos los daños. La implementación actual, `CalculadorCostoDanoService`, utiliza un `Map<TipoDano, Map<NivelGravedad, decimal>>` que mapea cada combinación tipo-gravedad a un monto fijo.

`CalculadorMultaPorDano` recibe `ICalculadorCostoDanoService` por inyección de dependencias y **delega completamente** el cálculo de costos al Strategy. Solo invoca `calcularCostoTotal(danos)` y recibe el monto total, sin conocer cómo se determinó.

**Esto es una Strategy separada de la del punto 6:** El punto 6 describe cómo `GestorMultasService` *selecciona cuál calculador de multas usar*. Este punto describe cómo, *dentro* de uno de esos calculadores (`CalculadorMultaPorDano`), el algoritmo de cálculo de costos de reparación es también intercambiable vía Strategy.

**Justificación SOLID:**
- **OCP**: Cambiar la política de precios de daños (ej., calcular por porcentaje del valor del material) requiere crear una nueva implementación de `ICalculadorCostoDanoService`, sin tocar `CalculadorMultaPorDano`.
- **SRP**: `CalculadorMultaPorDano` solo sabe generar multas por daño; la política de precios es responsabilidad del Strategy inyectado.
- **DIP**: `CalculadorMultaPorDano` depende de la interfaz `ICalculadorCostoDanoService`, no de la implementación concreta.

### 14. `CalculadorMultaPorPerdida` — Precio del Material
Obtiene el valor del material de `material.getPrecio()`. El porcentaje de recargo varía por tipo de usuario (Estudiante 20%, Profesor 10%, Investigador 0%, Público 30%).

---

## C) Capa de Dominio (Entidades y Reglas de Negocio)
Responsabilidad: *Representar los conceptos del negocio. Es la capa más pura, no conoce infraestructura ni presentación.*

### 15. Value Objects para IDs — Anti-patrón Primitive Obsession
Todos los IDs usan Value Objects: `IdUsuario`, `IdMaterial`, `IdTransaccion` (base), `IdPrestamo` (extiende `IdTransaccion`), `IdReserva` (extiende `IdTransaccion`), `IdMulta`.

**Nota:** `Multa` usa `IdMulta` para su propio ID, pero `String` para `idPrestamo`/`idUsuario` (referencia externa recibida desde el contexto de creación).

**Justificación SOLID:**
- **SRP**: Validación del formato centralizada en una sola clase.
- **OCP**: Cambiar formato de ID modifica una sola clase.
- **Type Safety**: Intercambiar `IdUsuario` por `IdMaterial` causa error de compilación.

### 16. Builders Fluentes sin Director
Los builders usan **Fluent Builder** (Joshua Bloch, Effective Java). No hay Director porque cada builder construye de una sola forma configurable; el Director sería YAGNI. Las interfaces (`IBuilderMulta`, `IBuilderPrestamo`, etc.) facilitan testing y cumplen DIP.

### 17. Reglas de Negocio de Reservas
1. Solo materiales físicos (bloqueo de `EBOOK`)
2. Solo si no está disponible (sugerir préstamo directo)
3. Expiración de 24 horas tras notificación
4. Cola de prioridad con `posicionCola`
5. Limpieza automática de reservas expiradas
6. Restricciones por multas/bloqueos (validadas vía `ValidadorReglasService`)

### 18. `ReglaLimiteNoExcedido`
Consulta al `ILimitePrestamoService` si el usuario superó el máximo de materiales según su tipo. Es una **estrategia de validación** especializada inyectada al `ValidadorReglasService`.

---

## D) Capa de Infraestructura (Repositorios, Persistencia)
Responsabilidad: *Implementar interfaces para guardar, leer y buscar datos.*

### 19. Abstract Factory de Repositorios — `IRepositorioFactory`
`RepositorioEnMemoriaFactory` crea repositorios en memoria. En producción se crearía un `RepositorioSQLFactory` que implemente la misma interfaz. Los servicios dependen de `IRepositorio<T>`, nunca de la implementación concreta (DIP).

### 20. Adapter — `NotificacionEmailAdapter`
Transforma la interfaz del servicio externo `NotificacionEmailService` hacia `INotificacionService`, integrando servicios externos sin contaminar el dominio.

---

## Cómo Compilar y Ejecutar el Sistema

**Requisitos previos:**
- Java JDK 17 o superior
- Apache Maven 3.6+

**Desde la carpeta `biblioteca-backend/`:**

```bash
# 1. Compilar el proyecto
mvn clean compile

# 2. Ejecutar la aplicación (inicia el menú de consola)
mvn exec:java

# 3. O compilar y ejecutar en un solo paso
mvn clean compile exec:java
```

El plugin `exec-maven-plugin` está configurado en `pom.xml` con la clase principal `com.biblioteca.Main`. Al ejecutar, el sistema:
1. Crea los repositorios en memoria vía `RepositorioEnMemoriaFactory`
2. Ensambla todos los servicios e inyecta dependencias
3. Carga datos de ejemplo (9 materiales, 6 usuarios, 4 préstamos)
4. Inicia el menú interactivo en consola

---

## Resumen de Patrones de Diseño Implementados

| Patrón | Ubicación | Principio SOLID |
|--------|-----------|-----------------|
| **Facade** | `BibliotecaFacade` | SRP, DIP |
| **Abstract Factory** | `IBibliotecaFactory`, `IRepositorioFactory`, `IPrestamoFactory` | OCP, DIP |
| **Builder (Fluent)** | `MaterialBuilder`, `UsuarioBuilder`, `PrestamoBuilder`, `MultaBuilder` | SRP |
| **Template Method** | `ProcesadorTransaccionTemplate` | OCP, DIP |
| **Strategy** | `ICalculadorMulta`, `ICalculadorCostoDanoService`, `IReglaValidacion` | OCP, DIP |
| **Decorator** | `NotificacionConLoggingDecorator`, `NotificacionConReintentosDecorator` | OCP, SRP |
| **Adapter** | `NotificacionEmailAdapter` | DIP, ISP |
