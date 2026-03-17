los builders de usuario y material no tienen un director tal y como indican los diagramas que explican este patron, por otra parte tampoco hay una interfaz de bulder (considerar si es necesario, si no lo es está bien)

explica por que usas un objeto de valor para los id de materia y usuario cuando cada clase de usuario y material puede guardar esto. (evalua solid), y por que no usas un objeto de valor para el id de transaccion, prestamo, reserva y multa.

Tener en cuenta esto para las reservas:
Requisito: Solo se pueden reservar libros físicos que estén totalmente prestados.
Recogida: Se dispone de un plazo limitado (a menudo 24 horas) para retirarlo antes de que pase al siguiente usuario.
Restricciones: Usuarios con multas, libros vencidos o suspensiones no pueden realizar reservas.
Cancelación: La reserva se puede cancelar si el libro reservado ya no es necesario. 
si yo saco un libro, y luego lo devuelvo, si quiero volver a acceder a el, el sistema debe validar si alguien lo reservo mientras yo lo tuve, si es asi entonces se le da a esa persona el derecho de poder tomarlo en las 24h, si no lo hace, entonces yo puedo volver a acceder a el. (considerar si es necesario, si no lo es está bien)

otros builders como el de prestamo, reserva y multa tampoco tienen un director ni una interfaz de builder (considerar si es necesario, si no lo es está bien)


por otra parte estaba revisando el prestamo y resulta que hay un praton (factory) para crear los tipos de prestamos, pero a su vez hay un builder para crear los prestamos, no entiendo bien la relacion entre ambos, explicar. revisar si se pueden tener ambos patrones o si es mejor tener uno solo.

las bibliotecas tienen un metodo para crear prestamos, pero no deberia ser asi, hay una clase de transacciones que crea un prestamo y una reserva por que solo tiene el de prestamo?
las bibliotecas factory no estan relacionadas en el diagrama con las factorys que ellas llaman como la del prestamo


viendo el calcularmultaporperdida me di cuenta de que usa un decimal de recargo, supongo que es el porcentaje de lo que debe dar un usuario frente a una multa por perdida, pero no veo donde se define ese porcentaje para cada tipo de usuario y tampoco es como el precio de un material se guarde, no esta en ningun lado (repositorio).

por que calcularmultadano, recibe una calcularcostodano desde una interfaz, como si quisiera recibir cualquier calcularcostodano que exista,  es que calcularcostodano creo que ya es algo general, pero se critico. explica por que en base a los patrones y principios.

prestamo service por ejemplo, tiene un monton de validaciones mientras crea un prestamo. eso esta ya incluyendo un patron? por ejemplo chain o alguno que sea de ese estilo para tener pasos

La fachada no llama a el servicio de renovacion, es el unico que falta.

una devolucion tiene esto:

1.  Registrar devolución simple
2.  Registrar devolución con inspección
3.  Registrar devolución con daños
4.  Ver historial de devoluciones

por que? osea la verdad solo deberia haber dos opciones, devolver y ver historial de devoluciones, ya que la devolucion con inspeccion y con daños es solo una devolucion con daños, y la devolucion simple es una devolucion sin daños, no entiendo por que hay tantas opciones.

la parte de gestion de materiales deberia buscar materiales pero no con tantas opciones, es decir que solo sea "buscar materiales" y que el usuario pueda buscar por titulo, autor, isbn, etc. y que el sistema devuelva los materiales que coincidan con la busqueda. seria una sobrecarga. Pasa lo mismo con los usuarios ya que hay muchas opciones de busqueda. lo mismo con ver reservas (dice por usuario y por material, deberia ser solo sobrecarga) y ver prestamos (dice por usuario y por material, deberia ser solo sobrecarga)


configuarar tarifas no deberia existir

para ciertas acciones como calcular una multa se pide tanto id prestamo, como de usuario y de materia, de muchismo, deberia ser de nuevo una sobrecarga, que sea id de prestamo o id de usuario junto con id de material. (revisar todas las acciones donde se haga esto para cambiarlo)

la captura de errores esta haciendo que el programa falle completamente en caso de que haya un error, deberia ser capaz de manejarlo y mostrar un mensaje de error al usuario. y deje volver a intentarlo:

? CALCULAR MULTA
Tipo de multa:
1. Por retraso
2. Por daño
3. Por pérdida
4. Administrativa
Seleccione: 3
ID Préstamo: PRE-000001
ID Usuario: dada
ID Material: add
[WARNING] 
java.lang.IllegalArgumentException: Material no encontrado
    at com.biblioteca.servicios.calculadores.CalculadorMultaPorPerdida.calcular (CalculadorMultaPorPerdida.java:26)
    at com.biblioteca.servicios.implementaciones.GestorMultasService.calcularMulta (GestorMultasService.java:24)
    at com.biblioteca.consola.MenuConsola.verCalculoMulta (MenuConsola.java:1321)
    at com.biblioteca.consola.MenuConsola.menuMultas (MenuConsola.java:1173)
    at com.biblioteca.consola.MenuConsola.procesarOpcionPrincipal (MenuConsola.java:157)
    at com.biblioteca.consola.MenuCon

esta parte de consultas:
==================================================
            ? CONSULTAS Y REPORTES
============================================================
1.  Ver disponibilidad de material
2.  Ver estado de usuario
3.  Ver estadísticas generales
4.  Ver límites por tipo de usuario
5.  Ver políticas de tiempo
6.  Ver reporte completo
0.  Volver

realmente la esta manejando la consola, no deberia ser asi, deberian haber clases especificas para ver estadisticas como estas, violamos single responsibility. (revisar todas las acciones donde se haga esto para cambiarlo)

la consola no esta en el diagrama. 

Contexto de multa tiene algunos atributos como ids y el tipo de multa pero no veo de donde se crea ese objeto de contexto ni como se le dan esos ids ni el tipo de multa.

validadorreglasservice no tiene relacion directa con la interfaz de ilimitepresatmoservice en el diagrama.

explicame mejor rasultadovalidacion

ContextoValidacion no esta en el diagrama y es una clase interna segun, no deberia ser asi. debe ser aparte como el contextomulta.

explica regla de limite no excedido

para los repositorios por que no se usa un patron? es decir se estan instanciando directamente. Quiero que sea un patron como no se facade o algo especifico para conectar backend con cualquier base de datos, asi sea local, pero que mis service no intancien directamente a los repos. que tomen un dato de cierto lugar, por ejemplo un archivo de configuracion, y que se conecten a la base de datos que se indica en ese archivo. pero en este caso sigue funcionando por una base de datos local en forma de repositorios.

Explica el decorator de las notificaciones y el por que no hay un servicio general de notificaciones.

el eventoprestamo no toma los datos de ningun lado, o al menos no lo llama nadie (no se ve una relacion de alguna clase hacia este en el diagrama)

