package com.biblioteca.dominio.entidades;

import java.time.LocalDateTime;

import com.biblioteca.dominio.objetosvalor.IdMaterial;
import com.biblioteca.dominio.objetosvalor.IdUsuario;

public abstract class Reserva extends Transaccion {
    protected LocalDateTime fechaReserva;
    protected LocalDateTime fechaNotificacion;
    protected LocalDateTime fechaExpiracion;
    protected int posicionCola;
    
    public Reserva(String id, IdUsuario idUsuario, IdMaterial idMaterial) {
        super(id, idUsuario, idMaterial);
        this.fechaReserva = LocalDateTime.now();
        this.fechaExpiracion = fechaReserva.plusDays(3); // Expira en 3 días
    }
    
    public LocalDateTime getFechaReserva() {
        return fechaReserva;
    }
    
    public LocalDateTime getFechaNotificacion() {
        return fechaNotificacion;
    }
    
    public void setFechaNotificacion(LocalDateTime fecha) {
        this.fechaNotificacion = fecha;
    }
    
    public LocalDateTime getFechaExpiracion() {
        return fechaExpiracion;
    }
    
    public int getPosicionCola() {
        return posicionCola;
    }
    
    public void setPosicionCola(int posicion) {
        this.posicionCola = posicion;
    }
}