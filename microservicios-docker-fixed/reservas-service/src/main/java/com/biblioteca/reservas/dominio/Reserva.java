package com.biblioteca.reservas.dominio;

import java.time.LocalDateTime;

/**
 * Entidad de dominio (no JPA) que representa una reserva en la cola de espera.
 * BC8 — Bounded Context: Gestión de reservas.
 */
public class Reserva {

    private String id;
    private String idUsuario;
    private String idMaterial;
    private int posicionCola;
    private String estadoReserva;   // EN_ESPERA | NOTIFICADA | COMPLETADA | CANCELADA
    private LocalDateTime fechaReserva;
    private LocalDateTime fechaNotificacion;
    private LocalDateTime fechaExpiracion;
    private String sede;

    public Reserva() {}

    public Reserva(String id, String idUsuario, String idMaterial, int posicionCola,
                   String estadoReserva, LocalDateTime fechaReserva,
                   LocalDateTime fechaNotificacion, LocalDateTime fechaExpiracion,
                   String sede) {
        this.id = id;
        this.idUsuario = idUsuario;
        this.idMaterial = idMaterial;
        this.posicionCola = posicionCola;
        this.estadoReserva = estadoReserva;
        this.fechaReserva = fechaReserva;
        this.fechaNotificacion = fechaNotificacion;
        this.fechaExpiracion = fechaExpiracion;
        this.sede = sede;
    }

    // -------------------------------------------------------------------------
    // Comportamiento de dominio
    // -------------------------------------------------------------------------

    /**
     * Notifica al usuario que el material está disponible.
     * Precondición: estado == EN_ESPERA y posicionCola == 1.
     */
    public void notificarDisponibilidad(LocalDateTime ahora) {
        if (!"EN_ESPERA".equals(estadoReserva)) {
            throw new IllegalStateException(
                    "Solo se puede notificar una reserva EN_ESPERA. Estado actual: " + estadoReserva);
        }
        if (posicionCola != 1) {
            throw new IllegalStateException(
                    "Solo se puede notificar la reserva en posición 1. Posición actual: " + posicionCola);
        }
        this.estadoReserva = "NOTIFICADA";
        this.fechaNotificacion = ahora;
        this.fechaExpiracion = ahora.plusHours(24);
    }

    /**
     * Expira la reserva cuando el usuario no recogió el material en 24 horas.
     * Precondición: estado == NOTIFICADA.
     */
    public void expirar() {
        if (!"NOTIFICADA".equals(estadoReserva)) {
            throw new IllegalStateException(
                    "Solo se puede expirar una reserva NOTIFICADA. Estado actual: " + estadoReserva);
        }
        this.estadoReserva = "CANCELADA";
    }

    /**
     * Cancela la reserva por solicitud del usuario o del sistema.
     * Precondición: estado == EN_ESPERA o NOTIFICADA.
     */
    public void cancelar() {
        if (!"EN_ESPERA".equals(estadoReserva) && !"NOTIFICADA".equals(estadoReserva)) {
            throw new IllegalStateException(
                    "Solo se puede cancelar una reserva EN_ESPERA o NOTIFICADA. Estado actual: " + estadoReserva);
        }
        this.estadoReserva = "CANCELADA";
    }

    /**
     * Indica si la reserva aún está activa (pendiente de resolución).
     */
    public boolean isActiva() {
        return "EN_ESPERA".equals(estadoReserva) || "NOTIFICADA".equals(estadoReserva);
    }

    // -------------------------------------------------------------------------
    // Getters
    // -------------------------------------------------------------------------

    public String getId() {
        return id;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public String getIdMaterial() {
        return idMaterial;
    }

    public int getPosicionCola() {
        return posicionCola;
    }

    public String getEstadoReserva() {
        return estadoReserva;
    }

    public LocalDateTime getFechaReserva() {
        return fechaReserva;
    }

    public LocalDateTime getFechaNotificacion() {
        return fechaNotificacion;
    }

    public LocalDateTime getFechaExpiracion() {
        return fechaExpiracion;
    }

    public String getSede() {
        return sede;
    }

    public void setPosicionCola(int posicionCola) {
        this.posicionCola = posicionCola;
    }
}
