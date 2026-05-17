package com.biblioteca.multas.dominio.estados;

/**
 * Excepción lanzada cuando se intenta ejecutar una operación no permitida en el estado actual de la multa.
 */
public class OperacionNoPermitidaEnEstadoMultaException extends Exception {
    private final String operacion;
    private final String estadoActual;
    
    public OperacionNoPermitidaEnEstadoMultaException(String operacion, String estadoActual, String detalles) {
        super("No se puede " + operacion + " en estado " + estadoActual + ". " + detalles);
        this.operacion = operacion;
        this.estadoActual = estadoActual;
    }
    
    public String getOperacion() {
        return operacion;
    }
    
    public String getEstadoActual() {
        return estadoActual;
    }
}
