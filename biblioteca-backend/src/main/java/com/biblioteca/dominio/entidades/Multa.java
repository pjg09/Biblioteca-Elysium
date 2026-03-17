package com.biblioteca.dominio.entidades;

import java.time.LocalDateTime;
import java.util.UUID;

import com.biblioteca.dominio.enumeraciones.EstadoMulta;
import com.biblioteca.dominio.objetosvalor.IdMulta;

public abstract class Multa {
    protected IdMulta id;
    protected String idPrestamo;
    protected String idUsuario;
    protected LocalDateTime fechaGeneracion;
    protected LocalDateTime fechaPago;
    protected EstadoMulta estado;
    protected String motivo;
    
    public Multa(String idPrestamo, String idUsuario, String motivo) {
        this.id = new IdMulta("MUL-" + UUID.randomUUID().toString().substring(0,8));
        this.idPrestamo = idPrestamo;
        this.idUsuario = idUsuario;
        this.fechaGeneracion = LocalDateTime.now();
        this.estado = EstadoMulta.PENDIENTE;
        this.motivo = motivo;
    }
    
    public IdMulta getId() {
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