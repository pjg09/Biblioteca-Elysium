package com.biblioteca.usuarios.dominio.agregados;

public class Usuario {

    private String id;
    private String nombre;
    private String email;
    private String tipoUsuario;
    private String estadoUsuario;
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

    public boolean isActivo() {
        return "ACTIVO".equals(this.estadoUsuario);
    }

    public void bloquearPorDeuda(String motivo) {
        this.estadoUsuario = "BLOQUEADO_MULTA";
    }

    public void desbloquear() {
        this.estadoUsuario = "ACTIVO";
    }

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
