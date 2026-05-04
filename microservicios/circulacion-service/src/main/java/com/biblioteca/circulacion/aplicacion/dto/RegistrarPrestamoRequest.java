package com.biblioteca.circulacion.aplicacion.dto;

public class RegistrarPrestamoRequest {

    private String idUsuario;
    private String idMaterial;
    private String tipoPrestamo; // NORMAL / INTERBIBLIOTECARIO
    private String sede;

    public RegistrarPrestamoRequest() {}

    public String getIdUsuario() { return idUsuario; }
    public void setIdUsuario(String idUsuario) { this.idUsuario = idUsuario; }

    public String getIdMaterial() { return idMaterial; }
    public void setIdMaterial(String idMaterial) { this.idMaterial = idMaterial; }

    public String getTipoPrestamo() { return tipoPrestamo; }
    public void setTipoPrestamo(String tipoPrestamo) { this.tipoPrestamo = tipoPrestamo; }

    public String getSede() { return sede; }
    public void setSede(String sede) { this.sede = sede; }
}
