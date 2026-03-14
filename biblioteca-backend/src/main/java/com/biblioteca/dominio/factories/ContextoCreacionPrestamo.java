package com.biblioteca.dominio.factories;

import java.time.LocalDateTime;

import com.biblioteca.dominio.entidades.Material;
import com.biblioteca.dominio.entidades.Usuario;

public class ContextoCreacionPrestamo {
    private final Usuario usuario;
    private final Material material;
    private final LocalDateTime fechaDevolucion;
    private final String tipo;
    
    // Específicos
    private final String ubicacionBiblioteca;
    private final String bibliotecaOrigen;
    private final String bibliotecaDestino;
    private final double costoTransferencia;

    private ContextoCreacionPrestamo(Builder builder) {
        this.usuario = builder.usuario;
        this.material = builder.material;
        this.fechaDevolucion = builder.fechaDevolucion;
        this.tipo = builder.tipo;
        this.ubicacionBiblioteca = builder.ubicacionBiblioteca;
        this.bibliotecaOrigen = builder.bibliotecaOrigen;
        this.bibliotecaDestino = builder.bibliotecaDestino;
        this.costoTransferencia = builder.costoTransferencia;
    }

    public Usuario getUsuario() { return usuario; }
    public Material getMaterial() { return material; }
    public LocalDateTime getFechaDevolucion() { return fechaDevolucion; }
    public String getTipo() { return tipo; }
    public String getUbicacionBiblioteca() { return ubicacionBiblioteca; }
    public String getBibliotecaOrigen() { return bibliotecaOrigen; }
    public String getBibliotecaDestino() { return bibliotecaDestino; }
    public double getCostoTransferencia() { return costoTransferencia; }

    public static class Builder {
        private Usuario usuario;
        private Material material;
        private LocalDateTime fechaDevolucion;
        private String tipo = "normal";
        private String ubicacionBiblioteca;
        private String bibliotecaOrigen;
        private String bibliotecaDestino;
        private double costoTransferencia;

        public Builder conUsuario(Usuario usuario) {
            this.usuario = usuario;
            return this;
        }

        public Builder conMaterial(Material material) {
            this.material = material;
            return this;
        }

        public Builder conFechaDevolucion(LocalDateTime fecha) {
            this.fechaDevolucion = fecha;
            return this;
        }

        public Builder tipoNormal(String ubicacion) {
            this.tipo = "normal";
            this.ubicacionBiblioteca = ubicacion;
            return this;
        }

        public Builder tipoInterbibliotecario(String origen, String destino, double costo) {
            this.tipo = "interbibliotecario";
            this.bibliotecaOrigen = origen;
            this.bibliotecaDestino = destino;
            this.costoTransferencia = costo;
            return this;
        }

        public ContextoCreacionPrestamo build() {
            if (usuario == null) throw new IllegalStateException("Usuario requerido");
            if (material == null) throw new IllegalStateException("Material requerido");
            if (fechaDevolucion == null) throw new IllegalStateException("Fecha devolucion requerida");
            return new ContextoCreacionPrestamo(this);
        }
    }
}
