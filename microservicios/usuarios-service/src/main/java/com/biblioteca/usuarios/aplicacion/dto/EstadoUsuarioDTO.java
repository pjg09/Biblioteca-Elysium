package com.biblioteca.usuarios.aplicacion.dto;

public class EstadoUsuarioDTO {

    private String usuarioId;
    private String nombre;
    private boolean activo;
    private String estadoUsuario;
    private String tipoUsuario;

    public EstadoUsuarioDTO() {}

    public EstadoUsuarioDTO(String usuarioId, String nombre, boolean activo,
                             String estadoUsuario, String tipoUsuario) {
        this.usuarioId = usuarioId;
        this.nombre = nombre;
        this.activo = activo;
        this.estadoUsuario = estadoUsuario;
        this.tipoUsuario = tipoUsuario;
    }

    public String getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(String usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public String getEstadoUsuario() {
        return estadoUsuario;
    }

    public void setEstadoUsuario(String estadoUsuario) {
        this.estadoUsuario = estadoUsuario;
    }

    public String getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(String tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }
}
