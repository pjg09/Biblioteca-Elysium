package com.biblioteca.commons.patrones;

/**
 * Interfaz para implementar el patrón State.
 * 
 * El patrón State encapsula el comportamiento condicionado por el estado
 * en objetos separados, permitiendo cambiar el comportamiento sin largas
 * cadenas de if/else.
 * 
 * Ejemplo de implementación para un Préstamo:
 * <pre>
 * public interface IEstadoPrestamo {
 *     void renovar(Prestamo prestamo, LocalDate nuevaFecha);
 *     void devolver(Prestamo prestamo, Evaluacion evaluacion);
 *     boolean puedeRenovarse();
 *     String nombreEstado();
 * }
 * 
 * public class PrestamoActivoState implements IEstadoPrestamo {
 *     public void renovar(Prestamo prestamo, LocalDate nuevaFecha) {
 *         // Lógica específica para renovación en estado activo
 *         prestamo.cambiarEstado(new PrestamoRenovadoState());
 *     }
 *     
 *     public void devolver(Prestamo prestamo, Evaluacion evaluacion) {
 *         // Lógica específica para devolución en estado activo
 *         prestamo.cambiarEstado(new PrestamoCompletadoState());
 *     }
 *     
 *     public boolean puedeRenovarse() {
 *         return true;
 *     }
 *     
 *     public String nombreEstado() {
 *         return "ACTIVO";
 *     }
 * }
 * 
 * public class PrestamoCompletadoState implements IEstadoPrestamo {
 *     // No se puede renovar ni devolver (ya está completado)
 *     public void renovar(Prestamo prestamo, LocalDate nuevaFecha) {
 *         throw new OperacionNoPermitidaException("renovar", "El préstamo ya está completado");
 *     }
 * }
 * </pre>
 * 
 * Beneficios:
 * - Sin grandes if/else en la lógica de negocio
 * - Fácil agregar nuevos estados
 * - Comportamiento claro y testeable por estado
 * - Principio Open/Closed (OCP): abierto a extensión, cerrado a modificación
 */
public interface IEstado<T> {
    /**
     * Devuelve el nombre/identificador del estado.
     */
    String nombreEstado();
}
