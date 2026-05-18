package com.biblioteca.cobros.infraestructura.persistencia;

import jakarta.persistence.*;

@Entity
@Table(name = "deudas_pendientes")
public class DeudaPendienteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "multa_id", unique = true, nullable = false)
    private String multaId;

    @Column(name = "usuario_id", nullable = false)
    private String usuarioId;

    @Column(name = "monto", nullable = false)
    private double monto;

    @Column(name = "pagado", nullable = false)
    private boolean pagado = false;

    public DeudaPendienteEntity() {
    }

    public DeudaPendienteEntity(String multaId, String usuarioId, double monto) {
        this.multaId = multaId;
        this.usuarioId = usuarioId;
        this.monto = monto;
        this.pagado = false;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public boolean isPagado() {
        return pagado;
    }

    public void setPagado(boolean pagado) {
        this.pagado = pagado;
    }
}
