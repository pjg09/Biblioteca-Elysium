package com.biblioteca.usuarios.dominio;

/**
 * Aggregate root del dominio de usuarios.
 * No es una entidad JPA — representa el modelo de dominio puro.
 */
public class Usuario {

    private String id;
    private String nombre;
    private String email;
    private String tipoUsuario;
    private String estadoUsuario; // ACTIVO / BLOQUEADO_MULTA / BLOQUEADO_PERDIDA / SUSPENDIDO
    private int limiteMaximoPrestamos;

    public Usuario() {}

    public Usuario(String id, String nombre, String email, String tipoUsuario,
                   String estadoUsuario, int limiteMaximoPrestamos) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.tipoUsuario = tipoUsuario;
        this.estadoUsuario = estadoUsuario;
        this.limiteMaximoPrestamos = limiteMaximoPrestamos;
    }

    // -------------------------------------------------------------------------
    // Comportamiento de dominio
    // -------------------------------------------------------------------------

    public boolean isActivo() {
        return "ACTIVO".equals(this.estadoUsuario);
    }

    /**
     * Bloquea al usuario por deuda pendiente.
     * Cambia el estado a BLOQUEADO_MULTA.
     */
    public void bloquearPorDeuda(String motivo) {
        this.estadoUsuario = "BLOQUEADO_MULTA";
    }

    /**
     * Desbloquea al usuario, devolviendo su estado a ACTIVO.
     */
    public void desbloquear() {
        this.estadoUsuario = "ACTIVO";
    }

    // -------------------------------------------------------------------------
    // Getters y setters
    // -------------------------------------------------------------------------

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

    public String getEstadoUsuario() {
        return estadoUsuario;
    }

    public void setEstadoUsuario(String estadoUsuario) {
        this.estadoUsuario = estadoUsuario;
    }

    public int getLimiteMaximoPrestamos() {
        return limiteMaximoPrestamos;
    }

    public void setLimiteMaximoPrestamos(int limiteMaximoPrestamos) {
        this.limiteMaximoPrestamos = limiteMaximoPrestamos;
    }
}
