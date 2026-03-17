package com.biblioteca.dominio.entidades;

import java.time.LocalDateTime;

import com.biblioteca.dominio.estados.IEstadoPrestamo;
import com.biblioteca.dominio.estados.PrestamoActivoState;
import com.biblioteca.dominio.objetosvalor.Resultado;
import com.biblioteca.dominio.objetosvalor.ResultadoValidacion;

import com.biblioteca.dominio.objetosvalor.IdUsuario;
import com.biblioteca.dominio.objetosvalor.IdMaterial;
import com.biblioteca.dominio.objetosvalor.IdTransaccion;

public abstract class Prestamo extends Transaccion {
    protected LocalDateTime fechaPrestamo;
    protected LocalDateTime fechaDevolucionEsperada;
    protected LocalDateTime fechaDevolucionReal;
    protected int renovacionesUsadas;
    private IEstadoPrestamo estadoActual;
    
    // El Builder usará este constructor protegido (o público según necesidad)
    public Prestamo(IdTransaccion id, IdUsuario idUsuario, IdMaterial idMaterial, LocalDateTime fechaDevolucionEsperada) {
        super(id, idUsuario, idMaterial);
        
        if (fechaDevolucionEsperada == null) {
            throw new IllegalArgumentException("Fecha de devolución no puede ser nula");
        }
        if (fechaDevolucionEsperada.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Fecha de devolución no puede estar en el pasado");
        }
        
        this.fechaPrestamo = LocalDateTime.now();
        this.fechaDevolucionEsperada = fechaDevolucionEsperada;
        this.renovacionesUsadas = 0;
        this.estadoActual = new PrestamoActivoState();
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

    public void cambiarEstado(IEstadoPrestamo nuevoEstado) {
        this.estadoActual = nuevoEstado;
    }
    
    public ResultadoValidacion puedeRenovarse() {
        return estadoActual.puedeRenovarse(this);
    }
    
    public Resultado devolver() {
        return estadoActual.devolver(this);
    }
}