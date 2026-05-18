package com.biblioteca.multas.dominio;

import java.time.LocalDateTime;

public class Multa {

    private String id;
    private String idPrestamo;
    private String idUsuario;
    private String tipoMulta;
    private double montoTotal;
    private String estado;
    private LocalDateTime fechaGeneracion;
    private LocalDateTime fechaPago;
    private String motivo;

    public Multa() {
    }

    public Multa(String id, String idPrestamo, String idUsuario, String tipoMulta,
                 double montoTotal, String estado, LocalDateTime fechaGeneracion,
                 LocalDateTime fechaPago, String motivo) {
        this.id = id;
        this.idPrestamo = idPrestamo;
        this.idUsuario = idUsuario;
        this.tipoMulta = tipoMulta;
        this.montoTotal = montoTotal;
        this.estado = estado;
        this.fechaGeneracion = fechaGeneracion;
        this.fechaPago = fechaPago;
        this.motivo = motivo;
    }

    public boolean esPendiente() {
        return "PENDIENTE".equals(this.estado);
    }

    public void pagar(LocalDateTime fecha) {
        if (!esPendiente()) {
            throw new IllegalStateException(
                "No se puede pagar una multa que no está en estado PENDIENTE. Estado actual: " + this.estado
            );
        }
        this.estado = "PAGADA";
        this.fechaPago = fecha;
    }

    public void condonar() {
        if (!esPendiente()) {
            throw new IllegalStateException(
                "No se puede condonar una multa que no está en estado PENDIENTE. Estado actual: " + this.estado
            );
        }
        this.estado = "CONDONADA";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdPrestamo() {
        return idPrestamo;
    }

    public void setIdPrestamo(String idPrestamo) {
        this.idPrestamo = idPrestamo;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getTipoMulta() {
        return tipoMulta;
    }

    public void setTipoMulta(String tipoMulta) {
        this.tipoMulta = tipoMulta;
    }

    public double getMontoTotal() {
        return montoTotal;
    }

    public void setMontoTotal(double montoTotal) {
        this.montoTotal = montoTotal;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaGeneracion() {
        return fechaGeneracion;
    }

    public void setFechaGeneracion(LocalDateTime fechaGeneracion) {
        this.fechaGeneracion = fechaGeneracion;
    }

    public LocalDateTime getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(LocalDateTime fechaPago) {
        this.fechaPago = fechaPago;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
}
