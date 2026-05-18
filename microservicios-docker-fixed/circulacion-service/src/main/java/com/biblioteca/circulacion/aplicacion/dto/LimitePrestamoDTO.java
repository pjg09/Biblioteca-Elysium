package com.biblioteca.circulacion.aplicacion.dto;

public class LimitePrestamoDTO {

    private String usuarioId;
    private String tipoUsuario;
    private int limiteMaximo;

    public LimitePrestamoDTO() {}

    public String getUsuarioId() { return usuarioId; }
    public void setUsuarioId(String usuarioId) { this.usuarioId = usuarioId; }

    public String getTipoUsuario() { return tipoUsuario; }
    public void setTipoUsuario(String tipoUsuario) { this.tipoUsuario = tipoUsuario; }

    public int getLimiteMaximo() { return limiteMaximo; }
    public void setLimiteMaximo(int limiteMaximo) { this.limiteMaximo = limiteMaximo; }
}
