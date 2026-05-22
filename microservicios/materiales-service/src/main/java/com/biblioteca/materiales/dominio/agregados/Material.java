package com.biblioteca.materiales.dominio.agregados;

public class Material {

    private String id;
    private String titulo;
    private String autor;
    private String tipo;
    private String estado;
    private double precio;

    public Material() {}

    public Material(String id, String titulo, String autor, String tipo, String estado, double precio) {
        this.id = id;
        this.titulo = titulo;
        this.autor = autor;
        this.tipo = tipo;
        this.estado = estado;
        this.precio = precio;
    }

    public void marcarComoPrestado() {
        this.estado = "PRESTADO";
    }

    public void marcarComoDisponible() {
        this.estado = "DISPONIBLE";
    }

    public void marcarComoReservado() {
        this.estado = "RESERVADO";
    }

    public void marcarComoEnReparacion(String motivo) {
        this.estado = "EN_REPARACION";
    }

    public boolean isDisponible() {
        return "DISPONIBLE".equals(this.estado);
    }

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
}
