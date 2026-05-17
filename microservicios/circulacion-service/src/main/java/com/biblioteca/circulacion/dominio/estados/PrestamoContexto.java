package com.biblioteca.circulacion.dominio.estados;

import java.time.LocalDateTime;

/**
 * Contexto que encapsula el estado mutable del Préstamo.
 * Pasado a los estados para que puedan modificar el comportamiento del préstamo.
 * 
 * Patrón: State Pattern + Contexto mutable
 */
public class PrestamoContexto {
    private LocalDateTime fechaDevolucionEsperada;
    private LocalDateTime fechaDevolucionReal;
    private int renovacionesUsadas;
    private IEstadoPrestamo estadoActual;
    
    public PrestamoContexto(LocalDateTime fechaDevolucionEsperada, int renovacionesUsadas, IEstadoPrestamo estadoActual) {
        this.fechaDevolucionEsperada = fechaDevolucionEsperada;
        this.renovacionesUsadas = renovacionesUsadas;
        this.estadoActual = estadoActual;
    }
    
    // Getters y Setters
    public LocalDateTime getFechaDevolucionEsperada() {
        return fechaDevolucionEsperada;
    }
    
    public void setFechaDevolucionEsperada(LocalDateTime fechaDevolucionEsperada) {
        this.fechaDevolucionEsperada = fechaDevolucionEsperada;
    }
    
    public LocalDateTime getFechaDevolucionReal() {
        return fechaDevolucionReal;
    }
    
    public void setFechaDevolucionReal(LocalDateTime fechaDevolucionReal) {
        this.fechaDevolucionReal = fechaDevolucionReal;
    }
    
    public int getRenovacionesUsadas() {
        return renovacionesUsadas;
    }
    
    public void setRenovacionesUsadas(int renovacionesUsadas) {
        this.renovacionesUsadas = renovacionesUsadas;
    }
    
    public IEstadoPrestamo getEstadoActual() {
        return estadoActual;
    }
    
    public void setEstadoActual(IEstadoPrestamo nuevoEstado) {
        this.estadoActual = nuevoEstado;
    }
}
