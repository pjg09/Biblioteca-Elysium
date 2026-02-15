package com.biblioteca.dominio.entidades;

import java.time.LocalDateTime;
import java.util.UUID;

import com.biblioteca.dominio.enumeraciones.EstadoTransaccion;

public abstract class Transaccion {
    protected String id;
    protected String idUsuario;
    protected String idMaterial;
    protected LocalDateTime fechaCreacion;
    protected EstadoTransaccion estado;
    
    public Transaccion(String idUsuario, String idMaterial) {
        this.id = UUID.randomUUID().toString();
        this.idUsuario = idUsuario;
        this.idMaterial = idMaterial;
        this.fechaCreacion = LocalDateTime.now();
        this.estado = EstadoTransaccion.ACTIVA;
    }
    
    public String getId() {
        return id;
    }
    
    public String getIdUsuario() {
        return idUsuario;
    }
    
    public String getIdMaterial() {
        return idMaterial;
    }
    
    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }
    
    public EstadoTransaccion getEstado() {
        return estado;
    }
    
    public void setEstado(EstadoTransaccion nuevoEstado) {
        this.estado = nuevoEstado;
    }
}