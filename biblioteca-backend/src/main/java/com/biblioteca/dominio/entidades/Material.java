package com.biblioteca.dominio.entidades;

import java.time.LocalDateTime;

import com.biblioteca.dominio.enumeraciones.EstadoMaterial;
import com.biblioteca.dominio.enumeraciones.TipoMaterial;

public abstract class Material {
    protected String id;
    protected String titulo;
    protected String autor;
    protected TipoMaterial tipo;
    protected EstadoMaterial estado;
    protected LocalDateTime fechaAdquisicion;
    
    public Material(String id, String titulo, String autor, TipoMaterial tipo) {
        this.id = id;
        this.titulo = titulo;
        this.autor = autor;
        this.tipo = tipo;
        this.estado = EstadoMaterial.DISPONIBLE;
        this.fechaAdquisicion = LocalDateTime.now();
    }
    
    public String getId() {
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
    
    public void setEstado(EstadoMaterial nuevoEstado) {
        this.estado = nuevoEstado;
    }
    
    public LocalDateTime getFechaAdquisicion() {
        return fechaAdquisicion;
    }
    
    @Override
    public String toString() {
        return "Material{" +
                "id='" + id + '\'' +
                ", titulo='" + titulo + '\'' +
                ", tipo=" + tipo +
                ", estado=" + estado +
                '}';
    }
}