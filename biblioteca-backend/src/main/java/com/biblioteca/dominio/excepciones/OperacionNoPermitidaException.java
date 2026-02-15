package com.biblioteca.dominio.excepciones;

public class OperacionNoPermitidaException extends BibliotecaException {
    private String operacion;
    
    public OperacionNoPermitidaException(String operacion, String motivo) {
        super("Operación " + operacion + " no permitida: " + motivo, "OP-001");
        this.operacion = operacion;
    }
    
    public String getOperacion() {
        return operacion;
    }
}