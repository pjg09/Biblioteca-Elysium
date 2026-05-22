package com.biblioteca.prestamosexternos.dominio.agregados;

import java.time.LocalDateTime;
import java.util.UUID;

public class SolicitudExterna {

    private static final int DIAS_PRESTAMO_INTERBIBLIOTECARIO = 20;

    private String id;
    private String prestamoId;
    private String idUsuario;
    private String idMaterial;
    private String bibliotecaOrigen;
    private String bibliotecaDestino;
    private double costoTransporte;
    private String estado;
    private LocalDateTime fechaSolicitud;
    private LocalDateTime fechaDevolucionEsperada;
    private int diasPrestamo;

    public SolicitudExterna() {}

    public SolicitudExterna(String id, String prestamoId, String idUsuario, String idMaterial,
                             String bibliotecaOrigen, String bibliotecaDestino,
                             double costoTransporte, String estado,
                             LocalDateTime fechaSolicitud, LocalDateTime fechaDevolucionEsperada,
                             int diasPrestamo) {
        this.id = id;
        this.prestamoId = prestamoId;
        this.idUsuario = idUsuario;
        this.idMaterial = idMaterial;
        this.bibliotecaOrigen = bibliotecaOrigen;
        this.bibliotecaDestino = bibliotecaDestino;
        this.costoTransporte = costoTransporte;
        this.estado = estado;
        this.fechaSolicitud = fechaSolicitud;
        this.fechaDevolucionEsperada = fechaDevolucionEsperada;
        this.diasPrestamo = diasPrestamo;
    }

    public static SolicitudExterna crear(String prestamoId, String idUsuario, String idMaterial,
                                          String bibliotecaOrigen, String bibliotecaDestino,
                                          double costoTransporte) {
        LocalDateTime ahora = LocalDateTime.now();
        return new SolicitudExterna(
                UUID.randomUUID().toString(),
                prestamoId,
                idUsuario,
                idMaterial,
                bibliotecaOrigen,
                bibliotecaDestino,
                costoTransporte,
                "PENDIENTE",
                ahora,
                ahora.plusDays(DIAS_PRESTAMO_INTERBIBLIOTECARIO),
                DIAS_PRESTAMO_INTERBIBLIOTECARIO
        );
    }

    public void aprobar() {
        if (!"PENDIENTE".equals(estado)) {
            throw new IllegalStateException(
                    "Solo se puede aprobar una solicitud PENDIENTE. Estado actual: " + estado);
        }
        this.estado = "APROBADA";
    }

    public void rechazar() {
        if (!"PENDIENTE".equals(estado)) {
            throw new IllegalStateException(
                    "Solo se puede rechazar una solicitud PENDIENTE. Estado actual: " + estado);
        }
        this.estado = "RECHAZADA";
    }

    public void completar() {
        if (!"APROBADA".equals(estado)) {
            throw new IllegalStateException(
                    "Solo se puede completar una solicitud APROBADA. Estado actual: " + estado);
        }
        this.estado = "COMPLETADA";
    }

    public String getId() { return id; }
    public String getPrestamoId() { return prestamoId; }
    public String getIdUsuario() { return idUsuario; }
    public String getIdMaterial() { return idMaterial; }
    public String getBibliotecaOrigen() { return bibliotecaOrigen; }
    public String getBibliotecaDestino() { return bibliotecaDestino; }
    public double getCostoTransporte() { return costoTransporte; }
    public String getEstado() { return estado; }
    public LocalDateTime getFechaSolicitud() { return fechaSolicitud; }
    public LocalDateTime getFechaDevolucionEsperada() { return fechaDevolucionEsperada; }
    public int getDiasPrestamo() { return diasPrestamo; }
}
