package com.biblioteca.dominio.excepciones;

import java.util.List;

public class UsuarioBloqueadoException extends BibliotecaException {
    private String idUsuario;
    private List<String> motivosBloqueo;
    private double multaPendiente;
    
    public UsuarioBloqueadoException(String idUsuario, List<String> motivosBloqueo, double multaPendiente) {
        super("Usuario " + idUsuario + " bloqueado. Multa pendiente: $" + multaPendiente, "USR-001");
        this.idUsuario = idUsuario;
        this.motivosBloqueo = motivosBloqueo;
        this.multaPendiente = multaPendiente;
    }
    
    public String getIdUsuario() {
        return idUsuario;
    }
    
    public List<String> getMotivos() {
        return motivosBloqueo;
    }
}