package com.biblioteca.circulacion.aplicacion.dto;

public class DeudaPendienteDTO {

    private String usuarioId;
    private double totalDeuda;
    private int multasPendientes;
    private boolean tieneDeuda;

    public DeudaPendienteDTO() {}

    public String getUsuarioId() { return usuarioId; }
    public void setUsuarioId(String usuarioId) { this.usuarioId = usuarioId; }

    public double getTotalDeuda() { return totalDeuda; }
    public void setTotalDeuda(double totalDeuda) { this.totalDeuda = totalDeuda; }

    public int getMultasPendientes() { return multasPendientes; }
    public void setMultasPendientes(int multasPendientes) { this.multasPendientes = multasPendientes; }

    public boolean isTieneDeuda() { return tieneDeuda; }
    public void setTieneDeuda(boolean tieneDeuda) { this.tieneDeuda = tieneDeuda; }
}
