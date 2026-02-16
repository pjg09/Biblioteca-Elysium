package com.biblioteca.dominio.entidades;

import java.time.LocalDateTime;
import java.util.UUID;

import com.biblioteca.dominio.enumeraciones.EstadoMulta;

public abstract class Multa {
    protected String id;
    protected String idPrestamo;
    protected String idUsuario;
    protected LocalDateTime fechaGeneracion;
    protected LocalDateTime fechaPago;
    protected EstadoMulta estado;
    protected String motivo;
    
    public Multa(String idPrestamo, String idUsuario, String motivo) {
        this.id = UUID.randomUUID().toString();
        this.idPrestamo = idPrestamo;
        this.idUsuario = idUsuario;
        this.fechaGeneracion = LocalDateTime.now();
        this.estado = EstadoMulta.PENDIENTE;
        this.motivo = motivo;
    }
    
    public String getId() {
        return id;
    }
    
    public String getIdPrestamo() {
        return idPrestamo;
    }
    
    public String getIdUsuario() {
        return idUsuario;
    }
    
    public LocalDateTime getFechaGeneracion() {
        return fechaGeneracion;
    }
    
    public LocalDateTime getFechaPago() {
        return fechaPago;
    }
    
    public void setFechaPago(LocalDateTime fecha) {
        this.fechaPago = fecha;
        this.estado = EstadoMulta.PAGADA;
    }
    
    public EstadoMulta getEstado() {
        return estado;
    }
    
    public String getMotivo() {
        return motivo;
    }
    
    public boolean esPendiente() {
        return estado == EstadoMulta.PENDIENTE;
    }
    
    public abstract double calcularMontoTotal();
}