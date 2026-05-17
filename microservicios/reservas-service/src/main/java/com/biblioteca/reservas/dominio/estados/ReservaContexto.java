package com.biblioteca.reservas.dominio.estados;

import java.time.LocalDateTime;

/**
 * Contexto que encapsula el estado mutable de la Reserva.
 * Pasado a los estados para que puedan modificar el comportamiento de la reserva.
 * 
 * Patrón: State Pattern + Contexto mutable
 */
public class ReservaContexto {
    private int posicionCola;
    private LocalDateTime fechaNotificacion;
    private LocalDateTime fechaExpiracion;
    private IEstadoReserva estadoActual;
    
    public ReservaContexto(int posicionCola, IEstadoReserva estadoActual) {
        this.posicionCola = posicionCola;
        this.estadoActual = estadoActual;
    }
    
    // Getters y Setters
    public int getPosicionCola() {
        return posicionCola;
    }
    
    public void setPosicionCola(int posicionCola) {
        this.posicionCola = posicionCola;
    }
    
    public LocalDateTime getFechaNotificacion() {
        return fechaNotificacion;
    }
    
    public void setFechaNotificacion(LocalDateTime fechaNotificacion) {
        this.fechaNotificacion = fechaNotificacion;
    }
    
    public LocalDateTime getFechaExpiracion() {
        return fechaExpiracion;
    }
    
    public void setFechaExpiracion(LocalDateTime fechaExpiracion) {
        this.fechaExpiracion = fechaExpiracion;
    }
    
    public IEstadoReserva getEstadoActual() {
        return estadoActual;
    }
    
    public void setEstadoActual(IEstadoReserva nuevoEstado) {
        this.estadoActual = nuevoEstado;
    }
}
