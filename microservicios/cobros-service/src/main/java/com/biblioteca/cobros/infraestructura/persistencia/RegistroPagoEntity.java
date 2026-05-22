package com.biblioteca.cobros.infraestructura.persistencia;

import com.biblioteca.cobros.dominio.agregados.RegistroPago;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "registros_pago")
public class RegistroPagoEntity {

    @Id
    @Column(name = "id", nullable = false, length = 36)
    private String id;

    @Column(name = "multa_id", nullable = false)
    private String multaId;

    @Column(name = "usuario_id", nullable = false)
    private String usuarioId;

    @Column(name = "monto", nullable = false)
    private double monto;

    @Column(name = "fecha_pago", nullable = false)
    private LocalDateTime fechaPago;

    @Column(name = "estado", nullable = false)
    private String estado;

    public RegistroPagoEntity() {
    }

    public static RegistroPagoEntity fromDomain(RegistroPago rp) {
        RegistroPagoEntity entity = new RegistroPagoEntity();
        entity.id = rp.getId();
        entity.multaId = rp.getMultaId();
        entity.usuarioId = rp.getUsuarioId();
        entity.monto = rp.getMonto();
        entity.fechaPago = rp.getFechaPago();
        entity.estado = rp.getEstado();
        return entity;
    }

    public RegistroPago toDomain() {
        RegistroPago rp = new RegistroPago();
        rp.setId(this.id);
        rp.setMultaId(this.multaId);
        rp.setUsuarioId(this.usuarioId);
        rp.setMonto(this.monto);
        rp.setFechaPago(this.fechaPago);
        rp.setEstado(this.estado);
        return rp;
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
