package com.biblioteca.dominio.entidades;

import java.time.LocalDateTime;
import java.util.UUID;

import com.biblioteca.dominio.enumeraciones.EstadoTransaccion;

import com.biblioteca.dominio.objetosvalor.IdUsuario;
import com.biblioteca.dominio.objetosvalor.IdMaterial;
import com.biblioteca.dominio.objetosvalor.IdTransaccion;

public abstract class Transaccion {
    protected IdTransaccion id;
    protected IdUsuario idUsuario;
    protected IdMaterial idMaterial;
    protected LocalDateTime fechaCreacion;
    protected EstadoTransaccion estado;
    
    public Transaccion(IdTransaccion id, IdUsuario idUsuario, IdMaterial idMaterial) {
        if (idUsuario == null) {
            throw new IllegalArgumentException("ID de usuario no puede ser nulo");
        }
        if (idMaterial == null) {
            throw new IllegalArgumentException("ID de material no puede ser nulo");
        }
        this.id = id != null ? id : new IdTransaccion(UUID.randomUUID().toString());
        this.idUsuario = idUsuario;
        this.idMaterial = idMaterial;
        this.fechaCreacion = LocalDateTime.now();
        this.estado = EstadoTransaccion.ACTIVA;
    }
    
    public IdTransaccion getId() {
        return id;
    }
    
    public IdUsuario getIdUsuario() {
        return idUsuario;
    }
    
    public IdMaterial getIdMaterial() {
        return idMaterial;
    }
    
    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }
    
    public EstadoTransaccion getEstado() {
        return estado;
    }
    
    public void setEstado(EstadoTransaccion nuevoEstado) {
        if (nuevoEstado == null) {
            throw new IllegalArgumentException("Estado no puede ser nulo");
        }
        this.estado = nuevoEstado;
    }
}