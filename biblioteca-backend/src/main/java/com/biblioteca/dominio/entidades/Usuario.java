package com.biblioteca.dominio.entidades;

import java.time.LocalDateTime;

import com.biblioteca.dominio.enumeraciones.EstadoUsuario;
import com.biblioteca.dominio.enumeraciones.TipoUsuario;
import com.biblioteca.dominio.objetosvalor.IdUsuario;
import java.util.UUID;

public abstract class Usuario {
    protected IdUsuario id;
    protected String nombre;
    protected String email;
    protected TipoUsuario tipo;
    protected EstadoUsuario estado;
    protected LocalDateTime fechaRegistro;
    
    protected Usuario(IdUsuario id, String nombre, String email, TipoUsuario tipo) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede ser nulo o vacío");
        }
        if (email == null || !email.matches(".+@.+\\..+")) {
            throw new IllegalArgumentException("Formato de email inválido");
        }
        
        this.id = id != null ? id : new IdUsuario("USR-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase());
        this.nombre = nombre;
        this.email = email;
        this.tipo = tipo;
        this.estado = EstadoUsuario.ACTIVO;
        this.fechaRegistro = LocalDateTime.now();
    }
    
    public IdUsuario getId() {
        return id;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public String getEmail() {
        return email;
    }
    
    public TipoUsuario getTipo() {
        return tipo;
    }
    
    public EstadoUsuario getEstado() {
        return estado;
    }
    
    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }
    
    // Métodos de comportamiento de dominio
    public void bloquearPorMulta() {
        this.estado = EstadoUsuario.BLOQUEADO_MULTA;
    }
    
    public void bloquearPorPerdida() {
        this.estado = EstadoUsuario.BLOQUEADO_PERDIDA;
    }
    
    public void suspender() {
        this.estado = EstadoUsuario.SUSPENDIDO;
    }
    
    public void reactivar() {
        this.estado = EstadoUsuario.ACTIVO;
    }
    
    public boolean puedeRealizarTransacciones() {
        return this.estado == EstadoUsuario.ACTIVO;
    }
    
    @Override
    public String toString() {
        return "Usuario{" +
                "id='" + id.getValor() + '\'' +
                ", nombre='" + nombre + '\'' +
                ", tipo=" + tipo +
                ", estado=" + estado +
                '}';
    }
}