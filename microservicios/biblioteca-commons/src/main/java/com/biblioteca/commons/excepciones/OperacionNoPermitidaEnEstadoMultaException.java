package com.biblioteca.commons.excepciones;

/**
 * Excepción lanzada cuando se intenta realizar una operación en una Multa
 * en un estado que no permite dicha operación.
 * 
 * Requiere DOS argumentos: operación y motivo (según CLAUDE.md).
 */
public class OperacionNoPermitidaEnEstadoMultaException extends Exception {

    private final String operacion;
    private final String motivo;

    public OperacionNoPermitidaEnEstadoMultaException(String operacion, String motivo) {
        super(String.format("Operación '%s' no permitida en Multa: %s", operacion, motivo));
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
