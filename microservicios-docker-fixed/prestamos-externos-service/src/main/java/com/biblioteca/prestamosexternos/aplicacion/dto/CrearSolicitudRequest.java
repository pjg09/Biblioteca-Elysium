package com.biblioteca.prestamosexternos.aplicacion.dto;

public class CrearSolicitudRequest {

    private String prestamoId;
    private String idUsuario;
    private String idMaterial;
    private String bibliotecaOrigen;
    private String bibliotecaDestino;
    private double costoTransporte;

    public CrearSolicitudRequest() {}

    public CrearSolicitudRequest(String prestamoId, String idUsuario, String idMaterial,
                                  String bibliotecaOrigen, String bibliotecaDestino,
                                  double costoTransporte) {
        this.prestamoId = prestamoId;
        this.idUsuario = idUsuario;
        this.idMaterial = idMaterial;
        this.bibliotecaOrigen = bibliotecaOrigen;
        this.bibliotecaDestino = bibliotecaDestino;
        this.costoTransporte = costoTransporte;
    }

    public String getPrestamoId() { return prestamoId; }
    public void setPrestamoId(String prestamoId) { this.prestamoId = prestamoId; }

    public String getIdUsuario() { return idUsuario; }
    public void setIdUsuario(String idUsuario) { this.idUsuario = idUsuario; }

    public String getIdMaterial() { return idMaterial; }
    public void setIdMaterial(String idMaterial) { this.idMaterial = idMaterial; }

    public String getBibliotecaOrigen() { return bibliotecaOrigen; }
    public void setBibliotecaOrigen(String bibliotecaOrigen) { this.bibliotecaOrigen = bibliotecaOrigen; }

    public String getBibliotecaDestino() { return bibliotecaDestino; }
    public void setBibliotecaDestino(String bibliotecaDestino) { this.bibliotecaDestino = bibliotecaDestino; }

    public double getCostoTransporte() { return costoTransporte; }
    public void setCostoTransporte(double costoTransporte) { this.costoTransporte = costoTransporte; }
}
