package com.biblioteca.cobros.dominio;

import java.time.LocalDateTime;
import java.util.UUID;

public class RegistroPago {

    private String id;
    private String multaId;
    private String usuarioId;
    private double monto;
    private LocalDateTime fechaPago;
    private String estado;

    public RegistroPago() {
    }

    private RegistroPago(String id, String multaId, String usuarioId, double monto,
                          LocalDateTime fechaPago, String estado) {
        this.id = id;
        this.multaId = multaId;
        this.usuarioId = usuarioId;
        this.monto = monto;
        this.fechaPago = fechaPago;
        this.estado = estado;
    }

    public static RegistroPago crear(String multaId, String usuarioId, double monto) {
        return new RegistroPago(
            UUID.randomUUID().toString(),
            multaId,
            usuarioId,
            monto,
            LocalDateTime.now(),
            "PROCESADO"
        );
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public LocalDateTime getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(LocalDateTime fechaPago) {
        this.fechaPago = fechaPago;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
