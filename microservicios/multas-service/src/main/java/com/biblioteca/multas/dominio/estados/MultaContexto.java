package com.biblioteca.multas.dominio.estados;

import java.time.LocalDateTime;

/**
 * Contexto que encapsula el estado mutable de la Multa.
 * Pasado a los estados para que puedan modificar el comportamiento de la multa.
 * 
 * Patrón: State Pattern + Contexto mutable
 */
public class MultaContexto {
    private double montoTotal;
    private LocalDateTime fechaPago;
    private IEstadoMulta estadoActual;
    
    public MultaContexto(double montoTotal, IEstadoMulta estadoActual) {
        this.montoTotal = montoTotal;
        this.estadoActual = estadoActual;
    }
    
    // Getters y Setters
    public double getMontoTotal() {
        return montoTotal;
    }
    
    public void setMontoTotal(double montoTotal) {
        this.montoTotal = montoTotal;
    }
    
    public LocalDateTime getFechaPago() {
        return fechaPago;
    }
    
    public void setFechaPago(LocalDateTime fechaPago) {
        this.fechaPago = fechaPago;
    }
    
    public IEstadoMulta getEstadoActual() {
        return estadoActual;
    }
    
    public void setEstadoActual(IEstadoMulta nuevoEstado) {
        this.estadoActual = nuevoEstado;
    }
}
