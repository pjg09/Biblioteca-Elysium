package com.biblioteca.prestamosexternos.infraestructura.persistencia;

import com.biblioteca.prestamosexternos.dominio.agregados.SolicitudExterna;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "solicitudes_externas")
public class SolicitudExternaEntity {

    @Id
    private String id;

    @Column(name = "prestamo_id", nullable = false)
    private String prestamoId;

    @Column(name = "id_usuario", nullable = false)
    private String idUsuario;

    @Column(name = "id_material", nullable = false)
    private String idMaterial;

    @Column(name = "biblioteca_origen", nullable = false)
    private String bibliotecaOrigen;

    @Column(name = "biblioteca_destino", nullable = false)
    private String bibliotecaDestino;

    @Column(name = "costo_transporte", nullable = false)
    private double costoTransporte;

    @Column(name = "estado", nullable = false)
    private String estado;

    @Column(name = "fecha_solicitud", nullable = false)
    private LocalDateTime fechaSolicitud;

    @Column(name = "fecha_devolucion_esperada", nullable = false)
    private LocalDateTime fechaDevolucionEsperada;

    @Column(name = "dias_prestamo", nullable = false)
    private int diasPrestamo;

    public SolicitudExternaEntity() {}

    // -------------------------------------------------------------------------
    // Conversión desde dominio
    // -------------------------------------------------------------------------

    public static SolicitudExternaEntity fromDomain(SolicitudExterna s) {
        SolicitudExternaEntity entity = new SolicitudExternaEntity();
        entity.id = s.getId();
        entity.prestamoId = s.getPrestamoId();
        entity.idUsuario = s.getIdUsuario();
        entity.idMaterial = s.getIdMaterial();
        entity.bibliotecaOrigen = s.getBibliotecaOrigen();
        entity.bibliotecaDestino = s.getBibliotecaDestino();
        entity.costoTransporte = s.getCostoTransporte();
        entity.estado = s.getEstado();
        entity.fechaSolicitud = s.getFechaSolicitud();
        entity.fechaDevolucionEsperada = s.getFechaDevolucionEsperada();
        entity.diasPrestamo = s.getDiasPrestamo();
        return entity;
    }

    // -------------------------------------------------------------------------
    // Conversión a dominio
    // -------------------------------------------------------------------------

    public SolicitudExterna toDomain() {
        return new SolicitudExterna(
                id, prestamoId, idUsuario, idMaterial,
                bibliotecaOrigen, bibliotecaDestino, costoTransporte,
                estado, fechaSolicitud, fechaDevolucionEsperada, diasPrestamo
        );
    }

    // -------------------------------------------------------------------------
    // Getters y Setters
    // -------------------------------------------------------------------------

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

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

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public LocalDateTime getFechaSolicitud() { return fechaSolicitud; }
    public void setFechaSolicitud(LocalDateTime fechaSolicitud) { this.fechaSolicitud = fechaSolicitud; }

    public LocalDateTime getFechaDevolucionEsperada() { return fechaDevolucionEsperada; }
    public void setFechaDevolucionEsperada(LocalDateTime fechaDevolucionEsperada) { this.fechaDevolucionEsperada = fechaDevolucionEsperada; }

    public int getDiasPrestamo() { return diasPrestamo; }
    public void setDiasPrestamo(int diasPrestamo) { this.diasPrestamo = diasPrestamo; }
}
