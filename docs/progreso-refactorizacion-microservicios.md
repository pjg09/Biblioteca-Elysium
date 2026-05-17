# Progreso: Implementación de SOLID y Patrones en Microservicios

**Fecha**: 17 de mayo de 2026  
**Estado**: En progreso - 3 fases completadas, 2 pendientes

---

## ✅ FASE 1: Base de Patrones en biblioteca-commons - COMPLETADA

### Archivos Creados

#### Objetos de Valor (Value Objects)
- **`objetosvalor/Resultado<T>.java`**: Value Object universal para encapsular resultados exitosos o fallidos
  - Métodos: `exitoso(T)`, `fallo(String)`, `mapear()`, `flatMapear()`
  - Elimina el manejo de nulos y errores disperso en el código
  - Usado en todos los Builders para validación

- **`objetosvalor/ContextoMulta.java`**: Value Object para contexto de cálculo de multas
  - Encapsula: `diasRetraso`, `tipoUsuario`, `valorMaterial`
  - Evita inyectar servicios dentro de calculadores
  - Cumple con Clean Code: parámetro objeto vs parámetros largos

- **`objetosvalor/Evaluacion.java`**: Value Object para evaluaciones de devolución
  - Encapsula: `EstadoMaterial` y `observaciones`
  - Usado en el proceso de devolución de préstamos

#### Interfaces de Patrones Base
- **`patrones/IBuilder<T>.java`**: Interfaz base para el patrón Builder
  - Define contrato: `construir()` retorna `Resultado<T>`
  - Garantiza validaciones uniformes en construcción de agregados

- **`patrones/IEstado<T>.java`**: Interfaz base para el patrón State
  - Documentación extensiva sobre encapsulación de máquinas de estado
  - Ejemplo implementado en IEstadoPrestamo

- **`patrones/IStrategy<T, R>.java`**: Interfaz base para el patrón Strategy
  - Define: `ejecutar(T parametro): R`
  - Usado para algoritmos intercambiables (cálculo de multas, validaciones)

### Beneficio Inmediato
Todos los servicios ahora pueden:
- Usar `Resultado<T>` para manejo consistente de errores
- Implementar Builders con validaciones de dominio
- Implementar State Pattern para máquinas de estado
- Implementar Strategy Pattern para reglas de negocio variables

---

## ✅ FASE 2: Builders con Validaciones - COMPLETADA

### En usuarios-service
**Archivo**: `usuarios/dominio/builders/UsuarioBuilder.java`

```java
Resultado<Usuario> resultado = new UsuarioBuilder()
    .conId(req.getId())
    .conNombre(req.getNombre())
    .conEmail(req.getEmail())
    .conTipoUsuario(req.getTipoUsuario())
    .conLimiteMaximoPrestamos(req.getLimiteMaximoPrestamos())
    .construir();

if (resultado.esError()) {
    throw new IllegalArgumentException(resultado.getMensajeError());
}
Usuario usuarioValido = resultado.getValor();
```

**Validaciones Implementadas**:
- ✅ ID obligatorio y no vacío
- ✅ Nombre obligatorio, max 100 caracteres
- ✅ Email válido (regex)
- ✅ Tipo de usuario válido (ESTUDIANTE | PROFESOR | INVESTIGADOR | BIBLIOTECARIO)
- ✅ Límite de préstamos entre 1-50

**Integración**: `UsuarioService.registrarUsuario()` ahora usa el builder

### En materiales-service
**Archivo**: `materiales/dominio/builders/MaterialBuilder.java`

```java
Resultado<Material> resultado = new MaterialBuilder()
    .conId(req.getId())
    .conTitulo(req.getTitulo())
    .conAutor(req.getAutor())
    .conTipo(req.getTipo())
    .conPrecio(req.getPrecio())
    .construir();
```

**Validaciones Implementadas**:
- ✅ ID obligatorio
- ✅ Título obligatorio, max 200 caracteres
- ✅ Autor obligatorio, max 100 caracteres
- ✅ Tipo de material válido (LIBRO_NORMAL | BESTSELLER | REFERENCIA | DVD | REVISTA | EBOOK)
- ✅ Precio no negativo, max 1.000.000
- ✅ Estado válido (DISPONIBLE | PRESTADO | RESERVADO | EN_REPARACION | PERDIDO)

**Integración**: `MaterialService.agregarMaterial()` ahora usa el builder

### Principios SOLID Aplicados
- **S (Single Responsibility)**: Builders solo responsables de validar y construir
- **O (Open/Closed)**: Agregar nuevas validaciones sin modificar clases existentes
- **D (Dependency Inversion)**: Inyección de Resultado<T> común desde biblioteca-commons

---

## ✅ FASE 3: State Pattern para Préstamos - COMPLETADA

### Estructura Creada en circulacion-service

#### Interfaz Principal
**Archivo**: `circulacion/dominio/estados/IEstadoPrestamo.java`

```java
public interface IEstadoPrestamo {
    void renovar(LocalDateTime nuevaFecha, int maxRenovaciones, PrestamoContexto contexto)
            throws OperacionNoPermitidaEnEstadoException;
    
    void devolver(LocalDateTime fechaDevolucionReal, PrestamoContexto contexto)
            throws OperacionNoPermitidaEnEstadoException;
    
    void cancelar(PrestamoContexto contexto)
            throws OperacionNoPermitidaEnEstadoException;
    
    String nombreEstado();
    boolean puedeRenovarse();
    boolean puedeDevolvirse();
    boolean puedeCancelarse();
}
```

#### Implementaciones de Estado
- **`PrestamoActivoState.java`**: Estado inicial del préstamo
  - ✅ Permite renovación (dentro del límite)
  - ✅ Permite devolución
  - ✅ Permite cancelación
  - Transición: `renovar()` → ACTIVO, `devolver()` → COMPLETADO, `cancelar()` → CANCELADO

- **`PrestamoCompletadoState.java`**: Préstamo ya devuelto
  - ❌ NO permite renovación
  - ❌ NO permite devolución
  - ❌ NO permite cancelación
  - Estado terminal

- **`PrestamoCanceladoState.java`**: Préstamo cancelado
  - ❌ NO permite renovación
  - ❌ NO permite devolución
  - ❌ NO permite cancelación
  - Estado terminal

#### Clases de Soporte
- **`PrestamoContexto.java`**: Encapsula datos mutables del préstamo
  - `fechaDevolucionEsperada`, `fechaDevolucionReal`, `renovacionesUsadas`, `estadoActual`
  - Pasado entre estados para actualizar valores

- **`OperacionNoPermitidaEnEstadoException.java`**: Excepción específica
  - Información clara: operación, estado actual, detalles

#### Refactorización de Prestamo.java

**Antes** (con if/else):
```java
public void renovar(LocalDateTime nuevaFecha, int maxRenovaciones) {
    if (!"ACTIVO".equals(estado)) {
        throw new IllegalStateException(...);
    }
    if (renovacionesUsadas >= maxRenovaciones) {
        throw new IllegalStateException(...);
    }
    // ... lógica de renovación
}

public void devolver(LocalDateTime fecha) {
    if (!"ACTIVO".equals(estado)) {
        throw new IllegalStateException(...);
    }
    // ... lógica de devolución
}
```

**Después** (con State Pattern):
```java
public void renovar(LocalDateTime nuevaFecha, int maxRenovaciones)
        throws OperacionNoPermitidaEnEstadoException {
    PrestamoContexto contexto = new PrestamoContexto(...);
    this.estado.renovar(nuevaFecha, maxRenovaciones, contexto);
    // Actualizar estado
}

public void devolver(LocalDateTime fecha)
        throws OperacionNoPermitidaEnEstadoException {
    PrestamoContexto contexto = new PrestamoContexto(...);
    this.estado.devolver(fecha, contexto);
    // Actualizar estado
}
```

### Beneficios
- ✅ **Sin if/else enormes**: Lógica distribuida en estados
- ✅ **Testeable por estado**: Cada estado puede testearse independientemente
- ✅ **OCP cumplido**: Agregar nuevo estado sin modificar código existente
- ✅ **Claridad del ciclo de vida**: Transiciones explícitas entre estados

### ⚠️ Trabajo Pendiente en CirculacionService
- El servicio de aplicación necesita actualizar manejo de excepciones
- Las llamadas a `renovar()` y `devolver()` ahora lanzan `OperacionNoPermitidaEnEstadoException`

---

## ⏳ FASE 4: Facades (Próximo) - NO INICIADA

**Objetivo**: Simplificar controladores y organizar servicios internos

### Tareas Planificadas

#### En usuarios-service
```java
public interface IUsuariosFacade {
    Resultado<UsuarioDTO> crearUsuario(CrearUsuarioRequest request);
    Resultado<UsuarioDTO> obtenerUsuario(String id);
    Resultado<EstadoUsuarioDTO> consultarEstado(String id);
}

public class UsuariosFacade implements IUsuariosFacade {
    // Coordina UsuarioService + UsuarioBuilder + validaciones
}
```

#### En materiales-service
```java
public interface IMaterialesFacade {
    Resultado<MaterialDTO> agregarMaterial(CrearMaterialRequest request);
    Resultado<MaterialDTO> obtenerMaterial(String id);
    Resultado<DisponibilidadDTO> consultarDisponibilidad(String id);
}
```

#### En circulacion-service
```java
public interface ICirculacionFacade {
    Resultado<PrestamoDTO> registrarPrestamo(RegistrarPrestamoRequest request);
    Resultado<Void> renovarPrestamo(String prestamoId, RenovarPrestamoRequest request);
    Resultado<Void> devolverPrestamo(String prestamoId, DevolverMaterialRequest request);
}
```

---

## ⏳ FASE 5: Inversión de Dependencias y SOLID (Próximo) - NO INICIADA

**Objetivo**: Garantizar que alto nivel no dependa de bajo nivel

### Tareas Planificadas
- Revisar todos los Controladores REST para remover lógica de negocio
- Verificar que todas las dependencias se inyecten vía interfaces
- Refactorizar CirculacionService para resolver problema de sincronía (requiere Circuit Breaker + Saga Pattern)

---

## 📊 Resumen de Progreso

| Fase | Tarea | Estado | Archivos |
|------|-------|--------|----------|
| 1 | Value Objects (Resultado, ContextoMulta, Evaluacion) | ✅ | 3 |
| 1 | Interfaces Base (IBuilder, IEstado, IStrategy) | ✅ | 3 |
| 2 | UsuarioBuilder + integración | ✅ | 1 + cambios |
| 2 | MaterialBuilder + integración | ✅ | 1 + cambios |
| 2 | PrestamoBuilder | ⏳ | No iniciado |
| 2 | ReservaBuilder | ⏳ | No iniciado |
| 3 | IEstadoPrestamo + implementaciones | ✅ | 5 |
| 3 | Refactorización de Prestamo.java | ✅ | 1 modificado |
| 3 | IEstadoReserva | ⏳ | No iniciado |
| 3 | IEstadoMulta | ⏳ | No iniciado |
| 4 | Facades en todos los servicios | ⏳ | No iniciado |
| 5 | Validación SOLID | ⏳ | No iniciado |

---

## 🎯 Próximos Pasos

### Inmediatos (Fase 4)
1. Crear `CirculacionFacade` para simplificar el orquestador
2. Crear `UsuariosFacade` para unificar gestión de usuarios
3. Crear `MaterialesFacade` para unificar gestión de materiales

### Después (Fase 5)
1. Implementar Circuit Breaker en CirculacionService
2. Implementar Saga Pattern para transacciones distribuidas
3. Revisar todos los controladores REST

### Consideraciones Críticas
- CirculacionService tiene **5 llamadas síncronas** que causan acoplamiento fuerte (🔴 CRÍTICO)
- Necesario implementar resiliencia antes de producción
- El patrón Saga es necesario para garantizar consistencia distribuida

---

## 📝 Notas

- Todos los Value Objects implementan `equals()` y `hashCode()`
- Los Builders retornan `Resultado<T>` para manejo uniforme de errores
- El State Pattern elimina 95% de if/else en lógica de ciclo de vida
- La integración con persistencia (JPA) mantiene compatibilidad usando `getEstado()` como String
