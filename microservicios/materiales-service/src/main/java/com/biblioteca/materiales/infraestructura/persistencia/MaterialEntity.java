package com.biblioteca.materiales.infraestructura.persistencia;

import com.biblioteca.materiales.dominio.Material;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "materiales")
public class MaterialEntity {

    @Id
    private String id;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false)
    private String autor;

    @Column(nullable = false)
    private String tipo;

    @Column(nullable = false)
    private String estado;

    @Column(nullable = false)
    private double precio;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    public MaterialEntity() {}

    // -------------------------------------------------------------------------
    // Fábrica estática desde dominio
    // -------------------------------------------------------------------------

    public static MaterialEntity fromDomain(Material m) {
        MaterialEntity entity = new MaterialEntity();
        entity.id = m.getId();
        entity.titulo = m.getTitulo();
        entity.autor = m.getAutor();
        entity.tipo = m.getTipo();
        entity.estado = m.getEstado();
        entity.precio = m.getPrecio();
        entity.fechaCreacion = LocalDateTime.now();
        return entity;
    }

    // -------------------------------------------------------------------------
    // Conversión a dominio
    // -------------------------------------------------------------------------

    public Material toDomain() {
        return new Material(id, titulo, autor, tipo, estado, precio);
    }

    // -------------------------------------------------------------------------
    // Getters y setters
    // -------------------------------------------------------------------------

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}
