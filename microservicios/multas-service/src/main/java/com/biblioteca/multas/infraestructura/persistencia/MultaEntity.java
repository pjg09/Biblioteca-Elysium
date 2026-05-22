package com.biblioteca.multas.infraestructura.persistencia;

import com.biblioteca.multas.dominio.agregados.Multa;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "multas")
public class MultaEntity {

    @Id
    @Column(name = "id", nullable = false, length = 36)
    private String id;

    @Column(name = "id_prestamo", nullable = false)
    private String idPrestamo;

    @Column(name = "id_usuario", nullable = false)
    private String idUsuario;

    @Column(name = "tipo_multa", nullable = false)
    private String tipoMulta;

    @Column(name = "monto_total", nullable = false)
    private double montoTotal;

    @Column(name = "estado", nullable = false)
    private String estado;

    @Column(name = "fecha_generacion", nullable = false)
    private LocalDateTime fechaGeneracion;

    @Column(name = "fecha_pago")
    private LocalDateTime fechaPago;

    @Column(name = "motivo", length = 500)
    private String motivo;

    public MultaEntity() {
    }

    public static MultaEntity fromDomain(Multa m) {
        MultaEntity entity = new MultaEntity();
        entity.id = m.getId();
        entity.idPrestamo = m.getIdPrestamo();
        entity.idUsuario = m.getIdUsuario();
        entity.tipoMulta = m.getTipoMulta();
        entity.montoTotal = m.getMontoTotal();
        entity.estado = m.getEstado();
        entity.fechaGeneracion = m.getFechaGeneracion();
        entity.fechaPago = m.getFechaPago();
        entity.motivo = m.getMotivo();
        return entity;
    }

    public Multa toDomain() {
        return new Multa(
            this.id,
            this.idPrestamo,
            this.idUsuario,
            this.tipoMulta,
            this.montoTotal,
            this.estado,
            this.fechaGeneracion,
            this.fechaPago,
            this.motivo
        );
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
