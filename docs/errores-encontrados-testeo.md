- ‎buscar material debería permitir buscar tanto por id, cómo por nombre, autor y tipo
- ‎al ver disponibilidad dice disponible: true y luego estado: disponible. La mismo información es redundante, que solo sea estado 
‎-agregar material permite poner un id que si es igual a otro existente va a sobrescribir los datos del existente ( no puede ser acumulativo ya que el ISBN es un estándar que se usa y se define por otra organización)
‎ -agregar material deja poner string como valor del material 
‎-los errores que da como el 400 deberían mostrar solo el texto del error, no el codgio. ya que estos muestran: 400:"{"error":"El precio no puede ser negativo "}"
‎-Buscar usuario debería poder buscar por id, nombre, email o tipo
‎-informacion redundante en ver estado del usuario dice activo : true y el estado: activo 
‎-la registrar un usuario pide el número máximo de préstamos pero eso debería establecerlo automáticamente la app ya que es parte de la lógica del negocio y este limite depende del tipo de usuario que se escoge al inicio
-‎el registro permite sobre escribir los datos de alguien si se pone el mismo id, pero para este caso lo mejor sería que los ids fuesen acumulativos ( se suma 1 al id más grande actualmente)
‎-error extraño al ingresar un email sin formatos (verificar también que solo sea string con el formato de un email y controlar mejor el error)
‎GESTIÓN DE USUARIOS
‎=======================================================
‎1. Listar todos
‎2. Buscar por ID
‎3. Ver estado de usuario
‎4. Registrar usuario
‎0. Volver
‎=======================================================
‎Seleccione: 4
‎Tipos: ESTUDIANTE, PROFESOR, INVESTIGADOR, PUBLICO_GENERAL
‎Tipo: ESTUDIANTE
‎ID: USR-000001
‎Nombre: lolazo
‎Email: nose
‎Límite máximo préstamos: 1
‎Error: 500 : "{"timestamp":"2026-05-19T17:39:56.620+00:00","status":500,"error":"Internal Server Error","path":"/usuarios"}"
‎
‎Enter para continuar...
‎=======================================================
‎        GESTIÓN DE USUARIOS
‎=======================================================
‎1. Listar todos
‎2. Buscar por ID
‎3. Ver estado de usuario
‎4. Registrar usuario
‎0. Volver
‎=======================================================
‎Seleccione: 4
‎Tipos: ESTUDIANTE, PROFESOR, INVESTIGADOR, PUBLICO_GENERAL
‎Tipo: ESTUDIANTE
‎ID: USR-000001
‎Nombre: lolazo
‎Email: a@gmail.com
‎Límite máximo préstamos: -1
‎Error: 500 : "{"timestamp":"2026-05-19T17:40:25.083+00:00","status":500,"error":"Internal Server Error","path":"/usuarios"}"
‎- al listar los prestamos la fecha incluye hora, minutos, segundos, esto no es necesario tanto detalle ( ni mostrarlo) y si el usuario no entrega a esa hora no es un limitante, con tal de que no se pase de ese día limite no se le pone una multa. 
‎- al registrar un material y el material ya está en estado de prestado el error de nuevo es el código y no solo el texto 
-‎error al hacer un préstamo normal:
‎GESTIÓN DE PRÉSTAMOS
‎=======================================================
‎1. Listar todos
‎2. Listar por usuario
‎3. Buscar por ID
‎4. Registrar préstamo
‎5. Renovar préstamo
‎0. Volver
‎=======================================================
‎Seleccione: 4
‎ID usuario: USR-000005
‎ID material: USR-000006
‎Tipo (1=NORMAL, 2=INTERBIBLIOTECARIO):
‎1
‎Error: 400 : "{"exito":false,"mensaje":"No se pudo verificar la disponibilidad del material: [404] during [GET] to [http://materiales-service/materiales/USR-000006/disponibilidad] [MaterialesClient#consultarDisponibilidad(String)]: []","data":null}"
‎al crear un usuario con campos vacíos da el mismo error anterior 
‎error al no seleccionar un tipo de préstamo:
‎=======================================================
‎        GESTIÓN DE PRÉSTAMOS
‎=======================================================
‎1. Listar todos
‎2. Listar por usuario
‎3. Buscar por ID
‎4. Registrar préstamo
‎5. Renovar préstamo
‎0. Volver
‎=======================================================
‎Seleccione: 4
‎ID usuario: USR-000006
‎ID material: MAT-000006
‎Tipo (1=NORMAL, 2=INTERBIBLIOTECARIO):
‎
‎Error: 400 : "{"exito":false,"mensaje":"Error al crear el préstamo: La sede es obligatoria","data":null}"
‎error al crear préstamo y seleccionar normal:
‎=======================================================
‎        GESTIÓN DE PRÉSTAMOS
‎=======================================================
‎1. Listar todos
‎2. Listar por usuario
‎3. Buscar por ID
‎4. Registrar préstamo
‎5. Renovar préstamo
‎0. Volver
‎=======================================================
‎Seleccione: 4
‎ID usuario: USR-000006
‎ID material: MAT-000006
‎Tipo (1=NORMAL, 2=INTERBIBLIOTECARIO):
‎1
‎Error: 400 : "{"exito":false,"mensaje":"Error al crear el préstamo: La sede es obligatoria","data":null}"
‎
‎Enter para continuar...
‎
‎error al crear préstamo y seleccionar interbibliotecario:
‎=======================================================
‎        GESTIÓN DE PRÉSTAMOS
‎=======================================================
‎1. Listar todos
‎2. Listar por usuario
‎3. Buscar por ID
‎4. Registrar préstamo
‎5. Renovar préstamo
‎0. Volver
‎=======================================================
‎Seleccione: 4
‎ID usuario: USR-000006
‎ID material: MAT-000006
‎Tipo (1=NORMAL, 2=INTERBIBLIOTECARIO):
‎2
‎Error: 400 : "{"exito":false,"mensaje":"Error al crear el préstamo: La sede es obligatoria","data":null}"
‎
‎Enter para continuar...
- ‎el mensaje de error al crear un préstamo con un material préstado muestra el código en vez de solo el texto del error
- ‎error extraño al renovar un préstamo sin ingresar nada
‎=======================================================
‎        GESTIÓN DE PRÉSTAMOS
‎=======================================================
‎1. Listar todos
‎2. Listar por usuario
‎3. Buscar por ID
‎4. Registrar préstamo
‎5. Renovar préstamo
‎0. Volver
‎=======================================================
‎Seleccione: 5
‎ID préstamo:
‎Error: 405 : "{"timestamp":"2026-05-19T18:00:50.190+00:00","status":405,"error":"Method Not Allowed","path":"/prestamos/renovacion"}"
- ‎verificar si al renovar un préstamo se está cumpliendo la regla de que sea:
‎el tiempo establecido por tipo de material 
‎que no exceda el limite de préstamos por usuario 
‎que el usuario no este bloqueado
‎que el material este prestado por el mismo usuario 
‎
‎- al hacer una devolución con daños (responder a la pregunta de material en buen estado: no) la devolución no pregunta nada de opciones de daño para saber si fue un daño leve, grave o irreparable, o si fue perdido
‎-listar las reservas da un error: 400 [no body]
‎-crear una reserva da un error extraño y posiblemente del mismo error si no se ingresan datos 
‎=======================================================
‎        GESTIÓN DE RESERVAS
‎=======================================================
‎1. Listar todas
‎2. Listar por usuario
‎3. Listar por material
‎4. Crear reserva
‎5. Cancelar reserva
‎0. Volver
‎=======================================================
‎Seleccione: 4
‎ID usuario: USR-000001
‎ID material: MAT-000006
‎Tipo (1=NORMAL, 2=INTERBIBLIOTECARIA):
‎1
‎Error: 500 : "{"timestamp":"2026-05-19T18:08:49.907+00:00","status":500,"error":"Internal Server Error","path":"/reservas"}"
‎- listar las multas da este error:
‎=======================================================
‎        GESTIÓN DE MULTAS
‎=======================================================
‎1. Ver todas las multas
‎2. Ver multas por usuario
‎3. Consultar deuda de usuario
‎4. Registrar pago de multa
‎0. Volver
‎=======================================================
‎Seleccione: 1
‎Error: 400 : "{"timestamp":"2026-05-19T18:09:55.495+00:00","status":400,"error":"Bad Request","path":"/multas"}"
- ‎consulTar deuda de usuario da un null en el campo deuda total si el usuario no debe nada, debería mostrar un cero
‎- pagar una deuda permite registrar un pago sin ingresar nada
‎=======================================================
‎        GESTIÓN DE MULTAS
‎=======================================================
‎1. Ver todas las multas
‎2. Ver multas por usuario
‎3. Consultar deuda de usuario
‎4. Registrar pago de multa
‎0. Volver
‎=======================================================
‎Seleccione: 4
‎ID multa:
‎ID usuario:
‎Monto:
‎
‎Pago registrado: 7368770d-d35a-458d-a5e2-20c88274053d
-‎faltan datos mock para probar reservas y multas
‎-la opción de estadísticas ya no existe en el menu
