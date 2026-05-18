package com.biblioteca.reservas.aplicacion.dto;

public class CrearReservaRequest {

    private String idUsuario;
    private String idMaterial;
    private String sede;
    private String tipoReserva;

    public CrearReservaRequest() {}

    public CrearReservaRequest(String idUsuario, String idMaterial, String sede, String tipoReserva) {
        this.idUsuario = idUsuario;
        this.idMaterial = idMaterial;
        this.sede = sede;
        this.tipoReserva = tipoReserva;
    }

    public String getIdUsuario() { return idUsuario; }
    public void setIdUsuario(String idUsuario) { this.idUsuario = idUsuario; }

    public String getIdMaterial() { return idMaterial; }
    public void setIdMaterial(String idMaterial) { this.idMaterial = idMaterial; }

    public String getSede() { return sede; }
    public void setSede(String sede) { this.sede = sede; }

    public String getTipoReserva() { return tipoReserva; }
    public void setTipoReserva(String tipoReserva) { this.tipoReserva = tipoReserva; }
}
