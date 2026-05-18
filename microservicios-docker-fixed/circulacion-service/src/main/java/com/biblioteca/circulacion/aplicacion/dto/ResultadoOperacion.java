package com.biblioteca.circulacion.aplicacion.dto;

public class ResultadoOperacion {

    private boolean exito;
    private String mensaje;
    private Object data;

    private ResultadoOperacion() {}

    public static ResultadoOperacion exitoso(String mensaje, Object data) {
        ResultadoOperacion r = new ResultadoOperacion();
        r.exito = true;
        r.mensaje = mensaje;
        r.data = data;
        return r;
    }

    public static ResultadoOperacion fallido(String mensaje) {
        ResultadoOperacion r = new ResultadoOperacion();
        r.exito = false;
        r.mensaje = mensaje;
        r.data = null;
        return r;
    }

    public boolean isExito() { return exito; }
    public String getMensaje() { return mensaje; }
    public Object getData() { return data; }
}
