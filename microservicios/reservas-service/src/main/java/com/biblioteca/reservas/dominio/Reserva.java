package com.biblioteca.reservas.dominio;

import com.biblioteca.commons.excepciones.OperacionNoPermitidaEnEstadoReservaException;
import com.biblioteca.reservas.dominio.estados.*;
import java.time.LocalDateTime;

/**
 * Agregado Reserva implementando State Pattern.
 * 
 * El comportamiento de la reserva varía según su estado:
 * - EN_ESPERA: En la cola, esperando disponibilidad
 * - NOTIFICADA: Material disponible, usuario notificado (24h para recoger)
 * - COMPLETADA: Usuario recogió el material (terminal)
 * - CANCELADA: Reserva cancelada (terminal)
 * - EXPIRADA: Expiró sin recoger (terminal)
 * 
 * Ventajas sobre if/else:
 * - Cada estado es testeable independientemente
 * - Fácil agregar nuevos estados
 * - Lógica clara y sin ramas complejas
 * - Cumple con OCP (Open/Closed Principle)
 * 
 * BC8 — Bounded Context: Gestión de reservas.
 */
public class Reserva {

    private String id;
    private String idUsuario;
    private String idMaterial;
    private int posicionCola;
    private IEstadoReserva estado; // Objeto de estado, no un String
    private LocalDateTime fechaReserva;
    private LocalDateTime fechaNotificacion;
    private LocalDateTime fechaExpiracion;
    private String sede;

    public Reserva() {}

    public Reserva(String id, String idUsuario, String idMaterial, int posicionCola,
                   String estadoString, LocalDateTime fechaReserva,
                   LocalDateTime fechaNotificacion, LocalDateTime fechaExpiracion,
                   String sede) {
        this.id = id;
        this.idUsuario = idUsuario;
        this.idMaterial = idMaterial;
        this.posicionCola = posicionCola;
        this.estado = reconstruirEstado(estadoString); // Convertir String a estado
        this.fechaReserva = fechaReserva;
        this.fechaNotificacion = fechaNotificacion;
        this.fechaExpiracion = fechaExpiracion;
        this.sede = sede;
    }

    // -------------------------------------------------------------------------
    // Comportamiento de dominio con State Pattern
    // -------------------------------------------------------------------------

    /**
     * Notifica al usuario que el material está disponible.
     * Delega al estado actual para validar y ejecutar la operación.
     */
    public void notificarDisponibilidad(LocalDateTime ahora)
            throws OperacionNoPermitidaEnEstadoReservaException {
        ReservaContexto contexto = new ReservaContexto(this.posicionCola, this.estado);
        
        // El estado decide si se puede notificar
        this.estado.notificar(ahora, contexto);
        
        // Si no lanzó excepción, actualizar el estado de la reserva
        this.posicionCola = contexto.getPosicionCola();
        this.fechaNotificacion = contexto.getFechaNotificacion();
        this.fechaExpiracion = contexto.getFechaExpiracion();
        this.estado = contexto.getEstadoActual();
    }

    /**
     * Expira la reserva cuando el usuario no recogió el material en 24 horas.
     * Delega al estado actual para validar y ejecutar la operación.
     */
    public void expirar()
            throws OperacionNoPermitidaEnEstadoReservaException {
        ReservaContexto contexto = new ReservaContexto(this.posicionCola, this.estado);
        
        // El estado decide si se puede expirar
        this.estado.expirar(contexto);
        
        // Si no lanzó excepción, actualizar el estado de la reserva
        this.estado = contexto.getEstadoActual();
    }

    /**
     * Cancela la reserva por solicitud del usuario o del sistema.
     * Delega al estado actual para validar y ejecutar la operación.
     */
    public void cancelar()
            throws OperacionNoPermitidaEnEstadoReservaException {
        ReservaContexto contexto = new ReservaContexto(this.posicionCola, this.estado);
        
        // El estado decide si se puede cancelar
        this.estado.cancelar(contexto);
        
        // Si no lanzó excepción, actualizar el estado de la reserva
        this.estado = contexto.getEstadoActual();
    }

    /**
     * Completa la reserva cuando el usuario recoge el material.
     * Delega al estado actual para validar y ejecutar la operación.
     */
    public void completar()
            throws OperacionNoPermitidaEnEstadoReservaException {
        ReservaContexto contexto = new ReservaContexto(this.posicionCola, this.estado);
        
        // El estado decide si se puede completar
        this.estado.completar(contexto);
        
        // Si no lanzó excepción, actualizar el estado de la reserva
        this.estado = contexto.getEstadoActual();
    }

    /**
     * Indica si la reserva aún está activa (pendiente de resolución).
     */
    public boolean isActiva() {
        return this.estado.esActiva();
    }

    // -------------------------------------------------------------------------
    // Getters y Setters
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

    /**
     * Retorna el estado de la reserva como String para compatibilidad con persistencia.
     */
    public String getEstadoReserva() {
        return estado.nombreEstado();
    }

    public IEstadoReserva getEstadoObjeto() {
        return estado;
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

    // --------- Helper privado ---------

    /**
     * Convierte un String de estado a un objeto IEstadoReserva.
     * Útil para reconstrucción desde persistencia.
     */
    private static IEstadoReserva reconstruirEstado(String estadoString) {
        switch (estadoString) {
            case "EN_ESPERA":
                return new ReservaEnEsperaState();
            case "NOTIFICADA":
                return new ReservaNotificadaState();
            case "COMPLETADA":
                return new ReservaCompletadaState();
            case "CANCELADA":
                return new ReservaCanceladaState();
            case "EXPIRADA":
                return new ReservaExpiradaState();
            default:
                throw new IllegalArgumentException("Estado de reserva desconocido: " + estadoString);
        }
    }
}
