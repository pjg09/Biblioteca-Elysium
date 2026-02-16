package com.biblioteca.dominio.objetosValor;

import java.time.LocalDateTime;

import com.biblioteca.dominio.enumeraciones.TipoMulta;

public class ContextoMulta {
    private String idPrestamo;
    private String idMaterial;
    private String idUsuario;
    private LocalDateTime fechaActual;
    private Evaluacion evaluacion;
    private TipoMulta tipoMulta;
    
    private ContextoMulta(Builder builder) {
        this.idPrestamo = builder.idPrestamo;
        this.idMaterial = builder.idMaterial;
        this.idUsuario = builder.idUsuario;
        this.fechaActual = builder.fechaActual;
        this.evaluacion = builder.evaluacion;
        this.tipoMulta = builder.tipoMulta;
    }
    
    // Getters
    public String getIdPrestamo() { return idPrestamo; }
    public String getIdMaterial() { return idMaterial; }
    public String getIdUsuario() { return idUsuario; }
    public LocalDateTime getFechaActual() { return fechaActual; }
    public Evaluacion getEvaluacion() { return evaluacion; }
    public TipoMulta getTipoMulta() { return tipoMulta; }
    
    // Builder Pattern
    public static class Builder {
        private String idPrestamo;
        private String idMaterial;
        private String idUsuario;
        private LocalDateTime fechaActual;
        private Evaluacion evaluacion;
        private TipoMulta tipoMulta;
        
        public Builder conPrestamo(String idPrestamo) {
            this.idPrestamo = idPrestamo;
            return this;
        }
        
        public Builder conMaterial(String idMaterial) {
            this.idMaterial = idMaterial;
            return this;
        }
        
        public Builder conUsuario(String idUsuario) {
            this.idUsuario = idUsuario;
            return this;
        }
        
        public Builder conFechaActual(LocalDateTime fecha) {
            this.fechaActual = fecha;
            return this;
        }
        
        public Builder conEvaluacion(Evaluacion evaluacion) {
            this.evaluacion = evaluacion;
            return this;
        }
        
        public Builder deTipo(TipoMulta tipo) {
            this.tipoMulta = tipo;
            return this;
        }
        
        public ContextoMulta build() {
            if (fechaActual == null) {
                this.fechaActual = LocalDateTime.now();
            }
            return new ContextoMulta(this);
        }
    }
}