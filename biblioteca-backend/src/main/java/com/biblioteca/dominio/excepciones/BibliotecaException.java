package com.biblioteca.dominio.excepciones;

public class BibliotecaException extends Exception {
    private String codigo;
    
    public BibliotecaException(String mensaje, String codigo) {
        super(mensaje);
        this.codigo = codigo;
    }
    
    public String getMensaje() {
        return super.getMessage();
    }
    
    public String getCodigo() {
        return codigo;
    }
}