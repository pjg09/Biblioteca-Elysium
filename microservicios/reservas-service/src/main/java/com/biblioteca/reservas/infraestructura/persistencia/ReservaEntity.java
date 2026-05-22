package com.biblioteca.reservas.infraestructura.persistencia;

import com.biblioteca.reservas.dominio.agregados.Reserva;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservas")
public class ReservaEntity {

    @Id
    private String id;

    @Column(name = "id_usuario", nullable = false)
    private String idUsuario;

    @Column(name = "id_material", nullable = false)
    private String idMaterial;

    @Column(name = "posicion_cola", nullable = false)
    private int posicionCola;

    @Column(name = "estado_reserva", nullable = false)
    private String estadoReserva;

    @Column(name = "fecha_reserva", nullable = false)
    private LocalDateTime fechaReserva;

    @Column(name = "fecha_notificacion")
    private LocalDateTime fechaNotificacion;

    @Column(name = "fecha_expiracion")
    private LocalDateTime fechaExpiracion;

    @Column(name = "sede")
    private String sede;

    public ReservaEntity() {}

    // -------------------------------------------------------------------------
    // Conversión desde dominio
    // -------------------------------------------------------------------------

    public static ReservaEntity fromDomain(Reserva r) {
        ReservaEntity entity = new ReservaEntity();
        entity.id = r.getId();
        entity.idUsuario = r.getIdUsuario();
        entity.idMaterial = r.getIdMaterial();
        entity.posicionCola = r.getPosicionCola();
        entity.estadoReserva = r.getEstadoReserva();
        entity.fechaReserva = r.getFechaReserva();
        entity.fechaNotificacion = r.getFechaNotificacion();
        entity.fechaExpiracion = r.getFechaExpiracion();
        entity.sede = r.getSede();
        return entity;
    }

    // -------------------------------------------------------------------------
    // Conversión a dominio
    // -------------------------------------------------------------------------

    public Reserva toDomain() {
        return new Reserva(
                id, idUsuario, idMaterial, posicionCola,
                estadoReserva, fechaReserva,
                fechaNotificacion, fechaExpiracion, sede
        );
    }

    // -------------------------------------------------------------------------
    // Getters y Setters
    // -------------------------------------------------------------------------

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getIdUsuario() { return idUsuario; }
    public void setIdUsuario(String idUsuario) { this.idUsuario = idUsuario; }

    public String getIdMaterial() { return idMaterial; }
    public void setIdMaterial(String idMaterial) { this.idMaterial = idMaterial; }

    public int getPosicionCola() { return posicionCola; }
    public void setPosicionCola(int posicionCola) { this.posicionCola = posicionCola; }

    public String getEstadoReserva() { return estadoReserva; }
    public void setEstadoReserva(String estadoReserva) { this.estadoReserva = estadoReserva; }

    public LocalDateTime getFechaReserva() { return fechaReserva; }
    public void setFechaReserva(LocalDateTime fechaReserva) { this.fechaReserva = fechaReserva; }

    public LocalDateTime getFechaNotificacion() { return fechaNotificacion; }
    public void setFechaNotificacion(LocalDateTime fechaNotificacion) { this.fechaNotificacion = fechaNotificacion; }

    public LocalDateTime getFechaExpiracion() { return fechaExpiracion; }
    public void setFechaExpiracion(LocalDateTime fechaExpiracion) { this.fechaExpiracion = fechaExpiracion; }

    public String getSede() { return sede; }
    public void setSede(String sede) { this.sede = sede; }
}
