# Arquitectura de Software Orientada a Microservicios aplicando metodología DDD
**Universidad Pontificia Bolivariana — Facultad de Ingeniería — Programa de Ingeniería en Sistemas**

**Presentado por:** Pedro José Gómez López · David Alexander Salazar Villa · Juan Miguel Muñoz Gómez  
**Docente:** Cesar Augusto López Gallego  
**Materia:** Arquitectura de Software  
**Fecha:** XX de Abril del 2026  
**Versión:** v5

---

## Introducción

Elysium es un sistema de gestión bibliotecaria que administra el ciclo completo de vida de las transacciones bibliográficas: préstamos, devoluciones, reservas y renovaciones de materiales.

El presente documento describe el análisis arquitectónico del sistema aplicando la metodología **Domain-Driven Design (DDD)**, partiendo de un monolito con patrones de diseño y principios SOLID ya implementados, esperando proyectar su evolución hacia una **arquitectura orientada a microservicios**.

El análisis cubre desde la tormenta de ideas inicial hasta la identificación de agregados, objetos de valor, eventos de dominio y servicios de dominio, con el **lenguaje ubicuo** que unifica el entendimiento entre el equipo técnico y el negocio bibliotecario.

---

## 1. Estructura Organizacional

La identificación de las áreas organizacionales es el punto de partida del análisis DDD. Cada área representa un espacio de conocimiento especializado dentro de la biblioteca universitaria, delimitando los **Bounded Contexts**.

| Área | Descripción |
|------|-------------|
| **Circulación de material** | El área donde los usuarios realizan transacciones de préstamos, devoluciones y renovaciones con un encargado. |
| **Gestión de Reservas** | El área responsable de la cola de espera de materiales, la notificación de disponibilidad y la expiración de turnos. |
| **Gestión de Material** | Clasificación e inventario del catálogo físico y digital. |
| **Cálculo de Multas** | El área que aplica la lógica diferenciada de sanción por retraso, daño o pérdida según el tipo de usuario. Conocimiento exclusivo de la biblioteca universitaria. |
| **Cobro de Multas** | El área que gestiona el saneamiento de deudas (pago, condonación) y el umbral de bloqueo de cuenta. Proceso administrativo reemplazable por sistemas externos. |
| **Gestión Usuarios** | Control de perfiles, privilegios y estados de cuenta. |
| **Préstamos interbibliotecarios** | Coordinación de recursos con entidades externas. |
| **Notificaciones** | El canal de comunicación oficial con el usuario. |
| **Reportes y estadísticas** | Inteligencia de datos para la toma de decisiones. |

---

## 2. Tormenta de Ideas — Event Storming: Triggers, Eventos y Bifurcaciones

El ejercicio de **Event Storming** permitió mapear los flujos principales del sistema mediante tres categorías:

| Categoría | Color | Descripción |
|-----------|-------|-------------|
| **Triggers** | 🟦 Azul | Acciones del usuario o del sistema que inician un flujo |
| **Eventos** | 🟥 Rosado | Hechos ocurridos en el dominio que tienen consecuencias |
| **Bifurcaciones** | 🟨 Amarillo | Puntos de decisión que determinan caminos alternativos |

El resultado evidenció **9 grupos de eventos cohesivos**, formalizados como los 9 Bounded Contexts del sistema.

> Para visualizar los diagramas interactivos: https://canva.link/gefj73dls7ykpak

### 2.1. Event Storming — Diagrama General (Flujos completos)

El diagrama principal captura todos los flujos del sistema en un solo mapa. A continuación la transcripción completa de los flujos identificados:

**Flujo 1 — Búsqueda y Préstamo de Material:**
```
[Buscar material] (Trigger: David Salazar)
    → [Existe en el catálogo local?] (Bifurcación)
        → SÍ: [Esta disponible?] (Evento: Pedro Gómez)
            → SÍ: [Gestionar Préstamo] (Trigger: Pedro Gómez)
                     → [Buscar info del usuario] (Bifurcación)
                     → [Nuevo préstamo activo] (Evento: Pedro Gómez)
                     → [Actualización del estado del material a "prestado"] (Evento: David Salazar)
                     → [Préstamo Rechazado] (Evento: David Salazar)  ← si falla
            → NO: [Material para reserva] (Evento: Pedro Gómez)
                     → [Crear reserva] (Trigger: David Salazar)
                     → [Nueva Reserva Pendiente] (Evento: David Salazar)
                     → [Añadir a cola] (Trigger: Pedro Gómez)
                     → [En lista de reserva] (Evento: Pedro Gómez)
        → NO: [¿La biblioteca tiene el material?] (Bifurcación: David Salazar)
            → [Material Inaccesible] (Evento: David Salazar) → [Consolidar datos] (Trigger)
            → [Material externo existente] (Evento: David Salazar)
            → [Requiere un préstamo interbibliotecario] (Trigger)
            → [Solicitar a otra biblioteca el material] (Trigger: David Salazar)
            → [Material Disponible] (Evento: Pedro Gómez)
```

**Flujo 2 — Devolución de Material:**
```
[Recibir Devolución] (Trigger: Pedro Gómez)
    → [¿Lo entregó a tiempo?] (Bifurcación: Pedro Gómez)
        → [¿Tiene daños físicos?] (Bifurcación: Pedro Gómez)
            → SÍ: [Devolución con daños] (Evento: Pedro Gómez)
            → NO: [Préstamo Finalizado correctamente] (Evento: Pedro Gómez)
        → [Devolución con retraso] (Bifurcación: Pedro Gómez)
```

**Flujo 3 — Control de Multas:**
```
[generar multa] (Trigger) → [calcular multa] (Bifurcación)
    → [multa pendiente de pago] (Evento)
    → [¿experimentó de bloqueo?] (Bifurcación)
        → SÍ: [bloqueo solicitado] (Evento) → [aplicar bloqueo] (Trigger) → [usuario bloqueado] (Evento)
        → NO: [deuda en cero] (Evento) → [desbloqueo solicitado] (Bifurcación)
                                                → [quitar bloqueo] (Trigger) → [usuario desbloqueado] (Evento)

[registrar pago] (Trigger) → [multa pagada] (Evento)
```

**Flujo 4 — Renovación de Préstamo:**
```
[Solicitar una renovación] (Trigger: David Salazar)
    → [¿observa tipo de material y de cantidad límite de préstamos activos? ¿Renova?] (Bifurcación: David Salazar)
        → SÍ: [actualización de fecha límite de préstamo activo] (Evento: David Salazar)
        → NO: [préstamo bloqueado] (Evento)
```

**Flujo 5 — Gestión de Usuarios:**
```
[añadir usuario] (Trigger) → [¿está disponible?] (Bifurcación)
    → [¿puede hacer el préstamo?] (Bifurcación)
        → SÍ: [límite anotado] → [usuario solicitado] (Evento)
        → NO: [operación denegada] (Evento)
    → [solicitud denegada] (Evento) → [límite excedido] (Evento) → [usuario validado] (Evento)
```

**Flujo 6 — Notificaciones:**
```
[Componer mensaje] (Trigger: David Salazar) → [Notificación enviada a usuario] (Evento: Juan Muñoz)
```

**Flujo 7 — Reportes:**
```
[Consolidar datos] (Trigger) → [Tableros actualizados] (Evento: Pedro Gómez)
```

**Flujo 8 — Préstamo Interbibliotecario:**
```
[Requiere un préstamo interbibliotecario] → [Solicitar a una biblioteca el material] (Trigger: David Salazar)
    → [¿La biblioteca tiene el material?] (Bifurcación: David Salazar)
        → SÍ: [Material externo existente] (Evento: David Salazar)
        → NO: [Material Inaccesible] (Evento: David Salazar)
```

### 2.2. Event Storming — Agrupación por Bounded Context

El segundo diagrama del Event Storming reorganiza todos los eventos en sus respectivos Bounded Contexts:

**Gestión Material:**
- Triggers (azul): Buscar material, Añadir en el catálogo local
- Eventos (rosado): Material Disponible, Material Inaccesible, Material externo existente
- Bifurcaciones (amarillo): ¿Esta disponible?, Material para reserva

**Transacciones bibliográficas (BC1):**
- Triggers: Recibir Devoluciones, Gestionar Préstamo, Solicitar una renovación
- Eventos: Préstamo Finalizado correctamente, Devolución con retraso, Devolución con daño, Préstamo Rechazado, Nuevo préstamo activo
- Bifurcaciones: ¿Tiene daños físicos?, actualización del estado del material a "prestado", actualización de fecha límite de préstamos activos, Crear reserva, Nueva Reserva Pendiente, Añadir a cola, En lista de reserva, en espera de reserva

**Control de multas:**
- Triggers: generar multa, calcular multa, registrar pago, multa pagada
- Eventos: multa pendiente de pago, ¿experimentó de bloqueo?, bloqueo solicitado, bloqueo solicitado (desbloqueo)
- Bifurcaciones: aplicar bloqueo, usuario bloqueado, quitar bloqueo, usuario desbloqueado

**Préstamo interbibliotecario:**
- Triggers (azul): Requiere un préstamo interbibliotecario, Solicitar a una biblioteca el material
- Eventos (rosado): ¿La biblioteca tiene el material?

**Estadísticas:**
- Triggers: Consolidar datos
- Eventos: Tableros actualizados

**Gestión Usuarios:**
- Triggers: añadir usuario
- Bifurcaciones: ¿está disponible?, ¿puede hacer el préstamo?
- Eventos: operación denegada, límite excedido, usuario validado

**Notificación:**
- Triggers: Componer mensaje
- Eventos: Notificación enviada a usuario

---

## 3. Identificación de Áreas de Conocimiento en Elysium

### 3.1. Dominios y Bounded Contexts

| Dominios y subdominios | Qué abarca | Bounded Context |
|------------------------|------------|-----------------|
| Gestión de materiales | Catálogo, disponibilidad, tipos, estados físicos | **BC2: Gestión de Materiales** |
| Gestión de usuarios | Tipos, límites, bloqueos, historial | **BC3: Gestión de Usuarios** |
| Circulación bibliográfica | Préstamos, devoluciones, renovaciones | **BC1: Circulación Bibliográfica** |
| Cálculo de Multas | Lógica diferenciada de sanción por retraso, daño y pérdida según tipo de usuario | **BC4: Cálculo de Multas** / **BC9: Cobro de Multas** |
| Notificaciones | Avisos de disponibilidad, vencimiento, mora | **BC6: Notificaciones** |
| Reportes y estadísticas | Consultas, métricas de uso | **BC7: Reportes y Estadísticas** |
| Préstamos interbibliotecarios | Coordinación con otras bibliotecas | **BC5: Préstamos Interbibliotecarios** |
| Gestión de Reservas | Cola de espera, notificación, expiración | **BC8: Gestión de Reservas** |

### 3.2. Identificación de Subdominios

| Subdominio | Tipo | Justificación |
|------------|------|---------------|
| **Circulación Bibliográfica** | **Core** | Las reglas de quién presta qué, por cuánto tiempo y con qué consecuencias son el diferenciador del negocio |
| **Cálculo de Multas** | **Core** | El cálculo diferenciado por tipo de usuario e infracción no lo resuelve ningún sistema genérico |
| **Gestión de Reservas** | **Core** | La lógica de cola de prioridad, expiración en 24h y reasignación de turnos son reglas propias de la biblioteca que ningún sistema genérico de turnos resuelve |
| Gestión de materiales | Soporte | Necesario pero no diferenciador; cualquier sistema necesita un catálogo |
| Gestión de usuarios | Soporte | Necesario pero reemplazable por un sistema de identidad estándar |
| Préstamos interbibliotecarios | Soporte | Extiende BC1 con coordinación externa; no existe sin las transacciones principales |
| Cobro de Multas | Soporte | Gestiona el ciclo de pago y condonación. Es reemplazable por un sistema externo de cobro sin afectar el cálculo de multas (BC4) |
| Notificaciones | Genérico | Reemplazable por Sendgrid, Firebase u otro servicio externo sin perder valor de negocio |
| Reportes y estadísticas | Genérico | Reemplazable por Power BI, Metabase u otra herramienta de BI estándar |

---

## 4. Elementos del Dominio

### 4.1. Entidades

#### Diagrama de Entidades por Bounded Context

```
┌─────────────────────────────────────────────────────────────────────────────────────────┐
│  Diagrama de Entidades — Elysium DDD                                                     │
│                                                                                          │
│  ┌──────────────────┐    ┌──────────────────────┐    ┌──────────────────────┐            │
│  │ Gestión de       │    │ Circulación           │    │ Gestión de           │            │
│  │ Reservas         │    │ Bibliográfica         │    │ materiales           │            │
│  │                  │    │                       │    │                      │            │
│  │  ·Material       │    │  ·Renovación          │    │  ·Material           │            │
│  │  ·Reserva        │    │  ·Material            │    │                      │            │
│  │  ·Usuario        │    │  ·Usuario             │    │                      │            │
│  │                  │    │  ·Préstamo            │    │                      │            │
│  └──────────────────┘    └──────────────────────┘    └──────────────────────┘            │
│                                                                                          │
│  ┌──────────────────┐    ┌──────────────────────┐    ┌──────────────────────┐            │
│  │ Gestión de       │    │ Cálculo de Multas     │    │ Notificaciones       │            │
│  │ usuarios         │    │                       │    │                      │            │
│  │                  │    │  ·Material            │    │  ·Destinatario       │            │
│  │  ·Usuario        │    │  ·Daño                │    │  ·Notificación       │            │
│  │                  │    │  ·Préstamo            │    │                      │            │
│  │                  │    │  ·Multa               │    │                      │            │
│  │                  │    │  ·Usuario             │    │                      │            │
│  └──────────────────┘    └──────────────────────┘    └──────────────────────┘            │
│                                                                                          │
│                          ┌──────────────────────┐    ┌──────────────────────┐            │
│                          │ Cobro de Multas       │    │ Préstamo             │            │
│                          │                       │    │ interbibliotecario   │            │
│                          │  ·Multa               │    │                      │            │
│                          │  ·Usuario             │    │  ·SolicitudExterna   │            │
│                          │  ·RegistroPago        │    │  ·MaterialExterno    │            │
│                          └──────────────────────┘    └──────────────────────┘            │
└─────────────────────────────────────────────────────────────────────────────────────────┘
```

*Nota: Las elipses del diagrama original representan los Bounded Contexts como contenedores con sus entidades internas. Las conexiones entre elipses representan dependencias entre BCs.*

#### Tabla de Bounded Contexts — Responsabilidades y Relaciones

| BC | Tipo | Responsabilidad Principal | Entidades y Objetos de Valor | Relaciones |
|----|------|--------------------------|------------------------------|------------|
| **BC1: Circulación Bibliográfica** | Core | Registro de préstamos, procesamiento de devoluciones y renovaciones. Aplica políticas de plazo y límites de renovación según el tipo de usuario y material. | Préstamo | Es Cliente de BC2 y BC3 mediante relación síncrona. Delega a BC5 si el material es externo. Emite `MaterialDevuelto` (asíncrono) hacia BC4, BC8, BC6 y BC7. BC8 escucha `MaterialDevuelto` para iniciar la cola de reservas. |
| **BC2: Gestión de materiales** | Supporting | Administración del catálogo, inventario físico/digital y disponibilidad. | Material | Proveedor de BC1. Actualiza estados tras eventos de devolución. Actúa como Servicio de Host Abierto publicando datos para BC7. Escucha evento de BC1 para actualizar disponibilidad. |
| **BC3: Gestión de usuarios** | Supporting | Registro de usuarios, privilegios por tipo, historial de actividad, y estados operativos de la cuenta. | Usuario | Proveedor de BC1 en relación síncrona. Reacciona a sanciones de BC4 para bloquear cuentas usando una **Capa Anticorrupción** para traducir el mensaje. |
| **BC4: Cálculo de Multas** | Core | Cálculo diferenciado de multas por tipo de usuario e infracción (retraso, daño, pérdida). Evaluación de daños físicos al momento de la devolución. | Multa, Daño | Consumidor de `MaterialDevuelto` de BC1. Emite `MultaGenerada` hacia BC9, BC6 y BC7. |
| **BC5: Préstamos interbibliotecarios** | Supporting | Coordinación logística y rastreo de materiales con instituciones aliadas. | SolicitudExterna | Proveedor de BC1 mediante relación síncrona para solicitudes fuera del catálogo local. |
| **BC6: Notificaciones** | Generic | Envío de mensajes por diversos canales (Email/SMS), notificación de turno en reservas, avisos de vencimiento, confirmaciones de multa. Consume eventos de otros contextos. | Notificacion | Suscriptor de eventos de todos los demás BCs. Comunicación reactiva y asíncrona. |
| **BC7: Reportes y estadísticas** | Generic | Consolidación de métricas y analítica de uso del sistema. | N/A | Observador pasivo que consume datos de todos los BCs. Transforma eventos en modelos de lectura. |
| **BC8: Gestión de Reservas** | Core | Gestión de la cola de espera, notificación de disponibilidad al primer usuario en cola y expiración de turnos transcurridas 24 horas. | Reserva | Consumidor de `MaterialDevuelto` de BC1. Emite `ReservaNotificada`, `ReservaExpirada` hacia BC6. Emite `ReservaExpirada` hacia BC1. Emite `ReservaCreada` hacia BC6 y BC7. |
| **BC9: Cobro de Multas** | Supporting | Gestión del ciclo de pago, condonación y umbral de bloqueo de cuenta por deuda acumulada. | RegistroPago | Consumidor de `MultaGenerada` de BC4. Emite `MultaPagada` y `UsuarioBloqueadoPorDeuda` hacia BC3 y BC6. Emite `PeticionDesbloqueoUsuario` hacia BC3 cuando la deuda total llega a cero. |

#### Bounded Context Canvas — BC1 Circulación Bibliográfica

```
┌─────────────────────────────────────────────────────────────────────────────────────────┐
│ Name: BC1 Circulación bibliográfica                      V5                             │
│ github.com/ddd-crew/bounded-context-canvas                                              │
├────────────────────────────┬──────────────────────────────────────┬────────────────────┤
│ PURPOSE                    │ STRATEGIC CLASSIFICATION             │ DOMAIN ROLES        │
│                            │                                      │                     │
│ Gestionar el ciclo de vida │ Domain:      Core                    │ Role Types:         │
│ central de los materiales  │ Business     Engagement              │ Execution Context   │
│ de la biblioteca (entradas │ Model:       (Uso del servicio)      │ (Contexto de        │
│ y salidas), maximizando la │ Evolution:   Custom Built (A medida) │ ejecución           │
│ disponibilidad de los      │                                      │ transaccional       │
│ recursos y garantizando un │                                      │ principal).         │
│ acceso justo mediante el   │                                      │                     │
│ control de tiempos y colas │                                      │                     │
│ de espera.                 │                                      │                     │
├────────────────────────────┼──────────────────────────────────────┼────────────────────┤
│ INBOUND COMMUNICATION      │ UBIQUITOUS LANGUAGE                  │ OUTBOUND COMM.      │
│                            │                                      │                     │
│ Collaborator  Messages     │ • Préstamo: contrato temporal de     │ Messages  Collabor. │
│ UI/Biblioteca RegistrarPréstamo      material.                    │ ConsultarDisponib.  Gest. Materiales │
│               CrearReserva│ • Reserva: separación en cola de     │ ConsultarEstadoUsr  Gest. Reservas  │
│               RecibirDevolución      material físico.             │ SolicitarMat.Ext.   Ctrl. Multas   │
│                            │ • Renovación: extensión de plazo.    │ PrestamoRegistrado  Notificaciones │
│ Gest.Material MaterialDispInfo      • Devolución: finaliza préstamo.│ PrestamoRenovado                  │
│                            │ • Plazo: días asignados según        │ RenovacionRechaz.                  │
│ Gest.Usuarios EstadoUsuarioInfo      política tipo material/usuario.│ MaterialDevuelto                  │
│                            │                                      │ VencimientoCercano                 │
│ Préstamo      MaterialDisp.│ BUSINESS DECISIONS                   │                     │
│ Interbibliot. Info         │ • Un préstamo exige usuario activo   │                     │
│               EstadoUsuarioInfo      y material disponible.       │                     │
│               MaterialExterno        • Las reservas caducan a 24h │                     │
│               Recibido     │   tras notificación.                 │                     │
│               ReservaExpirada        • La renovación falla si hay │                     │
│                            │   otro usuario en cola de reserva.   │                     │
│                            │ • Renovaciones máximas: Estudiante 2,│                     │
│                            │   Profesor 3, Investigador 4, Público 1│                   │
├────────────────────────────┼──────────────────────────────────────┼────────────────────┤
│ ASSUMPTIONS                │ VERIFICATION METRICS                 │ OPEN QUESTIONS      │
│                            │                                      │                     │
│ Asumimos que la latencia   │ • Tiempo medio de ejecución de un   │ (ninguna            │
│ de red con BC2 y BC3       │   préstamo en mostrador.             │ documentada)        │
│ siempre será < 200ms para  │ • % de reservas que expiran sin ser │                     │
│ no afectar la fila del     │   recogidas.                         │                     │
│ mostrador.                 │ • Tasa de renovaciones rechazadas    │                     │
│                            │   por cola de reserva.               │                     │
└────────────────────────────┴──────────────────────────────────────┴────────────────────┘
```

#### Bounded Context Canvas — BC8 Gestión de Reservas

```
┌─────────────────────────────────────────────────────────────────────────────────────────┐
│ Name: BC8 Gestión de reservas                            V5                             │
├────────────────────────────┬──────────────────────────────────────┬────────────────────┤
│ PURPOSE                    │ STRATEGIC CLASSIFICATION             │ DOMAIN ROLES        │
│                            │                                      │                     │
│ Garantizar el acceso justo │ Domain:      Core                    │ Role Types:         │
│ y ordenado a los materiales│ Business     Engagement              │ Execution Context   │
│ físicos no disponibles,    │ Model:       (Uso del servicio)      │ (Contexto de        │
│ administrando una cola de  │ Evolution:   Custom Built (A medida) │ ejecución           │
│ prioridad por orden de     │                                      │ transaccional       │
│ solicitud, notificando al  │                                      │ principal).         │
│ siguiente usuario cuando el│                                      │                     │
│ material es devuelto y     │                                      │                     │
│ liberando automáticamente  │                                      │                     │
│ el turno si no es recogido │                                      │                     │
│ en el período establecido. │                                      │                     │
├────────────────────────────┼──────────────────────────────────────┼────────────────────┤
│ INBOUND COMMUNICATION      │ UBIQUITOUS LANGUAGE                  │ OUTBOUND COMM.      │
│                            │                                      │                     │
│ UI/Biblioteca CrearReserva │ • Reserva: separación en cola de     │ ReservaCreada       │
│               CancelarRes. │   material físico prestado.          │                     │
│               NotificarDisp│ • Cola de prioridad: posición        │ ReservaNotificada → Circulación Bibl.│
│               ExpirarReserva│  secuencial (PosicionCola).         │                     │
│                            │ • Notificación de disponibilidad:    │ ReservaExpirada  → Notificaciones  │
│ BC1 Transacc. MaterialDevuelto        aviso al primero en cola.  │                     │
│ bibliográficas ReservaCancelada       • Período de notificación: │ ReservaCancelada → Reportes/Est.   │
│               ReservaExpirada         24h para retirar material. │                     │
│                            │ • Expiración: vencimiento automático │                     │
│                            │   a las 24h.                         │                     │
│                            │ • Cancelación: abandono voluntario   │                     │
│                            │   del turno.                         │                     │
│                            │                                      │                     │
│                            │ BUSINESS DECISIONS                   │                     │
│                            │ • Solo materiales físicos son        │                     │
│                            │   reservables (EBooks excluidos).    │                     │
│                            │ • Solo si el material está PRESTADO; │                     │
│                            │   si está disponible, se sugiere     │                     │
│                            │   préstamo directo.                  │                     │
│                            │ • Solo la primera en cola puede ser  │                     │
│                            │   notificada.                        │                     │
│                            │ • GestorColaReservas reasigna        │                     │
│                            │   posiciones al cancelar o expirar;  │                     │
│                            │   ningún agregado individual puede   │                     │
│                            │   hacerlo.                           │                     │
├────────────────────────────┼──────────────────────────────────────┼────────────────────┤
│ ASSUMPTIONS                │ VERIFICATION METRICS                 │ OPEN QUESTIONS      │
│ Latencia con BC2 y BC3     │ • Tiempo medio de ejecución préstamo │ (ninguna)           │
│ < 200ms.                   │ • % reservas que expiran sin recogida│                     │
│                            │ • Tasa renovaciones rechazadas       │                     │
└────────────────────────────┴──────────────────────────────────────┴────────────────────┘
```

#### Bounded Context Canvas — BC4 Cálculo de Multas

```
┌─────────────────────────────────────────────────────────────────────────────────────────┐
│ Name: BC4 Cálculo de multas                              V5                             │
├────────────────────────────┬──────────────────────────────────────┬────────────────────┤
│ PURPOSE                    │ STRATEGIC CLASSIFICATION             │ DOMAIN ROLES        │
│ Hacer cumplir las políticas│ Domain:      Core                    │ Role Types:         │
│ sancionatorias mediante el │ Business     Compliance / Revenue    │ Execution Context   │
│ cálculo diferenciado de    │ Model:                               │                     │
│ penalizaciones por retraso,│ Evolution:   Custom Built (A medida) │                     │
│ daño o pérdida, aplicando  │                                      │                     │
│ reglas específicas según   │                                      │                     │
│ tipo de usuario y          │                                      │                     │
│ protegiendo el inventario. │                                      │                     │
├────────────────────────────┼──────────────────────────────────────┼────────────────────┤
│ INBOUND COMMUNICATION      │ UBIQUITOUS LANGUAGE                  │ OUTBOUND COMM.      │
│                            │                                      │                     │
│ UI/Multas  GenerarMulta    │ • Multa: sanción económica por       │ MultaGenerada    → Cobro de multas  │
│            ConsultarDeudaPendiente    incumplimiento.             │ PeticionBloqueoUsr → Gest. Usuarios│
│            (desde BC1)     │ • Retraso: días de mora tras fecha   │ PeticionDesbloqueoUsr → Notif.     │
│                            │   esperada.                          │ DeudaPendienteInfo → Circulación   │
│ Circulación PrestamoDevueltoConRetraso• Evaluación de daño:       │                                    │
│ Bibliográf. DevolucionConDaño         inspección física al devolver.│                                  │
│             MaterialDevuelto          • Recargo por pérdida:      │                     │
│                            │   precio base + % según tipo usuario.│                     │
│                            │ • Umbral de bloqueo: deuda máxima   │                     │
│                            │   permitida.                         │                     │
│                            │                                      │                     │
│                            │ BUSINESS DECISIONS                   │                     │
│                            │ • Recargo pérdida: Estudiante 20%,   │                     │
│                            │   Profesor 10%, Investigador 0%,     │                     │
│                            │   Público 30%.                       │                     │
│                            │ • Valor del material se congela al   │                     │
│                            │   momento de la pérdida.             │                     │
│                            │ • Una devolución puede generar multa │                     │
│                            │   por retraso + daño simultáneamente.│                     │
│                            │ • Superar umbral genera             │                     │
│                            │   PeticionBloqueoUsuario inmediata   │                     │
│                            │   a BC3.                             │                     │
├────────────────────────────┼──────────────────────────────────────┼────────────────────┤
│ ASSUMPTIONS                │ VERIFICATION METRICS                 │ OPEN QUESTIONS      │
│ Las tarifas diarias de     │ • % de deuda recuperada vs. generada.│ (ninguna)           │
│ retraso son configurables  │ • Distribución de multas por tipo    │                     │
│ por tipo de material. El   │   (retraso/daño/pérdida).            │                     │
│ valor histórico del        │                                      │                     │
│ material queda fijado al   │                                      │                     │
│ momento de la pérdida.     │                                      │                     │
└────────────────────────────┴──────────────────────────────────────┴────────────────────┘
```

#### Bounded Context Canvas — BC9 Cobro de Multas

```
┌─────────────────────────────────────────────────────────────────────────────────────────┐
│ Name: BC9 Cobro de multas                                V5                             │
├────────────────────────────┬──────────────────────────────────────┬────────────────────┤
│ PURPOSE                    │ STRATEGIC CLASSIFICATION             │ DOMAIN ROLES        │
│ Gestionar el ciclo de vida │ Domain:      Supporting              │ Role Types:         │
│ del saneamiento de deuda   │ Business     Revenue                 │ Execution context.  │
│ generada por BC4,          │ Model:                               │                     │
│ procesando pagos y         │ Evolution:   Custom Built (A medida) │                     │
│ condonaciones, determinando│                                      │                     │
│ cuándo bloquear/desbloquear│                                      │                     │
│ según umbral de deuda.     │                                      │                     │
├────────────────────────────┼──────────────────────────────────────┼────────────────────┤
│ INBOUND COMMUNICATION      │ UBIQUITOUS LANGUAGE                  │ OUTBOUND COMM.      │
│                            │                                      │                     │
│ UI/Multas  PagarMulta      │ • Registro de pago: constancia del   │ MultaPagada                         │
│            CondonarDeuda   │   saneamiento de una multa.          │ UsuarioBloqueadoPorDeuda → Gest. Usr│
│                            │ • Condonación: cancelación parcial   │ PeticionDesbloqueoUsuario → Notif.  │
│ Cálculo    MultaGenerada   │   o total de la deuda.               │ ConfirmacionPago                    │
│ multas     (de BC4)        │ • Deuda acumulada: suma de multas    │ MultaRegistrada                     │
│                            │   pendientes de un usuario.          │                     │
│                            │ • Umbral de bloqueo: monto máximo    │                     │
│                            │   antes de bloquear cuenta.          │                     │
│                            │                                      │                     │
│                            │ BUSINESS DECISIONS                   │                     │
│                            │ • Una multa PAGADA es estado         │                     │
│                            │   terminal — no puede revertirse.    │                     │
│                            │ • Desbloqueo se solicita a BC3 cuando│                     │
│                            │   la deuda total llega a cero.       │                     │
│                            │ • PoliticaBloqueo determina si       │                     │
│                            │   bloquear, desbloquear o habilitar  │                     │
│                            │   condonación parcial.               │                     │
├────────────────────────────┼──────────────────────────────────────┼────────────────────┤
│ ASSUMPTIONS                │ VERIFICATION METRICS                 │ OPEN QUESTIONS      │
│ Este BC puede ser          │ • Tiempo promedio entre generación   │ (ninguna)           │
│ reemplazado por una        │   de multa y pago.                   │                     │
│ pasarela de pago externa   │ • % de multas condonadas vs. pagadas.│                     │
│ (PSE, etc.) sin afectar la │ • Número de usuarios bloqueados      │                     │
│ lógica de cálculo de BC4.  │   activos.                           │                     │
└────────────────────────────┴──────────────────────────────────────┴────────────────────┘
```

#### Bounded Context Canvas — BC2 Gestión de Materiales

```
┌─────────────────────────────────────────────────────────────────────────────────────────┐
│ Name: BC2 Gestión de materiales                          V5                             │
├────────────────────────────┬──────────────────────────────────────┬────────────────────┤
│ PURPOSE                    │ STRATEGIC CLASSIFICATION             │ DOMAIN ROLES        │
│ Mantener la única fuente de│ Domain:      Supporting              │ Role Types:         │
│ verdad sobre el inventario │ Business     Cost Reduction          │ Execution Context   │
│ físico y digital,          │ Model:       (Gestión de inventario) │ (Contexto de        │
│ clasificando la metadata   │ Evolution:   Product / Commodity     │ ejecución           │
│ bibliográfica para         │                                      │ transaccional       │
│ facilitar su               │                                      │ principal).         │
│ descubrimiento y gestión   │                                      │                     │
│ de ciclo de vida.          │                                      │                     │
├────────────────────────────┼──────────────────────────────────────┼────────────────────┤
│ INBOUND COMMUNICATION      │ UBIQUITOUS LANGUAGE                  │ OUTBOUND COMM.      │
│                            │                                      │                     │
│ UI/Multas  AgregarMaterial │ • Material: objeto físico/digital    │ MaterialDispInfo → Circulación Bibl.│
│ BD Catálogo DarDeBajaMat.  │   (Libro, DVD, Revista, EBook).      │ CatalogoActualizado → Reportes/Est. │
│             ConsultarDisp. │ • Estado físico: Disponible,         │                     │
│ Circulación MaterialDevuelto         Prestado, En Reparación,     │                     │
│ Bibliogr.                  │   Perdido.                           │                     │
│ Préstamo    MaterialExternoIngresado  • Baja de material: retiro  │                     │
│ Interbibl.                 │   permanente del catálogo.           │                     │
│                            │ • Licencia digital: permiso de uso   │                     │
│                            │   concurrente para EBooks.           │                     │
│                            │ • Disponibilidad: indicador en tiempo│                     │
│                            │   real para BC1.                     │                     │
│                            │                                      │                     │
│                            │ BUSINESS DECISIONS                   │                     │
│                            │ • ISBN/ISSN como identificador único.│                     │
│                            │ • Material en estado PERDIDO no puede│                     │
│                            │   prestarse ni reservarse.           │                     │
│                            │ • EBooks gestionan licencias         │                     │
│                            │   disponibles, no estado físico.     │                     │
├────────────────────────────┼──────────────────────────────────────┼────────────────────┤
│ ASSUMPTIONS                │ VERIFICATION METRICS                 │ OPEN QUESTIONS      │
│ Se usa ISBN/ISSN como      │ • Precisión del inventario           │ (ninguna)           │
│ identificador único global │   (discrepancias BD vs. estantería). │                     │
│ para libros y revistas.    │ • Tiempo de actualización de         │                     │
│                            │   disponibilidad tras devolución.    │                     │
└────────────────────────────┴──────────────────────────────────────┴────────────────────┘
```

#### Bounded Context Canvas — BC3 Gestión de Usuarios

```
┌─────────────────────────────────────────────────────────────────────────────────────────┐
│ Name: BC3 Gestión de usuarios                            V5                             │
├────────────────────────────┬──────────────────────────────────────┬────────────────────┤
│ PURPOSE                    │ STRATEGIC CLASSIFICATION             │ DOMAIN ROLES        │
│ Administrar la identidad,  │ Domain:      Supporting              │ Role Types:         │
│ roles y el estado de cuenta│ Business     Engagement              │ • Execution /       │
│ de los miembros de la      │ Model:       (Uso del servicio)      │   Gateway Context.  │
│ comunidad universitaria    │ Evolution:   Product                 │                     │
│ para autorizar su acceso a │                                      │                     │
│ los servicios de la        │                                      │                     │
│ biblioteca.                │                                      │                     │
├────────────────────────────┼──────────────────────────────────────┼────────────────────┤
│ INBOUND COMMUNICATION      │ UBIQUITOUS LANGUAGE                  │ OUTBOUND COMM.      │
│                            │                                      │                     │
│ BD Usuarios ConsultarEstadoUsuario    • Usuario: miembro con       │ EstadoUsuarioInfo → Circulación    │
│ Circulación Bibliogr.      │   acceso al sistema.                 │ PerfilUsuarioActualizado → Reportes│
│ Cálculo     PeticionBloqueoUsuario    • Privilegios: límites      │                     │
│ multas                     │   configurados por rol.              │                     │
│ Cobro       PeticionDesbloqueoUsuario • Estado de cuenta: Activo, │                     │
│ multas                     │   Suspendido, Bloqueado.             │                     │
│                            │                                      │                     │
│                            │ BUSINESS DECISIONS                   │                     │
│                            │ • BC3 solo ejecuta el bloqueo; la    │                     │
│                            │   razón viene de BC4/BC9.            │                     │
│                            │ • Investigadores tienen más          │                     │
│                            │   privilegios base que Estudiantes.  │                     │
│                            │ • Sincronización automática de       │                     │
│                            │   altas/bajas desde BD central de la │                     │
│                            │   universidad.                       │                     │
├────────────────────────────┼──────────────────────────────────────┼────────────────────┤
│ ASSUMPTIONS                │ VERIFICATION METRICS                 │ OPEN QUESTIONS      │
│ La creación de estudiantes │ • Número de usuarios bloqueados      │ ¿Se integrará con   │
│ y egresados se sincroniza  │   vs. activos.                       │ bases de datos      │
│ desde la BD central de la  │ • Tiempo de respuesta a consultas    │ externas (Google    │
│ universidad vía SSO.       │   de autorización de BC1.            │ Books) para         │
│                            │                                      │ autocompletar       │
│                            │                                      │ metadatos?          │
└────────────────────────────┴──────────────────────────────────────┴────────────────────┘
```

#### Bounded Context Canvas — BC5 Préstamos Interbibliotecarios

```
┌─────────────────────────────────────────────────────────────────────────────────────────┐
│ Name: BC5 Préstamos interbibliotecarios                  V5                             │
├────────────────────────────┬──────────────────────────────────────┬────────────────────┤
│ PURPOSE                    │ STRATEGIC CLASSIFICATION             │ DOMAIN ROLES        │
│ Expandir las fronteras del │ Domain:      Supporting              │ Role Types:         │
│ catálogo local colaborando │ Business     Engagement              │ Gateway Context.    │
│ con otras instituciones    │ Model:       (Uso del servicio)      │                     │
│ para conseguir material    │ Evolution:   Custom Built            │                     │
│ solicitado por los         │                                      │                     │
│ usuarios, gestionando la   │                                      │                     │
│ logística y los costos.    │                                      │                     │
├────────────────────────────┼──────────────────────────────────────┼────────────────────┤
│ INBOUND COMMUNICATION      │ UBIQUITOUS LANGUAGE                  │ OUTBOUND COMM.      │
│                            │                                      │                     │
│ Circulación SolicitarMat.  │ • Solicitud externa: petición formal │ MaterialExternoRecibido → Circulación│
│ Bibliogr.   Externo        │   a institución aliada.              │ SolicitudExternaRechazada → Gest.Mat│
│                            │ • Biblioteca destino: institución    │                   → Notificaciones  │
│ API Univ.   MaterialConfirmado        proveedora del material.    │                     │
│ Externas    MaterialRechazado         • Costo de transferencia:   │                     │
│                            │   valor logístico del envío.         │                     │
│                            │ • Rastreo: trazabilidad logística    │                     │
│                            │   (Solicitado→En Tránsito→Recibido   │                     │
│                            │   →Devuelto).                        │                     │
│                            │                                      │                     │
│                            │ BUSINESS DECISIONS                   │                     │
│                            │ • No se puede renovar sin aprobación │                     │
│                            │   de la biblioteca origen.           │                     │
│                            │ • Costo de transferencia debe ser    │                     │
│                            │   aceptado antes de la solicitud.    │                     │
│                            │ • Comunicación manual si la          │                     │
│                            │   institución aliada no tiene API    │                     │
│                            │   compartida.                        │                     │
├────────────────────────────┼──────────────────────────────────────┼────────────────────┤
│ ASSUMPTIONS                │ VERIFICATION METRICS                 │ OPEN QUESTIONS      │
│ Comunicación manual por    │ • Tiempo promedio de recepción del   │ (ninguna)           │
│ email/teléfono si no hay   │   material externo.                  │                     │
│ API compartida con la      │ • Tasa de éxito de solicitudes       │                     │
│ institución aliada.        │   externas.                          │                     │
└────────────────────────────┴──────────────────────────────────────┴────────────────────┘
```

#### Bounded Context Canvas — BC6 Notificaciones

```
┌─────────────────────────────────────────────────────────────────────────────────────────┐
│ Name: BC6 Notificaciones                                 V5                             │
├────────────────────────────┬──────────────────────────────────────┬────────────────────┤
│ PURPOSE                    │ STRATEGIC CLASSIFICATION             │ DOMAIN ROLES        │
│ Garantizar la comunicación │ Domain:      Generic                 │ Role Types:         │
│ oportuna y confiable con el│ Business     Engagement              │ Execution Context.  │
│ usuario a través de        │ Model:                               │                     │
│ múltiples canales,         │ Evolution:   Commodity (Puede        │                     │
│ abstrayendo la complejidad │              delegarse a un SaaS)    │                     │
│ del envío de los demás     │                                      │                     │
│ contextos del sistema.     │                                      │                     │
├────────────────────────────┼──────────────────────────────────────┼────────────────────┤
│ INBOUND COMMUNICATION      │ UBIQUITOUS LANGUAGE                  │ OUTBOUND COMM.      │
│                            │                                      │                     │
│ Circulación VencimientoCercano         • Notificación: mensaje a  │ EnviarEmail → Canal ext. Email/SMS │
│ Bibliogr.   PrestamoRenovado           entregar al usuario.       │ EnviarSMS                          │
│             RenovacionRechazada        • Canal: Email o SMS.      │                     │
│             SolicitudExternaRechazada  • Plantilla: formato       │                     │
│ Cálculo multas AvisoMultaGenerada      predefinido con variables  │                     │
│                            │   del evento.                        │                     │
│ Reservas   ReservaNotificada           • Destinatario: usuario    │                     │
│            ReservaCancelada            reducido a nombre + email  │                     │
│            ReservaExpirada             (sin historial).           │                     │
│            ReservaCreada              │                            │                     │
│ Cobro      ConfirmacionPago│ BUSINESS DECISIONS                   │                     │
│ multas                     │ • Los eventos ya traen nombre y      │                     │
│                            │   email — no se hacen callbacks a    │                     │
│                            │   BC3.                               │                     │
│                            │ • Respeta preferencias de canal del  │                     │
│                            │   usuario.                           │                     │
│                            │ • Asíncrono: nunca bloquea si el     │                     │
│                            │   proveedor falla.                   │                     │
├────────────────────────────┼──────────────────────────────────────┼────────────────────┤
│ ASSUMPTIONS                │ VERIFICATION METRICS                 │ OPEN QUESTIONS      │
│ Los eventos ya incluyen    │ • Tasa de entrega exitosa            │ (ninguna)           │
│ datos de contacto del      │   (delivery rate).                   │                     │
│ usuario — no se consulta   │ • Tasa de rebote (bounces).          │                     │
│ BC3 en el momento del      │                                      │                     │
│ envío.                     │                                      │                     │
└────────────────────────────┴──────────────────────────────────────┴────────────────────┘
```

#### Bounded Context Canvas — BC7 Reportes y Estadísticas

```
┌─────────────────────────────────────────────────────────────────────────────────────────┐
│ Name: BC7 Reportes y estadísticas                        V5                             │
├────────────────────────────┬──────────────────────────────────────┬────────────────────┤
│ PURPOSE                    │ STRATEGIC CLASSIFICATION             │ DOMAIN ROLES        │
│ Proporcionar inteligencia  │ Domain:      Generic                 │ Role Types:         │
│ de negocio y visibilidad   │ Business     Cost Reduction /        │ Analysis Context.   │
│ operativa a la gerencia    │ Model:       Optimización            │                     │
│ consolidando datos de todos│ Evolution:   Commodity               │                     │
│ los dominios sin afectar el│                                      │                     │
│ rendimiento transaccional. │                                      │                     │
├────────────────────────────┼──────────────────────────────────────┼────────────────────┤
│ INBOUND COMMUNICATION      │ UBIQUITOUS LANGUAGE                  │ OUTBOUND COMM.      │
│                            │                                      │                     │
│ Todos los  PrestamoRegistrado          • Métrica: valor numérico  │ DatosVisusMaterializadas → UI/Ger. │
│ bounded    MaterialDevuelto            calculado del historial    │                     │
│ context    CatalogoActualizado         de eventos.                │                     │
│            PerfilUsuarioActualizado    • Tablero (dashboard):     │                     │
│            ReservaCreada/Expirada/     vista gráfica de métricas  │                     │
│            Cancelada                   filtradas.                 │                     │
│            MultaGenerada              • Modelo de lectura: datos  │                     │
│            MultaPagada                 aplanados para consulta    │                     │
│                                        rápida.                    │                     │
│ UI/Gerencia SolicitarDashboard         • Filtro estadístico:      │                     │
│                            │   parámetros de tiempo, tipo de      │                     │
│                            │   material o usuario.                │                     │
│                            │                                      │                     │
│                            │ BUSINESS DECISIONS                   │                     │
│                            │ • Consistencia eventual — no         │                     │
│                            │   requiere datos en tiempo real.     │                     │
│                            │ • No modifica estado de ningún       │                     │
│                            │   otro contexto.                     │                     │
│                            │ • BD propia (ElasticSearch o         │                     │
│                            │   MongoDB) para lecturas complejas.  │                     │
├────────────────────────────┼──────────────────────────────────────┼────────────────────┤
│ ASSUMPTIONS                │ VERIFICATION METRICS                 │ OPEN QUESTIONS      │
│ BD de reportes independ.   │ • Tiempo de carga de los dashboards. │ (ninguna)           │
│ (ElasticSearch o MongoDB)  │ • Latencia de sincronización de      │                     │
│ para no afectar rendim.    │   eventos (time-to-insight).         │                     │
│ transaccional del Core.    │                                      │                     │
└────────────────────────────┴──────────────────────────────────────┴────────────────────┘
```

---

## 4.2. Lenguaje Ubicuo

### Lenguaje Ubicuo: Circulación Bibliográfica

| Término | Significado en el dominio |
|---------|--------------------------|
| **Préstamo** | Contrato de entrega temporal de un material a un usuario bajo ciertas políticas de tiempo y usos. Puede ser Normal o Interbibliotecario. |
| **Renovación** | Acción para extender el plazo de un préstamo activo. Modifica la fecha esperada de devolución e incrementa el contador de usos. |
| **Devolución** | Acción que finaliza el ciclo de un préstamo activo, cambiando el estado del material y del usuario. |
| **Plazo** | Cantidad de días asignados para el préstamo, calculados según la política del tipo de material y tipo de usuario. |

### Lenguaje Ubicuo: Cálculo de Multas

| Término | Significado en el dominio |
|---------|--------------------------|
| **Multa** | Sanción (definida por la biblioteca) aplicada a un usuario por incumplir las reglas de la biblioteca. Puede ser por retraso, daño o pérdida. |
| **Retraso** | Cantidad de días transcurridos desde la fecha de devolución esperada hasta la devolución real. Genera un tipo de multa. |
| **Evaluación de Daño** | Inspección física del material al ser devuelto (ej. páginas rasgadas, cubierta dañada) para determinar si es usable y el nivel de gravedad, generando un tipo de multa. |
| **Recargo por Pérdida** | Valor calculado a partir del precio base del material más un porcentaje de penalidad que varía según el tipo de usuario (estudiante, profesor, etc.). |
| **Umbral de Bloqueo** | Monto máximo de multas acumuladas permitida. Superarlo desencadena una restricción total para realizar nuevas transacciones hasta suplir las multas. |
| **Estado de Cuenta** | Situación del usuario frente a la biblioteca respecto a sus multas (ej. Pendiente, Pagada, Condonada). |

### Lenguaje Ubicuo: Gestión de Materiales

| Término | Significado en el dominio |
|---------|--------------------------|
| **Material** | Objeto físico o digital (Libro, Revista, DVD, EBook) gestionado por la biblioteca, identificado por un código único y metadatos (ISBN, título, autor). |
| **Estado Físico** | Condición actual de un material tangible. Determina si es apto para préstamo (Ej. Óptimo, Deteriorado, En Reparación). |
| **Baja de Material** | Proceso administrativo mediante el cual un material es retirado permanentemente del catálogo por pérdida, daño irreparable o desactualización. |
| **Licencia Digital** | Permiso de uso concurrente o temporal asociado a un EBook. Limita cuántos usuarios pueden acceder simultáneamente. |
| **Disponibilidad** | Indicador en tiempo real que refleja si un material está físicamente en la estantería o con licencias libres para ser asignado en Circulación Bibliográfica. |

### Lenguaje Ubicuo: Gestión de Usuarios

| Término | Significado en el dominio |
|---------|--------------------------|
| **Usuario** | Individuo (Estudiante, Profesor, Investigador, Público General) registrado y autorizado para utilizar los servicios de la biblioteca. |
| **Privilegios** | Conjunto de reglas base asignadas por el tipo de usuario (ej. cantidad máxima de libros, días permitidos y renovaciones habilitadas). |
| **Estado de Cuenta** | Condición operativa del usuario. Puede ser Activo (habilitado), Suspendido (inhabilitado temporalmente por política) o Bloqueado (inhabilitado por deuda en Gestión de multas). |

### Lenguaje Ubicuo: Préstamos Interbibliotecarios

| Término | Significado en el dominio |
|---------|--------------------------|
| **Solicitud Externa** | Petición formal enviada a una institución aliada para conseguir un material que no existe en el catálogo local de Elysium. |
| **Biblioteca Origen / Destino** | Instituciones involucradas en la transacción. "Origen" es quien posee el material; "Destino" es donde el usuario lo recibe. |
| **Costo de Transferencia** | Valor económico asociado a la logística y envío del material entre instituciones. Debe ser aceptado antes de la solicitud. |
| **Rastreo** | Trazabilidad del estado logístico del material externo (Solicitado, En Tránsito, Recibido, Devuelto al Origen). |

### Lenguaje Ubicuo: Gestión de Reservas

| Término | Significado en el dominio |
|---------|--------------------------|
| **Reserva** | Separación en cola de un material físico que actualmente está prestado. Expira en 24 horas tras la notificación de disponibilidad. |
| **Cola de Prioridad** | Posición secuencial asignada a las reservas pendientes de un mismo material. El primer lugar tiene prioridad de notificación. |
| **Notificación de Disponibilidad** | Aviso enviado al primer usuario en cola cuando el material devuelto queda libre. Inicia el período de 24 horas para que lo retire. |
| **Período de Notificación** | Ventana de 24 horas contadas desde la notificación de disponibilidad. Si el usuario no retira el material, la reserva expira. |
| **Expiración de Reserva** | Vencimiento automático de la reserva transcurridas las 24 horas. Libera el turno y reasigna la cola. |
| **Cancelación de Reserva** | Acción voluntaria del usuario para abandonar su turno en la cola antes de ser notificado o antes de vencer el período. |

### Lenguaje Ubicuo: Notificaciones

| Término | Significado en el dominio |
|---------|--------------------------|
| **Notificación** | Paquete de información (mensaje) para ser entregado a un destinatario específico. |
| **Destinatario** | Entidad reducida a sus datos de contacto (Nombre, Email, Teléfono). Ignora su rol académico o historial. |
| **Canal de Envío** | Medio tecnológico a través del cual se despacha el mensaje (Email, SMS). |
| **Plantilla** | Formato predefinido del mensaje que contiene variables que son reemplazadas por los datos del evento disparador. |

### Lenguaje Ubicuo: Reportes y Estadísticas

| Término | Significado en el dominio |
|---------|--------------------------|
| **Modelo de Lectura** | Representación aplanada y desnormalizada de los datos de los demás contextos, optimizada exclusivamente para ser consultada rápidamente. |
| **Métrica** | Valor numérico calculado (suma, promedio, conteo) a partir del historial de eventos (ej. "Libros más prestados", "Tiempo medio de devolución"). |
| **Tablero** | Interfaz visual que agrupa un conjunto de métricas filtradas bajo un contexto específico para la toma de decisiones. |
| **Filtro Estadístico** | Parámetros de tiempo, tipo de material o tipo de usuario aplicados sobre un modelo de lectura para acotar el análisis. |

---

## 4.3. Agregados y Objetos de Valor

### BC1 — Circulación Bibliográfica

| Tipo de Elemento | Nombre | Descripción dentro del Contexto |
|-----------------|--------|--------------------------------|
| `<<Aggregate>>` | **Prestamo** | Raíz del agregado de circulación. Controla renovaciones, devoluciones y protege el periodo de tiempo. Se persiste como un bloque atómico. |
| `<<Entity>>` | PrestamoNormal | Especialización de Prestamo para operaciones locales. Agrega ubicación física. |
| `<<Entity>>` | PrestamoInterbibliotecario | Especialización con datos de coordinación externa (origen, costo de transferencia). |
| `<<Value Object>>` | PeriodoPrestamo | Encapsula fechas de inicio, vencimiento y devolución. Inmutable. |
| `<<Value Object>>` | RenovacionesUsadas | Encapsula usos actuales y el límite. Expone `puedeRenovar()`. |
| `<<Value Object>>` | PrestamoId | Identidad tipada del préstamo (ej. PRE-0001). |
| `<<Domain Event>>` | PrestamoRegistrado | Ocurre al confirmar préstamo. |
| `<<Domain Event>>` | MaterialDevuelto | Ocurre al registrar devolución (indica si fue tardío o con daño). |
| `<<Domain Event>>` | PrestamoRenovado | Ocurre al extender el plazo exitosamente. |
| `<<Domain Event>>` | RenovacionRechazada | (Nuevo) Ocurre cuando se intenta renovar pero se excede el límite o hay reservas pendientes. |
| `<<Domain Service>>` | PoliticaRenovacion | Valida privilegios vs estado del préstamo. |
| `<<Enumeration>>` | EstadoPrestamo | ACTIVO, DEVUELTO, VENCIDO, RENOVADO. |

#### Diagrama UML — Agregado Prestamo (BC1) y Agregado Reserva (BC8)

```
«Aggregate»                                    «Aggregate»
Prestamo                                       Reserva
─────────────────────────────                  ─────────────────────────────
-id : PrestamoId                               -id : ReservaId
-periodo : PeriodoPrestamo                     -posicionCola : PosicionCola
-renovaciones : RenovacionesUsadas             -periodoNotif : PeriodoNotificacion
-estado : EstadoPrestamo                       -estado : EstadoReserva
-idUsuario : UsuarioId                         -idUsuario : UsuarioId
-idMaterial : MaterialId                       -idMaterial : MaterialId
+renovar(fecha) : void                         +notificarDisponibilidad() : void
+devolver(fecha) : void                        +expirar() : void
        ↗  (herencia)                                  ↗  (herencia)
«Entity»              «Entity»          «Entity»                 «Entity»
PrestamoNormal        PrestamoInterbibl ReservaNormal            ReservaInterbibl
─────────────         ─────────────     ─────────────            ─────────────────
-ubicacion: string    -bibliotecaOrigen -ubicacion               -bibliotecaDestino
+getUbicacion()       -costoTransfer.   +getUbicacion()          +getDestino()

        │  usa                                          │  usa
        ↓                                              ↓
«Value Object»   «Value Object»    «Value Object»   «Value Object»
PeriodoPrestamo  RenovacionesUsadas PosicionCola    PeriodoNotificacion
──────────────   ──────────────     ──────────────   ───────────────────
-fechaInicio     -usadas : int      -posicion : int  -fechaNotificacion
-fechaVencim.    -limite : int      +esElPrimero()   -fechaExpiracion
+esTardio(): bool +puedeRenovar()   +anterior()      +haExpirado()

«Enumeration»                      «Enumeration»
EstadoPrestamo                     EstadoReserva
──────────────                     ──────────────
ACTIVO                             EN_ESPERA
DEVUELTO                           NOTIFICADA
VENCIDO                            EXPIRADA
RENOVADO                           CANCELADA
```

### BC8 — Gestión de Reservas

| Tipo de Elemento | Nombre | Descripción dentro del Contexto |
|-----------------|--------|--------------------------------|
| `<<Aggregate>>` | **Reserva** | Raíz del agregado de cola de espera. Controla su propio ciclo de vida (notificar, expirar) independientemente de los préstamos. Se persiste como un bloque atómico. |
| `<<Entity>>` | ReservaNormal | Especialización para ejemplares locales. |
| `<<Entity>>` | ReservaInterbibliotecaria | Especialización para ejemplares de otra biblioteca. |
| `<<Value Object>>` | PosicionCola | Posición secuencial en la cola de un material. Expone `esElPrimero()`. Inmutable. |
| `<<Value Object>>` | PeriodoNotificacion | Encapsula `fechaNotificacion` y `fechaExpiracion` (= fechaNotificacion + 24h). Expone `haExpirado(fechaActual)`. Inmutable. |
| `<<Value Object>>` | ReservaId | Identidad tipada (ej. RES-0001). Inmutable. |
| `<<Domain Event>>` | ReservaCreada | Ocurre al confirmar la creación. Consumido por BC6 y BC7. |
| `<<Domain Event>>` | ReservaNotificada | Ocurre cuando se notifica al primer usuario en cola. Consumido por BC6. |
| `<<Domain Event>>` | ReservaExpirada | Ocurre cuando el usuario no recoge el material en 24h. Consumido por BC1 y BC6. |
| `<<Domain Event>>` | ReservaCancelada | Ocurre cuando el usuario cancela su turno. Consumido por BC6. |
| `<<Domain Service>>` | GestorColaReservas | Reasigna las posiciones de la cola cuando una reserva expira o se cancela. Opera sobre múltiples instancias del agregado Reserva. |
| `<<Enumeration>>` | EstadoReserva | EN_ESPERA, NOTIFICADA, EXPIRADA, CANCELADA. |

### BC4 — Cálculo de Multas

| Tipo de Elemento | Nombre | Descripción dentro del Contexto |
|-----------------|--------|--------------------------------|
| `<<Aggregate>>` | **Multa** | Raíz del agregado. Controla su pago, condonación y delega el cálculo exacto a sus entidades internas. |
| `<<Entity>>` | MultaPorRetraso | Calcula monto base a días de retraso y tarifa. |
| `<<Entity>>` | MultaPorDano | Delega cálculo a su VO EvaluacionDano. |
| `<<Entity>>` | MultaPorPerdida | Calcula base al valor del material + recargo por perfil. |
| `<<Value Object>>` | Dinero | Encapsula BigDecimal y moneda. Previene errores de redondeo. |
| `<<Value Object>>` | EvaluacionDano | Agrupa lista de daños y pondera gravedad. |
| `<<Value Object>>` | Dano | Daño individual (ej. páginas rasgadas) sin identidad propia. |
| `<<Value Object>>` | PorcentajeRecargo | Encapsula el porcentaje de penalidad en MultaPorPerdida. Válido entre 0 y 100. Varía por tipo de usuario: **Estudiante 20%, Profesor 10%, Investigador 0%, Público General 30%**. |
| `<<Domain Event>>` | PeticionDesbloqueoUsuario | Ocurre cuando la deuda total llega a cero tras un pago. Consumido por BC3 para reactivar la cuenta. |
| `<<Domain Event>>` | MultaGenerada | Ocurre al procesar una infracción. |
| `<<Domain Event>>` | MultaPagada | Ocurre al saldar la deuda. |
| `<<Domain Event>>` | UsuarioBloqueadoPorDeuda | Ocurre cuando la suma de multas supera el límite. |
| `<<Domain Service>>` | GestorUmbralBloqueo | Suma múltiples multas de un usuario para determinar bloqueo. |
| `<<Enumeration>>` | EstadoMulta | PENDIENTE, PAGADA, CONDONADA. |

#### Diagrama UML — Agregado Multa (BC4)

```
                        «Aggregate»
                        Multa
                        ────────────────────────────
                        -id : MultaId
                        -prestamoId : PrestamoId
                        -usuarioId : UsuarioId
                        -monto : Dinero
                        -estado : EstadoMulta
                        -tipo : TipoMulta
                        +pagar(fecha) : void
                        +calcularMonto()
                       /              \
                tiene                  tiene
               /                         \
«Entity»         «Entity»       «Entity»            «Enumeration»   «Enumeration»
MultaPorRetraso  MultaPorDano   MultaPorPerdida      EstadoMulta     TipoMulta
───────────────  ────────────   ───────────────      ───────────     ──────────
-diasRetraso:int -evaluacion:   -valorMaterial       PENDIENTE       POR_RETRASO
-tarifaDiaria:   EvaluacionDano -porcRecargo         PAGADA          POR_DANO
 Dinero          +calcularMonto()+calcularMonto()    CONDONADA       POR_PERDIDA
+calcularMonto()      │
         │           │  usa
         │  usa      ↓
         ↓   «Value Object»
«Value Object»   EvaluacionDano
Dinero           ────────────────
────────────     -danos: List<Dano>
-monto:BigDecimal-nivelGravedad
-moneda: String  +calcularCosto()
+sumar()
+multiplicar()
```

---

## 4.4. Eventos y Comandos

| Comando (Intención) | Agregado Raíz | Evento Publicado (Resultado) | Invariante Protegida | Consumidor Principal |
|--------------------|--------------|------------------------------|---------------------|---------------------|
| `RegistrarPrestamo` | Prestamo (BC1) | `PrestamoRegistrado` | Usuario activo, sin multas. | Reportes (BC7) |
| `RenovarPrestamo` | Prestamo (BC1) | `PrestamoRenovado` | Renovaciones < Límite. | Notificaciones (BC6) |
| `RenovarPrestamo` | Prestamo (BC1) | `RenovacionRechazada` | Respeta límite y cola. | Notificaciones (BC6) |
| `DevolverMaterial` | Prestamo (BC1) | `MaterialDevuelto` | fechaReal >= fechaPrestamo. | Sanciones (BC4) |
| `CrearReserva` | Reserva (BC8) | `ReservaCreada` | Material debe estar prestado. | Notificaciones (BC6) |
| `ExpirarReserva` | Reserva (BC8) | `ReservaExpirada` | 24h transcurridas tras aviso. | BC1 (liberar disponibilidad), BC6 |
| `GenerarMulta` | Multa (BC4) | `MultaGenerada` | Monto > 0, Préstamo devuelto. | BC3: Gestión de Usuarios |
| `PagarMulta` | Multa (BC4) | `MultaPagada` | Estado era PENDIENTE. | BC3: Gestión de Usuarios |
| `SolicitarPrestamoExterno` | SolicitudExterna (BC5) | `MaterialInaccesible` | Respuesta de red válida. | Notificaciones (BC6) |
| `ConsolidarDatos` | N/A (Read Model) | `TablerosActualizados` | Datos sincronizados. | UI / Gerencia |
| `NotificarDisponibilidad` | Reserva (BC8) | `ReservaNotificada` | Solo la primera en cola puede notificarse. | BC6 |
| `CancelarReserva` | Reserva (BC8) | `ReservaCancelada` | Estado debe ser EN_ESPERA o NOTIFICADA. | BC8 (reasignar cola), BC6 |

---

## 4.5. Diagramas de Secuencia con Comandos y Eventos

Los siguientes diagramas ilustran cómo los comandos del usuario se traducen en llamadas síncronas entre BCs (líneas continuas) y eventos asíncronos publicados en el bus de eventos (líneas punteadas).

> **Principio clave:** La separación entre la respuesta inmediata al actor y el procesamiento posterior mediante eventos garantiza la independencia entre Bounded Contexts y la capacidad de evolución independiente de cada microservicio.

### Diagrama 1 — Registro de Préstamo

```
Actor               BC1:Transacc.    BC3:Gestión    BC2:Gestión    Bus de        BC6:
(UI/Bibliotec.)     Bibliográficas   de Usuarios    de Material    Eventos       Notificaciones
    │                    │               │               │              │              │
    │RegistrarPrestamo   │               │               │              │              │
    │(idUsuario,idMat,   │               │               │              │              │
    │ tipo)              │               │               │              │              │
    │───────────────────>│               │               │              │              │
    │                    │verificarElegib│               │              │              │
    │                    │(idUsuario)    │               │              │              │
    │                    │──────────────>│               │              │              │
    │                    │<─ ─ ─ ─ ─ ─ ─│               │              │              │
    │                    │ usuarioActivo=true            │              │              │
    │                    │               │VerificarDisp  │              │              │
    │                    │               │ (idMaterial)  │              │              │
    │                    │───────────────────────────────>              │              │
    │                    │<─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ │              │              │
    │                    │ disponible=true               │              │              │
    │                    │Prestamo.registrar()           │              │              │
    │                    │◄──────────────────────────────┘              │              │
    │<───────────────────│               │               │              │              │
    │return Resultado    │               │               │              │              │
    │.Exitoso(prestamoId)│               │               │              │              │
    │                    │               │               │              │              │
    │                    │────────────────────────────────────────────>│              │
    │                    │   [Desacoplamiento: BC1 avisa que terminó]   │              │
    │                    │          Publica: PrestamoRegistradoEvent    │              │
    │                    │               │               │              │──────────────>
    │                    │               │               │              │ Consume:     │
    │                    │               │               │              │ PrestamoReg. │
    │                    │               │               │              │ Enviar conf. │
    │                    │               │               │              │ (email, sms) │
```

### Diagrama 2 — Devolución con Daño y Generación de Multa

```
Actor          BC1:Circulación    Bus de Eventos    BC4:Sanciones    BC3:Comunidad    BC6:Notif.
(UI/Bibliote.) │                  │                 │                │                │
    │          │                  │                 │                │                │
    │DevolverMaterial              │                 │                │                │
    │(prestamoId, evaluacion)      │                 │                │                │
    │─────────>│                  │                 │                │                │
    │          │Prestamo.devolver()│                 │                │                │
    │          │◄─────────────────│                 │                │                │
    │<─────────│                  │                 │                │                │
    │return Resultado.Exitoso      │                 │                │                │
    │(resumen) │                  │                 │                │                │
    │          │ Publica:         │                 │                │                │
    │          │ MaterialDevueltoEvent              │                │                │
    │          │─────────────────>│                 │                │                │
    │          │                  │ Consume:         │                │                │
    │          │                  │ MaterialDevuelto │                │                │
    │          │                  │─────────────────>                │                │
    │          │                  │                 │Multa.calcular() │                │
    │          │                  │                 │◄────────────────                │
    │          │                  │ Publica:        │                 │               │
    │          │                  │ MultaGeneradaEvent               │                │
    │          │                  │                 │GestorUmbralBloq│                │
    │          │                  │                 │.evaluar()      │                │
    │          │                  │                 │◄───────────────│                │
    │          │          [alt: si deuda total >= umbral configurado] │                │
    │          │                  │ Publica:        │                │                │
    │          │                  │ UsuarioBloqueadoPorDeudaEvent    │                │
    │          │                  │                 │                │                │
    │          │    [par] ─────────────────────────────────────────────────────────── │
    │          │                  │ Consume:        │                │                │
    │          │                  │ UsuarioBloqueadoPorDeuda ───────>│                │
    │          │                  │                 │ Actualizar cuenta a BLOQUEADO   │
    │          │                  │ Consume:        │                │                │
    │          │                  │ MultaGeneradaEvent ─────────────────────────────>│
    │          │                  │ UsuarioBloqueadoPorDeudaEvent ──────────────────>│
    │          │                  │                 │                │  preparar y    │
    │          │                  │                 │                │  enviarAvisos()│
```

### Diagrama 3 — Préstamo Interbibliotecario

```
Estudiante/UI   BC1:Transacc.Bibl.   Bus de Eventos   BC5:Prestamo    API Externa    BC6:Notif.
    │            │                    │                Interbibliot.   (Otras Univ.)  │
    │SolicitarPrestamoExterno          │                │               │              │
    │(isbn, idUsuario)                 │                │               │              │
    │────────────>│                   │                │               │              │
    │             │validarUsuario()   │                │               │              │
    │             │◄──────────────────│                │               │              │
    │<────────────│                   │                │               │              │
    │ return "Solicitud en proceso"    │                │               │              │
    │             │──────────────────────────────────>│               │              │
    │             │         SolicitarMaterialExterno(isbn)            │               │
    │             │                   │                │               │              │
    │             │                   │     [BC5 asume la complejidad de hablar       │
    │             │                   │      con el mundo exterior (reintentos, etc.)]│
    │             │                   │                │─────────────>│              │
    │             │                   │                │ GET /api/v1/materials?isbn=  │
    │             │                   │                │<─────────────│              │
    │             │                   │                │ 200 OK (Material Encontrado) │
    │             │                   │                │calcularCosto Transferencia() │
    │             │                   │ Publica:       │              │              │
    │             │                   │ MaterialExternoRecibidoEvent  │              │
    │             │ Consume:          │                │              │              │
    │             │ MaterialExternoRecibidoEvent        │              │              │
    │             │ Prestamo.registrar(tipo=INTERBIBL)  │              │              │
    │             │                   │ Consume:       │              │              │
    │             │                   │ MaterialExternoRecibidoEvent ─────────────>│
    │             │                   │                │              │ prepararMens.│
    │             │                   │                │              │ Enviar Email │
    │             │                   │                │              │ "Tu libro    │
    │             │                   │                │              │  está en     │
    │             │                   │                │              │  camino"     │
```

### Diagrama 4 — Pago de Multa y Desbloqueo

```
Estudiante/    BC4:Gest.multas   Bus de Eventos   BC3:Gest.usuarios   BC6:Notific.
Tesorería      │                  │                │                   │
    │          │                  │                │                   │
    │PagarMulta(idMulta, montoPag.)│                │                   │
    │─────────>│                  │                │                   │
    │          │Multa.pagar(mont.)│                │                   │
    │          │ (Cambia a PAGADA)│                │                   │
    │          │◄─────────────────│                │                   │
    │          │ Publica:         │                │                   │
    │          │ MultaPagadaEvent │                │                   │
    │          │─────────────────>│                │                   │
    │          │GestorUmbralBloq.evaluar()         │                   │
    │          │◄─────────────────│                │                   │
    │          │ ¿La deuda total ahora es cero?    │                   │
    │          │ [alt: Deuda en cero]               │                   │
    │          │ Publica:         │                │                   │
    │          │ PeticionDesbloqueoUsuarioEvent    │                   │
    │          │─────────────────>│                │                   │
    │<─────────│                  │                │                   │
    │ return Resultado.Exitoso("Pago registrado")  │                   │
    │          │                  │ Consume:       │                   │
    │          │                  │ PeticionDesbloqueoUsuarioEvent     │
    │          │                  │────────────────>                   │
    │          │                  │                │cambiarEstadoCuenta│
    │          │                  │                │(ACTIVO)           │
    │          │                  │                │ [El usuario ya    │
    │          │                  │                │  puede volver a   │
    │          │                  │                │  prestar libros.] │
    │          │                  │ Consume:       │                   │
    │          │                  │ MultaPagadaEvent ─────────────────>│
    │          │                  │                │ Enviar Email o sms│
    │          │                  │                │ "Recibo de pago   │
    │          │                  │                │  #1234"           │
```

---

## 4.6. Lógica de Dominio: Reglas, Comportamientos y Restricciones

### A. Préstamo.renovar(nuevaFechaVencimiento: LocalDate)

Extiende el plazo de un préstamo activo. La nueva fecha de vencimiento es calculada por `PoliticaPlazo` (Domain Service) según el tipo de material y de usuario. La validación es responsabilidad del propio agregado `Prestamo`.

| | Precondiciones | Invariantes | Postcondiciones |
|-|---------------|-------------|-----------------|
| | `nuevaFechaVencimiento` no es nula y es posterior a la fecha actual del sistema. | `EstadoPrestamo` es **ACTIVO**. No se pueden renovar préstamos devueltos, vencidos ni cancelados. | Se genera una nueva instancia inmutable de `PeriodoPrestamo` con la `nuevaFechaVencimiento`. La instancia anterior es descartada. |
| | El servicio de aplicación ha resuelto el agregado `Prestamo` del repositorio antes de invocar este método. El agregado no accede a repositorios. | `renovacionesUsadas < límite` según tipo de usuario (Estudiante: 2, Profesor: 3, Investigador: 4, Público: 1). Esta regla vive en `RenovacionesUsadas.puedeRenovar()`. | `RenovacionesUsadas` se reemplaza por una nueva instancia con `usadas + 1`. El VO es inmutable. |
| | El servicio de aplicación ha verificado previamente con BC4 que el usuario no tiene multas pendientes. Esta validación es de coordinación entre BCs — no es precondición del método del agregado. | No existen reservas en estado `EN_ESPERA` para el mismo material. Si las hay, la política puede denegar la renovación. | `EstadoPrestamo` transiciona a **RENOVADO**. |

**Evento publicado:** `PrestamoRenovado` — consumido por BC6 y BC7. Campos: `prestamoid`, `nuevaFechaVencimiento`, `renovacionesUsadas`.

---

### B. Préstamo.devolver(fechaReal: LocalDate, evaluacion: EvaluacionDano)

Finaliza el ciclo de vida de un préstamo activo. El agregado calcula internamente si hubo retraso y si hay daños, y publica el evento `MaterialDevuelto` para que BC4 genere la multa correspondiente.

| | Precondiciones | Invariantes | Postcondiciones |
|-|---------------|-------------|-----------------|
| | `fechaReal` no es nula y no es anterior a `PeriodoPrestamo.fechaInicio`. | `EstadoPrestamo` es **ACTIVO o RENOVADO**. No se puede devolver un préstamo ya devuelto o vencido administrativamente. | `PeriodoPrestamo` se reemplaza con nueva instancia que incluye `fechaDevolucion = fechaReal`. |
| | `evaluación` no es nula. Si no hay daños visibles, se acepta una `EvaluaciónDano` con lista vacía. | `idUsuario` e `idMaterial` son invariantes del agregado: nunca pueden ser nulos ni modificados durante todo su ciclo de vida. Son la identidad trazable del contrato bibliográfico. | `EstadoPrestamo` transiciona a **DEVUELTO**. |
| | | | `fueTardio` y `conDanos` quedan calculados desde `PeriodoPrestamo.esTardio()` y la evaluación. |

**Evento publicado:** `MaterialDevuelto` — consumido por BC8, BC4, BC2, BC6 y BC7. Campos: `prestamoid`, `materialid`, `usuarioid`, `fueTardio`, `conDanos`, `evaluación`.

---

### C. Multa.pagar(fechaPago: LocalDate)

Registra el saneamiento de una sanción pendiente y transiciona su estado. Operación atómica que garantiza que no existan pagos duplicados.

| | Precondiciones | Invariantes | Postcondiciones |
|-|---------------|-------------|-----------------|
| | `fechaPago` no es nula y no es anterior a `fechaGeneracion` de la multa. | `EstadoMulta` es obligatoriamente **PENDIENTE**. Una multa pagada o CONDONADA no puede procesar pagos. | `EstadoMulta` transiciona a **PAGADA**. |
| | La multa existe en el repositorio de BC4 con el `multaId` proporcionado. | El monto interno (tipo `Dinero`) debe ser mayor a cero. | El estado no puede retroceder: ninguna operación puede volver a PENDIENTE desde PAGADA. |

**Evento publicado:** `MultaPagada` — consumido por BC3 y BC6. Campos: `multaId`, `usuarioid`, `monto`, `fechaPago`.

---

### D. Reserva.notificarDisponibilidad(fechaNotificacion: LocalDateTime)

Transiciona una reserva en espera a estado notificado e inicia el período de 24 horas para que el usuario recoja el material.

| | Precondiciones | Invariantes | Postcondiciones |
|-|---------------|-------------|-----------------|
| | `fechaNotificación` no es nula y corresponde al momento actual del sistema. | `EstadoReserva` es **EN_ESPERA**. No se puede notificar una reserva ya notificada, expirada o cancelada. | `EstadoReserva` transiciona a **NOTIFICADA**. |
| | `GestorColaReservas` ha confirmado que esta reserva tiene posición 1 en la cola para el material. | | Se instancia el VO `PeriodoNotificacion` con `fechaExpiration = fechaNotificacion + 24h`. Inmutable desde su creación. |
| | Ninguna otra reserva del mismo material puede notificarse hasta que esta expira o se convierte en préstamo. | | |

**Evento publicado:** `ReservaNotificada` — consumido por BC6. Campos: `reservaId`, `materialId`, `usuarioId`.

---

### E. Reserva.expirar()

Vence la reserva cuando el usuario no recoge el material dentro de las 24 horas. Libera el turno para el siguiente en la cola.

| | Precondiciones | Invariantes | Postcondiciones |
|-|---------------|-------------|-----------------|
| | El tiempo actual es posterior a `PeriodoNotificacion.fechaExpiration`. | `EstadoReserva` es **NOTIFICADA**. Solo las reservas notificadas pueden expirar. | `EstadoReserva` transiciona a **EXPIRADA**. Estado terminal — no puede reactivarse. |
| | El agregado valida internamente que las 24h transcurrieron. | `PeriodoNotificacion` es el VO que define el período de notificación. | `GestorColaReservas` reasigna posiciones: todos los que estaban después avanzan una posición. |

**Evento publicado:** `ReservaExpirada` — consumido por BC1 y BC6. Campos: `reservaId`, `materialId`, `usuarioId`.

---

### F. Reserva.cancelar()

Permite al usuario abandonar voluntariamente su turno en la cola antes de ser notificado o antes de que venza el período de recogida.

| | Precondiciones | Invariantes | Postcondiciones |
|-|---------------|-------------|-----------------|
| | El servicio de aplicación ha resuelto el agregado `Reserva` del repositorio antes de invocar este método. | `EstadoReserva` es **EN_ESPERA o NOTIFICADA**. Una reserva EXPIRADA o CANCELADA no puede cancelarse nuevamente. El estado es terminal en ambos casos. | `EstadoReserva` transiciona a **CANCELADA**. Estado terminal: no puede reactivarse. |
| | | La posición en cola (`PosicionCola`) es el único dato que cambia cuando se cancela. Una cancelación no modifica las reservas de otros usuarios directamente — eso es responsabilidad de `GestorColaReservas`. | `GestorColaReservas` reasigna posiciones para todos los que estaban después en la cola. La primera reserva en cola queda en posición 1 y puede ser notificada si el material está disponible. |

**Evento publicado:** `ReservaCancelada` — consumido por BC8 (`GestorColaReservas` reasigna la cola) y BC6 (notifica al usuario). Campos: `reservaId`, `materialId`, `usuarioId`.

---

## 4.7. Servicios del Dominio

| Servicio de Dominio | Bounded Context | Responsabilidad | Por qué no es un servicio de aplicación |
|--------------------|-----------------|-----------------|-----------------------------------------|
| **PoliticaRenovacion** | BC1 | Calcula la nueva fecha de vencimiento para una renovación según el tipo de material y el tipo de usuario. También determina si existen reservas pendientes para el mismo material que impidan la renovación. | La regla "cuántos días corresponden según tipo de material y usuario" es conocimiento del negocio, no coordinación técnica. La validación de si el préstamo puede renovarse (límite de renovaciones, estado ACTIVO) es responsabilidad del agregado `Prestamo`; este servicio sólo calcula el insumo que necesita el agregado para tomar esa decisión. |
| **GestorColaReservas** | BC8 | Reasigna las posiciones de la cola cuando una reserva expira o se cancela. Involucra múltiples instancias del agregado `Reserva`. | Opera sobre múltiples agregados del mismo tipo — ningún agregado individual puede hacerlo sin violar el principio de que un agregado no accede a otro directamente. |
| **PoliticaPlazo** | BC1 | Calcula los días de préstamo según el tipo de usuario y el tipo de material. | Es conocimiento puro del negocio bibliotecario (ej. Investigador + Libro = 30 días) sin dependencias de infraestructura. |
| **GestorUmbralBloqueo** | BC4 | Suma el monto total de multas pendientes de un usuario y determina si supera el umbral de bloqueo configurado. Devuelve un resultado booleano y el monto acumulado; no ejecuta el bloqueo — esa decisión la toma BC9. | Involucra múltiples instancias del agregado `Multa` de un mismo usuario — requiere visión global que ninguna `Multa` individual tiene. |
| **PoliticaBloqueo** | BC9 | Recibe el resultado de `GestorUmbralBloqueo` (BC4) vía evento `MultaGenerada` y determina qué acción tomar sobre la cuenta del usuario: bloquear, desbloquear al llegar a cero, o habilitar la condición de condonación parcial. | El cálculo del monto (suma de Multas) corresponde a `GestorUmbralBloqueo` en BC4. `PoliticaBloqueo` encapsula las reglas de qué hacer con ese monto desde la perspectiva del ciclo de vida del pago: no es infraestructura, es conocimiento del negocio de cobro. |
| **PoliticaPlazoInterbibliotecario** | BC5 | Calcula los días de préstamo para materiales externos aplicando los acuerdos específicos de cada institución aliada (que pueden sobreescribir la política estándar de BC1) y el tipo de usuario. A diferencia de `PoliticaPlazo`, maneja variantes por convenio bilateral: una misma combinación tipo-usuario puede tener plazos distintos según la biblioteca de origen. | Las condiciones de préstamo entre bibliotecas son conocimiento puro del dominio (ej. acuerdos específicos por institución) y no deben vivir en un servicio de aplicación que orquesta la logística. |
| **EvaluadorInfraccion** | BC4 | A partir del evento `MaterialDevuelto` (con `fueTardio`, `conDanos` y evaluación) determina qué tipo de multa debe generarse (retraso, daño, pérdida) y con qué parámetros. Puede generar más de una multa en la misma devolución (ej. retraso + daño). | La decisión de qué tipo(s) de multa corresponden no pertenece a ninguna entidad `Multa` individual (que solo conoce su propio cálculo) ni al servicio de aplicación (que solo coordina). Es lógica de negocio que combina múltiples hechos del dominio: estado del material, fechas y perfil del usuario. |

---

## 4.8. Transición del Dominio Anémico al Dominio Enriquecido

*(Sección mencionada en el índice — contenido a desarrollar en versiones posteriores del documento.)*

---

## Resumen Ejecutivo del Modelo DDD

### Los 9 Bounded Contexts

| BC | Nombre | Tipo | Agregado Raíz | Patrón de Integración |
|----|--------|------|--------------|----------------------|
| BC1 | Circulación Bibliográfica | **Core** | Prestamo | Cliente síncrono de BC2/BC3; emisor asíncrono |
| BC2 | Gestión de Materiales | Supporting | Material | Proveedor / Open Host Service |
| BC3 | Gestión de Usuarios | Supporting | Usuario | Proveedor / Gateway Context |
| BC4 | Cálculo de Multas | **Core** | Multa | Consumidor de eventos; emisor asíncrono |
| BC5 | Préstamos Interbibliotecarios | Supporting | SolicitudExterna | Gateway Context |
| BC6 | Notificaciones | Generic | Notificacion | Suscriptor de todos los BCs |
| BC7 | Reportes y Estadísticas | Generic | — (Read Model) | Observador pasivo / Analysis Context |
| BC8 | Gestión de Reservas | **Core** | Reserva | Consumidor y emisor de eventos |
| BC9 | Cobro de Multas | Supporting | RegistroPago | Puente entre BC4 y BC3 |

### Eventos de Dominio Clave

```
BC1 ──[MaterialDevuelto]──────────────────► BC4 (calcular multa)
                                         ► BC8 (iniciar cola reservas)
                                         ► BC2 (actualizar disponibilidad)
                                         ► BC6 (notificar)
                                         ► BC7 (reportes)

BC4 ──[MultaGenerada]────────────────────► BC9 (cobro)
                                         ► BC6 (notificar)
                                         ► BC7 (reportes)

BC4 ──[UsuarioBloqueadoPorDeuda]─────────► BC3 (bloquear cuenta)
                                         ► BC6 (notificar)

BC8 ──[ReservaExpirada]──────────────────► BC1 (liberar disponibilidad)
                                         ► BC6 (notificar)

BC9 ──[PeticionDesbloqueoUsuario]────────► BC3 (reactivar cuenta)
```

### Servicios de Dominio vs Servicios de Aplicación

| Tipo | Criterio de distinción | Ejemplos |
|------|----------------------|---------|
| **Servicio de Dominio** | Contiene reglas de negocio puras. Opera sobre múltiples agregados del mismo BC. No tiene dependencias de infraestructura. | PoliticaRenovacion, GestorColaReservas, GestorUmbralBloqueo, EvaluadorInfraccion |
| **Servicio de Aplicación** | Orquesta la coordinación entre BCs. Resuelve agregados del repositorio. Publica eventos al bus. No contiene lógica de negocio. | RegistrarPrestamoUseCase, PagarMultaUseCase, CrearReservaUseCase |
