package com.biblioteca.dominio.entidades;

import java.time.LocalDateTime;

import com.biblioteca.dominio.enumeraciones.EstadoUsuario;
import com.biblioteca.dominio.enumeraciones.TipoUsuario;

public abstract class Usuario {
    protected String id;
    protected String nombre;
    protected String email;
    protected TipoUsuario tipo;
    protected EstadoUsuario estado;
    protected LocalDateTime fechaRegistro;
    
    public Usuario(String id, String nombre, String email, TipoUsuario tipo) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.tipo = tipo;
        this.estado = EstadoUsuario.ACTIVO;
        this.fechaRegistro = LocalDateTime.now();
    }
    
    public String getId() {
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
    
    public void setEstado(EstadoUsuario nuevoEstado) {
        this.estado = nuevoEstado;
    }
    
    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }
    
    @Override
    public String toString() {
        return "Usuario{" +
                "id='" + id + '\'' +
                ", nombre='" + nombre + '\'' +
                ", tipo=" + tipo +
                ", estado=" + estado +
                '}';
    }
}