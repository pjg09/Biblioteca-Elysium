package com.biblioteca.dominio.entidades;

import java.time.LocalDateTime;

import com.biblioteca.dominio.enumeraciones.EstadoMaterial;
import com.biblioteca.dominio.enumeraciones.TipoMaterial;
import com.biblioteca.dominio.objetosvalor.IdMaterial;
import java.util.UUID;

public abstract class Material {
    protected IdMaterial id;
    protected String titulo;
    protected String autor;
    protected TipoMaterial tipo;
    protected EstadoMaterial estado;
    protected LocalDateTime fechaAdquisicion;
    protected double precio;
    
    // Constructor protegido - solo subclases pueden crear
    protected Material(IdMaterial id, String titulo, String autor, TipoMaterial tipo, double precio) {
        if (titulo == null || titulo.trim().isEmpty()) {
            throw new IllegalArgumentException("Título no puede ser nulo");
        }
        
        this.id = id != null ? id : new IdMaterial("MAT-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase());
        this.titulo = titulo;
        this.autor = autor;
        this.tipo = tipo;
        this.estado = EstadoMaterial.DISPONIBLE; // ESTADO INICIAL VÁLIDO
        this.fechaAdquisicion = LocalDateTime.now();
        this.precio = precio;
    }
    
    public IdMaterial getId() {
        return id;
    }
    
    public String getTitulo() {
        return titulo;
    }
    
    public String getAutor() {
        return autor;
    }
    
    public TipoMaterial getTipo() {
        return tipo;
    }
    
    public EstadoMaterial getEstado() {
        return estado;
    }
    
    public LocalDateTime getFechaAdquisicion() {
        return fechaAdquisicion;
    }
    
    public double getPrecio() {
        return precio;
    }
    
    public void setPrecio(double precio) {
        this.precio = precio;
    }

    // COMPORTAMIENTO, NO SOLO DATOS
    public void marcarComoPrestado() {
        if (this.estado != EstadoMaterial.DISPONIBLE) {
            throw new IllegalStateException(
                "No se puede prestar un material en estado: " + this.estado
            );
        }
        this.estado = EstadoMaterial.PRESTADO;
    }
    
    public void marcarComoReservado() {
        if (this.estado != EstadoMaterial.DISPONIBLE) {
            throw new IllegalStateException(
                "No se puede reservar un material en estado: " + this.estado
            );
        }
        this.estado = EstadoMaterial.RESERVADO;
    }
    
    public void marcarComoDisponible() {
        if (this.estado == EstadoMaterial.PERDIDO) {
            throw new IllegalStateException(
                "Material perdido no puede volver a disponible directamente"
            );
        }
        this.estado = EstadoMaterial.DISPONIBLE;
    }
    
    public void marcarComoEnReparacion(String motivo) {
        if (motivo == null || motivo.trim().isEmpty()) {
            throw new IllegalArgumentException("Debe especificar motivo de reparación");
        }
        this.estado = EstadoMaterial.EN_REPARACION;
    }
    
    public void marcarComoPerdido(String motivo) {
        if (motivo == null || motivo.trim().isEmpty()) {
            throw new IllegalArgumentException("Debe especificar motivo de pérdida");
        }
        this.estado = EstadoMaterial.PERDIDO;
    }
    
    public boolean estaDisponibleParaPrestamo() {
        return this.estado == EstadoMaterial.DISPONIBLE;
    }
    
    @Override
    public String toString() {
        return "Material{" +
                "id='" + id.getValor() + '\'' +
                ", titulo='" + titulo + '\'' +
                ", tipo=" + tipo +
                ", estado=" + estado +
                '}';
    }
}