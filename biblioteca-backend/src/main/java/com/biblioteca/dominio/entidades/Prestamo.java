package com.biblioteca.dominio.entidades;

import java.time.LocalDateTime;

public abstract class Prestamo extends Transaccion {
    protected LocalDateTime fechaPrestamo;
    protected LocalDateTime fechaDevolucionEsperada;
    protected LocalDateTime fechaDevolucionReal;
    protected int renovacionesUsadas;
    
    public Prestamo(String idUsuario, String idMaterial, LocalDateTime fechaPrestamo, LocalDateTime fechaDevolucionEsperada) {
        super(idUsuario, idMaterial);
        this.fechaPrestamo = fechaPrestamo;
        this.fechaDevolucionEsperada = fechaDevolucionEsperada;
        this.renovacionesUsadas = 0;
    }
    
    public LocalDateTime getFechaPrestamo() {
        return fechaPrestamo;
    }
    
    public LocalDateTime getFechaDevolucionEsperada() {
        return fechaDevolucionEsperada;
    }
    
    public LocalDateTime getFechaDevolucionReal() {
        return fechaDevolucionReal;
    }
    
    public void setFechaDevolucionReal(LocalDateTime fecha) {
        this.fechaDevolucionReal = fecha;
    }
    
    public int getRenovacionesUsadas() {
        return renovacionesUsadas;
    }
    
    public void incrementarRenovaciones() {
        this.renovacionesUsadas++;
    }

    public void setFechaDevolucionEsperada(LocalDateTime nuevaFecha) {
        this.fechaDevolucionEsperada = nuevaFecha;
    }
}