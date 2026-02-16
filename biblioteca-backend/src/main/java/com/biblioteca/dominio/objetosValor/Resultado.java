package com.biblioteca.dominio.objetosValor;

public class Resultado {
    private boolean exito;
    private String mensaje;
    private Object data;
    
    private Resultado(boolean exito, String mensaje, Object data) {
        this.exito = exito;
        this.mensaje = mensaje;
        this.data = data;
    }
    
    public boolean getExito() {
        return exito;
    }
    
    public String getMensaje() {
        return mensaje;
    }
    
    public Object getData() {
        return data;
    }
    
    public static Resultado Exitoso(String mensaje, Object data) {
        return new Resultado(true, mensaje, data);
    }
    
    public static Resultado Fallido(String mensaje) {
        return new Resultado(false, mensaje, null);
    }
    
    @Override
    public String toString() {
        return "Resultado{" +
                "exito=" + exito +
                ", mensaje='" + mensaje + '\'' +
                ", data=" + data +
                '}';
    }
}