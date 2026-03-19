# Justificación del Modelo de Transacciones en Elysium

## ¿Qué es una Transacción en este sistema?

La clase abstracta `Transaccion` modela una **entidad persistente con identidad propia y ciclo de vida** que vincula a un usuario con un material. Para que algo sea una transacción en Elysium, debe cumplir cuatro criterios:

1. **Identidad propia**: tiene un ID único que lo identifica en el sistema (`IdTransaccion`).
2. **Persistencia independiente**: se almacena como registro en un repositorio (`IRepositorio<T>`) y puede recuperarse por su ID.
3. **Ciclo de vida con estados**: nace en un estado, transita por otros y termina. Sus estados se representan mediante `EstadoTransaccion` (`ACTIVA`, `COMPLETADA`, `CANCELADA`).
4. **Vínculo usuario-material**: registra *quién* (`IdUsuario`) interactúa con *qué* (`IdMaterial`) y *cuándo* (`fechaCreacion`).

---

## ¿Por qué `Reserva` es una Transacción?

Una reserva cumple los cuatro criterios:

- **Identidad propia**: cada reserva tiene un `IdReserva` (que extiende `IdTransaccion`). Se puede buscar por ID: `repoReserva.obtenerPorId("RES-001")`.
- **Persistencia independiente**: se almacena en `IRepositorio<Reserva>`. El sistema mantiene una cola de reservas por material y necesita consultar, listar y modificar reservas como registros independientes.
- **Ciclo de vida con estados**: una reserva **nace** cuando el usuario la solicita (estado `ACTIVA`), **vive** mientras espera en cola (con `posicionCola`, `fechaExpiracion`) y puede ser notificada cuando el material se libera, y **muere** cuando se cancela, expira o se convierte en préstamo (estado `COMPLETADA` o `CANCELADA`).
- **Vínculo usuario-material**: registra que el usuario X solicitó el material Y en la fecha Z.

Además, la reserva tiene atributos propios que justifican su existencia como entidad: `posicionCola`, `fechaNotificacion`, `fechaExpiracion`, y subtipos diferenciados (`ReservaNormal` con ubicación, `ReservaInterbibliotecaria` con biblioteca destino). Todo esto requiere persistencia y consulta posterior, lo cual sería imposible si la reserva fuera simplemente una acción sin registro.

---

## ¿Por qué la devolución NO es una Transacción?

Una devolución **no crea nada nuevo**; modifica el estado de un préstamo existente. Cuando `DevolucionService.registrarDevolucion()` se ejecuta:

1. Busca el `Prestamo` existente por su ID.
2. Establece `fechaDevolucionReal` con la fecha actual.
3. Cambia el estado del préstamo de `ACTIVA` a `COMPLETADA`.
4. Devuelve el material al estado `DISPONIBLE`.
5. Si hay daños, retraso o pérdida, genera una `Multa` (que sí es una entidad con identidad propia).

No existe un repositorio de devoluciones, ni un `IdDevolucion`, ni un objeto `Devolucion` persistente. La devolución es una **operación de negocio** que cierra el ciclo de vida de un préstamo. Toda la información de la devolución queda registrada *en el propio préstamo* (`fechaDevolucionReal`, estado `COMPLETADA`), no en un registro separado.

Crear una entidad `Devolucion` sería redundante: duplicaría información que ya está en el préstamo (`quién` devolvió `qué` y `cuándo`) sin aportar un ciclo de vida propio. Una devolución no tiene estados intermedios — ocurre o no ocurre.

---

## ¿Por qué la renovación NO es una Transacción?

La renovación es una **modificación in-place** de un préstamo existente. Cuando `RenovacionService.renovarPrestamo()` se ejecuta:

1. Busca el `Prestamo` existente por su ID.
2. Recalcula `fechaDevolucionEsperada` usando `IPoliticaTiempoService`.
3. Incrementa el contador `renovacionesUsadas`.
4. Actualiza el préstamo en el repositorio.

El préstamo sigue siendo el mismo registro con el mismo ID. No nace ninguna entidad nueva. La renovación extiende la vigencia del préstamo, no crea una transacción separada.

Una renovación tampoco tiene ciclo de vida propio: no tiene estados (`PENDIENTE`, `APROBADA`, `RECHAZADA`). Se valida y se ejecuta de forma atómica — si pasa las validaciones (`maximoRenovaciones`, sin reservas pendientes, no vencido más de 7 días), se aplica inmediatamente. Si no pasa, se rechaza y no queda registro de nada.

---

## Resumen: la prueba de diseño

| Concepto | ¿Tiene ID propio? | ¿Se persiste? | ¿Tiene ciclo de vida? | ¿Es Transacción? |
|----------|-------------------|---------------|----------------------|-------------------|
| Préstamo | `IdPrestamo` | `IRepositorio<Prestamo>` | ACTIVA → COMPLETADA | ✅ Sí |
| Reserva | `IdReserva` | `IRepositorio<Reserva>` | ACTIVA → COMPLETADA/CANCELADA | ✅ Sí |
| Devolución | No | No | No (es una acción instantánea) | ❌ No |
| Renovación | No | No | No (es una modificación atómica) | ❌ No |

La regla es directa: **si es una _cosa_ con identidad que existe en el sistema, es una entidad y potencialmente una transacción. Si es una _acción_ que modifica cosas que ya existen, es lógica de servicio.**
