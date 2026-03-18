# b. Flujo del Sistema Explicado

El sistema sigue una **arquitectura de 4 capas** donde cada solicitud del usuario recorre las capas de arriba hacia abajo y la respuesta sube de vuelta.

```
┌─────────────────────────────────────────────────┐
│     CAPA DE PRESENTACIÓN (MenuConsola)          │ ← Captura input del usuario
├─────────────────────────────────────────────────┤
│     CAPA DE APLICACIÓN (Servicios)              │ ← Orquesta la lógica
├─────────────────────────────────────────────────┤
│     CAPA DE DOMINIO (Entidades, Value Objects)  │ ← Reglas de negocio puras
├─────────────────────────────────────────────────┤
│     CAPA DE INFRAESTRUCTURA (IRepositorio<T>)   │ ← Persistencia de datos
└─────────────────────────────────────────────────┘
```

---

## Inicialización del Sistema (`Main.java`)

Antes de que el usuario interactúe, `Main.main()` construye toda la infraestructura:

1. **Crea los repositorios** mediante `IRepositorioFactory` → `RepositorioEnMemoriaFactory` (Abstract Factory)
2. **Crea los servicios base**: `DisponibilidadStandardService`, `LimitePorTipoUsuarioService`, `PoliticaTiempoPorTipoService`, `NotificacionEmailService`
3. **Registra las estrategias de multas**: `CalculadorMultaPorRetraso`, `CalculadorMultaPorDano`, `CalculadorMultaPorPerdida` en `GestorMultasService` (Strategy)
4. **Registra las reglas de validación**: `ReglaUsuarioActivo`, `ReglaMaterialExiste`, `ReglaMaterialDisponible`, `ReglaLimiteNoExcedido` en `ValidadorReglasService` (Strategy)
5. **Crea los servicios principales**: `PrestamoService`, `DevolucionService`, `ReservaService`, `RenovacionService`
6. **Carga datos de ejemplo**: materiales, usuarios y préstamos de prueba
7. **Inicia `MenuConsola`** pasando todas las dependencias

```
Main.main()
  │
  ├── RepositorioEnMemoriaFactory.crear*()     → Repositorios
  ├── new DisponibilidadStandardService(repos)  → IDisponibilidadService
  ├── new ValidadorReglasService(servicios)     → IValidadorReglasService
  │     ├── registrarRegla(ReglaUsuarioActivo)
  │     ├── registrarRegla(ReglaMaterialExiste)
  │     ├── registrarRegla(ReglaMaterialDisponible)
  │     └── registrarRegla(ReglaLimiteNoExcedido)
  ├── new GestorMultasService()                 → IGestorMultasService
  │     ├── registrarCalculador(CalculadorRetraso)
  │     ├── registrarCalculador(CalculadorDano)
  │     └── registrarCalculador(CalculadorPerdida)
  ├── new PrestamoService(...)                  → IPrestamoService
  ├── new DevolucionService(...)                → IDevolucionService
  ├── new ReservaService(...)                   → IReservaService
  ├── new RenovacionService(...)                → IRenovacionService
  ├── cargarDatosEjemplo(repos)
  └── new MenuConsola(todo).iniciar()
```

---

## Flujo 1: Registrar Préstamo

El usuario quiere solicitar un material prestado.

### Recorrido por capas:

```
PRESENTACIÓN → MenuConsola
  │  Captura: idUsuario, idMaterial, tipoPréstamo ("normal" o "interbibliotecario")
  │  Envuelve en try-catch para errores amigables
  ▼
APLICACIÓN → PrestamoService.registrarPrestamo(idUsuario, idMaterial, tipo)
  │
  │  PASO 1: Validar reglas de negocio
  │  └── ValidadorReglasService.validarPrestamo(idUsuario, idMaterial)
  │       ├── ReglaUsuarioActivo.validar()     → ¿Estado == ACTIVO?
  │       ├── ReglaMaterialExiste.validar()    → ¿Existe en el repo?
  │       ├── ReglaMaterialDisponible.validar()→ ¿Estado == DISPONIBLE?
  │       └── ReglaLimiteNoExcedido.validar()  → ¿No excede máximo?
  │       └── ResultadoValidacion.combinar()   → Acumula todos los errores
  │
  │  PASO 2: Obtener entidades del dominio
  │  └── repoUsuario.obtenerPorId() + repoMaterial.obtenerPorId()
  │
  │  PASO 3: Verificar si el material es prestable según tipo
  │  └── DisponibilidadStandardService.materialEsPrestable()
  │
  │  PASO 4: Calcular fecha de devolución
  │  └── PoliticaTiempoPorTipoService.obtenerFechaDevolucion(tipoMaterial, tipoUsuario)
  │
  │  PASO 5: Crear el préstamo (Factory Method + Builder)
  │  └── PrestamoService.crearPrestamoSegunTipo(tipo, contexto)
  │       └── factories.get("normal") → PrestamoNormalFactory.crearPrestamo(contexto)
  │            └── PrestamoBuilder.paraUsuario().deMaterial().conVencimiento().construir()
  │
  │  PASO 6: Persistir
  │  └── repoPrestamo.agregar(prestamo)
  │
  │  PASO 7: Actualizar estado del material
  │  └── material.marcarComoPrestado()  → EstadoMaterial.PRESTADO
  │  └── repoMaterial.actualizar(material)
  │
  │  PASO 8: Notificar
  │  └── NotificacionEmailService.enviarNotificacion(idUsuario, mensaje)
  │
  ▼
PRESENTACIÓN → MenuConsola muestra Resultado (éxito o error con mensajes)
```

---

## Flujo 2: Devolver Material

El usuario devuelve un material prestado. Se evalúa si hay retraso o daños.

### Recorrido por capas:

```
PRESENTACIÓN → MenuConsola
  │  Captura: idPréstamo
  │  Pregunta: ¿Desea reportar daños? → Si sí, captura lista de Daños
  │  Crea Evaluacion(materialUsable, listaDaños)
  ▼
APLICACIÓN → DevolucionService.registrarDevolucion(idPrestamo, evaluacion)
  │
  │  PASO 1: Obtener préstamo + validar que no fue devuelto ya
  │  └── repoPrestamo.obtenerPorId()
  │
  │  PASO 2: Obtener material y usuario
  │  └── repoMaterial.obtenerPorId() + repoUsuario.obtenerPorId()
  │
  │  PASO 3: Registrar fecha de devolución
  │  └── prestamo.setFechaDevolucionReal(ahora)
  │  └── prestamo.setEstado(COMPLETADA)
  │
  │  PASO 4: Evaluar multas por retraso (Strategy)
  │  └── Si fechaDevolucion > fechaEsperada:
  │       └── GestorMultasService.calcularMulta(contextoRetraso)
  │            └── CalculadorMultaPorRetraso.puedeCalcular? → SÍ → calcular()
  │            └── repoMulta.agregar(multa)
  │
  │  PASO 5: Evaluar multas por daños (Strategy)
  │  └── Si evaluacion != null Y tieneDaños:
  │       └── GestorMultasService.calcularMulta(contextoDano)
  │            └── CalculadorMultaPorDano.puedeCalcular? → SÍ → calcular()
  │                 └── ICalculadorCostoDanoService.calcularCostoTotal(daños)
  │            └── repoMulta.agregar(multa)
  │
  │  PASO 6: Actualizar estado del material
  │  └── Si material usable → marcarComoDisponible()
  │  └── Si material dañado → marcarComoEnReparacion()
  │
  │  PASO 7: Verificar bloqueo del usuario
  │  └── Si totalMultas >= $50,000 → GestorBloqueoService.bloquearUsuario()
  │
  │  PASO 8: Notificar al usuario
  │  └── NotificacionEmailService.enviarNotificacion(resumen)
  │
  ▼
PRESENTACIÓN → MenuConsola muestra resultado + desglose de multas si aplica
```

---

## Flujo 3: Crear Reserva

El usuario reserva un material que no está disponible.

### Recorrido por capas:

```
PRESENTACIÓN → MenuConsola
  │  Captura: idUsuario, idMaterial, tipoReserva ("normal" o "interbibliotecaria")
  ▼
APLICACIÓN → ReservaService.crearReserva(idUsuario, idMaterial, tipoReserva)
  │
  │  PASO 1: Validar reglas de reserva
  │  └── ValidadorReglasService.validarReserva(idUsuario, idMaterial)
  │       └── Ejecuta las mismas estrategias de validación
  │
  │  PASO 2: Verificar que no tiene reserva activa duplicada
  │  └── tieneReservaActiva(idUsuario, idMaterial)
  │
  │  PASO 3: Validar reglas de negocio de reservas
  │  └── ¿Es EBOOK? → Rechazar (solo materiales físicos)
  │  └── ¿Está DISPONIBLE? → Rechazar (sugerir préstamo directo)
  │
  │  PASO 4: Crear reserva según tipo
  │  └── "normal" → new ReservaNormal(...)
  │  └── "interbibliotecaria" → new ReservaInterbibliotecaria(...)
  │
  │  PASO 5: Calcular posición en cola de espera
  │  └── calcularPosicionCola(idMaterial) → cuenta reservas activas + 1
  │
  │  PASO 6: Persistir
  │  └── repoReserva.agregar(reserva)
  │
  │  PASO 7: Notificar
  │  └── NotificacionEmailService.enviarNotificacion(confirmación + posición en cola)
  │
  ▼
PRESENTACIÓN → MenuConsola muestra resultado + posición en cola
```

---

## Flujo 4: Renovar Préstamo

El usuario extiende la fecha de devolución de un préstamo activo.

### Recorrido por capas:

```
PRESENTACIÓN → MenuConsola
  │  Captura: idPréstamo (NO pide idUsuario, se deriva del préstamo)
  ▼
APLICACIÓN → RenovacionService.renovarPrestamo(idPrestamo)
  │
  │  PASO 1: Validar renovación
  │  └── validarRenovacion(idPrestamo)
  │       ├── ¿El préstamo existe?
  │       ├── ¿Está en estado ACTIVA?
  │       ├── ¿No excede máximo de renovaciones por TipoUsuario?
  │       │    └── Estudiante: 2, Profesor: 3, Investigador: 4, Público: 1
  │       ├── ¿No hay reservas pendientes sobre el material?
  │       └── ¿No está vencido por más de 7 días?
  │
  │  PASO 2: Obtener datos del material y usuario
  │  └── repoMaterial.obtenerPorId() + repoUsuario.obtenerPorId()
  │
  │  PASO 3: Calcular nueva fecha de devolución (desde HOY)
  │  └── PoliticaTiempoPorTipoService.obtenerFechaDevolucion(tipoMaterial, tipoUsuario)
  │
  │  PASO 4: Actualizar préstamo
  │  └── prestamo.setFechaDevolucionEsperada(nuevaFecha)
  │  └── prestamo.incrementarRenovaciones()
  │
  │  PASO 5: Persistir
  │  └── repoPrestamo.actualizar(prestamo)
  │
  ▼
PRESENTACIÓN → MenuConsola muestra nueva fecha + renovaciones restantes
```

---

## Flujo Transversal: Manejo de Errores

Todas las operaciones siguen el mismo patrón de manejo de errores:

```
MenuConsola                              Servicio
    │                                       │
    ├── try {                               │
    │     servicio.operacion(params)  ──────►│
    │                                       ├── try {
    │                                       │     validar → ejecutar → persistir
    │                                       │     return Resultado.Exitoso(msg, data)
    │                                       │   } catch (Exception e) {
    │                                       │     return Resultado.Fallido(msg)
    │                                       │   }
    │◄──────── Resultado ────────────────────
    │                                       
    │   if (resultado.getExito())
    │     → Mostrar mensaje de éxito
    │   else
    │     → Mostrar error amigable
    │
    ├── } catch (BibliotecaException e)
    │     → Mostrar error de negocio
    ├── } catch (IllegalArgumentException e)
    │     → Mostrar error de validación
    └──────────────────────────────────────
```

El sistema **nunca termina abruptamente**. Los errores se capturan en dos niveles:
1. **Nivel servicio**: Retorna `Resultado.Fallido()` con mensaje descriptivo.
2. **Nivel consola**: Captura excepciones no manejadas con `try-catch`.

---

## Diagrama de Dependencias entre Capas

```
PRESENTACIÓN
  MenuConsola
    ├── BibliotecaFacade (Facade)
    └── IServicioReportes
          │
APLICACIÓN ▼
  BibliotecaFacade
    ├── IPrestamoService ──► PrestamoService
    │                          ├── IValidadorReglasService (Strategy: reglas)
    │                          ├── IDisponibilidadService
    │                          ├── IPoliticaTiempoService
    │                          ├── IPrestamoFactory (Factory Method: tipo préstamo)
    │                          └── INotificacionService (Decorator: logging+reintentos)
    │
    ├── IDevolucionService ──► DevolucionService
    │                            ├── IInspeccionMaterialService
    │                            ├── IGestorMultasService (Strategy: calculadores)
    │                            ├── IGestorBloqueoService
    │                            └── INotificacionService
    │
    ├── IReservaService ──► ReservaService
    │                         ├── IValidadorReglasService
    │                         ├── IDisponibilidadService
    │                         └── INotificacionService
    │
    └── IRenovacionService ──► RenovacionService
                                 ├── IPoliticaTiempoService
                                 └── IRepositorio<Reserva> (verifica reservas)
          │
DOMINIO    ▼
  Entidades: Material, Usuario, Prestamo, Reserva, Multa
  Value Objects: IdUsuario, IdMaterial, IdTransaccion, IdMulta
  Enums: EstadoMaterial, EstadoUsuario, EstadoTransaccion, TipoMaterial, TipoUsuario
  Builders: MaterialBuilder, PrestamoBuilder, UsuarioBuilder, MultaBuilder
          │
INFRAESTRUCTURA ▼
  IRepositorio<T>
    ├── RepositorioMaterialEnMemoria
    ├── RepositorioUsuarioEnMemoria
    ├── RepositorioPrestamoEnMemoria
    ├── RepositorioReservaEnMemoria
    └── RepositorioMultaEnMemoria
```
