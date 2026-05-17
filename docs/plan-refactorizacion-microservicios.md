# Plan de Refactorización: SOLID y Patrones de Diseño en Microservicios

El objetivo de este plan es reintroducir los principios SOLID y los patrones de diseño (previamente implementados en la arquitectura monolítica `biblioteca-backend`) dentro de la nueva arquitectura basada en microservicios en la carpeta `microservicios/`.

## 1. Análisis de Patrones y Principios a Migrar

### Patrones de Diseño Requeridos
- **Facade**: Para simplificar la comunicación entre los controladores REST y los servicios internos de dominio en cada microservicio.
- **Strategy**: Para algoritmos variables como el cálculo de multas (`multas-service`) o reglas de validación en préstamos (`circulacion-service`).
- **State**: Para manejar el ciclo de vida complejo de entidades, por ejemplo, el estado de un préstamo (`circulacion-service`) o de una reserva (`reservas-service`).
- **Builder**: Para la creación de entidades de dominio complejas (`Material` en `materiales-service`, `Usuario` en `usuarios-service`, etc.).
- **Repository**: Para abstraer el acceso a datos (Spring Data JPA ya nos da parte, pero se debe aislar el modelo de base de datos del modelo de dominio).
- **Value Objects (Objetos de Valor)**: Patrón DDD (Domain-Driven Design) fundamental para encapsular conceptos como `Resultado`, `Evaluacion` o `ContextoMulta`, los cuales deberían vivir en `biblioteca-commons` para ser compartidos si es necesario, o en el dominio de cada microservicio.

### Principios SOLID
- **S (Single Responsibility)**: Dividir controladores muy grandes y servicios con demasiadas responsabilidades (ej. separar notificaciones, reportes, cálculo de reglas).
- **O (Open/Closed)**: Uso de polimorfismo y Strategy para poder añadir nuevas reglas de negocio sin modificar código existente.
- **L (Liskov Substitution)**: Mantener jerarquías de herencia limpias para usuarios y materiales.
- **I (Interface Segregation)**: Evitar interfaces monolíticas. Crear interfaces pequeñas y específicas (ej. `IReservaService`, `INotificacionService`).
- **D (Dependency Inversion)**: Los módulos de alto nivel no deben depender de los de bajo nivel, usar inyección de dependencias a través de interfaces, aislando la lógica core de los adaptadores web o de persistencia.

---

## 2. Estrategia por Microservicio

### `biblioteca-commons` (Objetos Compartidos)
- **Tareas**:
  - [ ] Mover/Crear Objeto de Valor `Resultado<T>` universal para las respuestas internas.
  - [ ] Mover/Crear interfaces comunes de dominio y eventos base (`IDomainEvent`).

### 1. `usuarios-service`
- **Patrones/SOLID**: Builder, Factory Method, Repository.
- **Tareas**:
  - [ ] Implementar `UsuarioBuilder` para instanciar usuarios y validarlos al crearse.
  - [ ] Definir interfaces pequeñas (ISP) para la gestión vs consulta de usuarios.
  - [ ] Asegurar sustitución de Liskov en la jerarquía (Estudiante vs Profesor).

### 2. `materiales-service`
- **Patrones/SOLID**: Builder, Liskov Substitution.
- **Tareas**:
  - [ ] Implementar `MaterialBuilder` (creación de Libros, Revistas, etc.).
  - [ ] Separar la capa de persistencia (Entidades JPA) de las entidades puras del modelo de dominio.
  - [ ] Implementar Facade si el manejo de stock e inventario se hace complejo.

### 3. `circulacion-service` (Préstamos y Devoluciones)
- **Patrones/SOLID**: State (crucial), Strategy, Facade.
- **Tareas**:
  - [ ] **State**: Implementar el ciclo de vida del préstamo (`PrestamoActivoState`, `PrestamoCompletadoState`, etc.) asegurando que el estado dicte el comportamiento permitido.
  - [ ] **Strategy**: Implementar un motor de `IReglaValidacion` para decidir si se aprueba un préstamo (morosidad, límites, etc.).
  - [ ] **Facade**: Crear `CirculacionFacade` que coordine la emisión del préstamo, validaciones y actualización asíncrona (RabbitMQ) de otros servicios.

### 4. `multas-service` y `cobros-service`
- **Patrones/SOLID**: Strategy, Value Objects.
- **Tareas**:
  - [ ] **Strategy**: Recuperar la interfaz `ICalculadorMulta` y sus implementaciones concretas dependiendo del tipo de infracción/retraso.
  - [ ] **Value Object**: Rehacer `ContextoMulta` para calcular multas sin depender directamente de servicios en tiempo de ejecución de cálculo.

### 5. `reservas-service`
- **Patrones/SOLID**: State, Observer (vía Eventos).
- **Tareas**:
  - [ ] **State**: Ciclo de vida de la reserva (`EN_ESPERA`, `NOTIFICADA`, `COMPLETADA`, `CANCELADA`).
  - [ ] Coordinar estados y publicar eventos para desacoplar el envío de e-mails (`notificaciones-service`).

---

## 3. Plan de Acción y Tareas Generales

1. [ ] **FASE 1: Capa Transversal y Modelos Base (Commons)**
   - Establecer `biblioteca-commons` con utilidades puras y de patrones básicos (`Resultado`, Base Eventos).

2. [ ] **FASE 2: Core de Dominio (Entidades y Builders)**
   - Refactorizar Entidades JPA vs. Entidades DTO/Dominio.
   - Reintegrar los **Builders** para `Usuario` y `Material`.

3. [ ] **FASE 3: Lógica Condicional (State y Strategy)**
   - Extraer la lógica "if/else" de `circulacion-service` y `multas-service` hacia los patrones **State** (estado del préstamo) y **Strategy** (cálculo/validación).

4. [ ] **FASE 4: Simplificación de Accesos (Facade e Inversión de Dependencias)**
   - Revisar todos los Controladores REST para que no tengan lógica de negocio y usen un **Facade**.
   - Interfaces sólidas e inyección de dependencias estricta (Principios D e I).

## 4. Próximos pasos
Para iniciar, sugiero empezar con la **FASE 1** (asegurar el paquete commons) y continuar de inmediato con **FASE 3** dentro de `circulacion-service`, ya que el patrón State para préstamos es la refactorización más urgente y de mayor impacto.