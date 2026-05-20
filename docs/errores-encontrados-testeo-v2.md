‎- al ver la disponibilidad de un material se visualiza el ID del material con el ID correcto y abajo ID: null no debería mostrar esto último 
‎-el error de agregar un nuevo material e ingresar al ID siendo una ID ya existente sigue sobrescribiendo los datos de ese registro con ese ID existente no debería ser así debería retornar un mensaje que diga ese ID ya es utilizado ingrese otro 
‎- al agregar un nuevo usuario e ingresar un ID que ya exista de nuevo se sobreescriben los datos del registro que ya tenía ese ID, la idea de este error y el anterior a ese es separar esa posible sobre escritura para que sea una edición de un usuario y que la opción de agregar un usuario maneje este error para no sobrescribir accidentalmente si se va a agregar un nuevo usuario si se quiere editar ya debería haber otra opción que sí sobre escriba los datos de un usuario ya existente 
‎
‎-al crear un préstamo hiper bibliotecario se espera que hayan más opciones Como por ejemplo nombre de la biblioteca y algún otro dato más que no se tome automáticamente de nuestra base de datos el estado podría ser en espera 
‎
‎-al crear un nuevo registro de préstamo se está creando con un uuid pero se espera que se cree con el mismo formato que otros préstamos PRE-XXXXXX
‎
‎-al registrar un nuevo material sin llenar ninguno de los campos requeridos devuelve un error de precio inválido ingresa un número no está mal pero quiere decir que solo está validando el precio el resto de Campos no están siendo validados deberían de dar un error desde el inicio desde que no se selecciona el tipo de material o si se van llenando Campos y uno de ellos no se llena debería dar un error en ese campo que no se llenó. 
‎
‎-el mismo caso aplica para un estudiante solo da un error de email inválido si no se llenan ninguno de los campos no está mal pero no me está indicando si realmente se están baleando cada uno de los campos y si alguno de estos no se llena debe dar un error en ese campo que no se llenó 
‎
‎-al crear una nueva reserva se está creando con un uuiD, de nuevo se espera que se cree con el formato RES-XXXXXX
‎
‎-crear datos mock para que se pueda probar el flujo del programa cuando hay un usuario con un libro hay dos reservas detrás de ese libro para que se pueda registrar una devolución del libro se actualiza el estado y muestre un mensaje de notificación o alguna forma de verificar que se está tratando de crear el proceso de notificación hacia el primer usuario en la cola de reservas luego que se pueda repetir el proceso para el segundo usuario cuando el anterior y ahaya prestado y devolvido el material 
‎
‎
‎-crear también datos mok con usuarios que tengan el límite de préstamos al máximo para tratar de crear un nuevo préstamo y ver qué pasa y con usuarios que estén en un estado distinto al estado normal para ver si eso afecta en el préstamo.
‎
‎-debería haber una opción en la gestión de multas que deje ver los detalles de la multa para poder leer el texto completo del motivo Ya que puede ser muy largo y para ver estos detalles solo debería ingresarse el ID de la multa 
‎
‎-habiendo un dato mock como registro de multa con un usuario se trató de consultar la multa por usuario se ingresó el mismo ID del usuario que ya estaba registrado en esta multa y resultó dando cero multas pendientes y una deuda total de cero a pesar de que el estado estaba en pendiente 
‎
‎-se realizó un registro de pago de multa para el único de datos mok que hay no debería pedirse una ID de usuario ya que se pide el ID de la multa antes de ese ID (es redundante) la idea de ingresar un monto está bien si se considera que un usuario no tendría que pagar todo completo pero al ingresar un monto menor al monto total de la deuda debe el sistema calcular cuánto le hace falta y si aún le hace falta el estado de la multa debe seguir en pendiente 
‎-cuando se realizó el registro de pago de multa se ingresó el monto total a pagar pero se revisó de nuevo la lista de las multas y esta multa aparecía aún en pendiente y con el mismo monto, 
‎-al registrar un pago de multa se creó correctamente el pago con un uuid pero no hay forma de listar estos pagos ya hechos debería haber una lista de pagos creados
