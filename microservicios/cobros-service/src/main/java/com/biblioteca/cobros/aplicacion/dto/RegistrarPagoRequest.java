package com.biblioteca.cobros.aplicacion.dto;

public class RegistrarPagoRequest {

    private String multaId;
    private String usuarioId;
    private double monto;

    public RegistrarPagoRequest() {
    }

    public RegistrarPagoRequest(String multaId, String usuarioId, double monto) {
        this.multaId = multaId;
        this.usuarioId = usuarioId;
        this.monto = monto;
    }

    public String getMultaId() {
        return multaId;
    }

    public void setMultaId(String multaId) {
        this.multaId = multaId;
    }

    public String getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(String usuarioId) {
        this.usuarioId = usuarioId;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }
}
