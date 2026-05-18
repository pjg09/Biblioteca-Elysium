package com.biblioteca.usuarios.aplicacion.dto;

public class CrearUsuarioRequest {

    private String id;
    private String nombre;
    private String email;
    private String tipoUsuario;
    private int limiteMaximoPrestamos;

    public CrearUsuarioRequest() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(String tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    public int getLimiteMaximoPrestamos() {
        return limiteMaximoPrestamos;
    }

    public void setLimiteMaximoPrestamos(int limiteMaximoPrestamos) {
        this.limiteMaximoPrestamos = limiteMaximoPrestamos;
    }
}
