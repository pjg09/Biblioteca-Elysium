package com.biblioteca.commons.excepciones;

/**
 * Excepción lanzada cuando se intenta realizar una operación en una Reserva
 * en un estado que no permite dicha operación.
 * 
 * Requiere DOS argumentos: operación y motivo (según CLAUDE.md).
 */
public class OperacionNoPermitidaEnEstadoReservaException extends Exception {

    private final String operacion;
    private final String motivo;

    public OperacionNoPermitidaEnEstadoReservaException(String operacion, String motivo) {
        super(String.format("Operación '%s' no permitida: %s", operacion, motivo));
        this.operacion = operacion;
        this.motivo = motivo;
    }

    public String getOperacion() {
        return operacion;
    }

    public String getMotivo() {
        return motivo;
    }
}
