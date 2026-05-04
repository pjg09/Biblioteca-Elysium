package com.biblioteca.dominio.objetosvalor;

import com.biblioteca.dominio.enumeraciones.TipoMulta;
import com.biblioteca.dominio.enumeraciones.TipoUsuario;

import java.time.LocalDateTime;

public class ContextoMulta {
    private final String idPrestamo;
    private final String idMaterial;
    private final String idUsuario;
    private final LocalDateTime fechaActual;
    private final Evaluacion evaluacion;
    private final TipoMulta tipoMulta;
    // Datos resueltos por el servicio de aplicación antes de invocar los calculadores
    private final int diasRetraso;
    private final TipoUsuario tipoUsuario;
    private final double valorMaterial;

    private ContextoMulta(Builder builder) {
        this.idPrestamo   = builder.idPrestamo;
        this.idMaterial   = builder.idMaterial;
        this.idUsuario    = builder.idUsuario;
        this.fechaActual  = builder.fechaActual;
        this.evaluacion   = builder.evaluacion;
        this.tipoMulta    = builder.tipoMulta;
        this.diasRetraso  = builder.diasRetraso;
        this.tipoUsuario  = builder.tipoUsuario;
        this.valorMaterial = builder.valorMaterial;
    }

    public String getIdPrestamo()       { return idPrestamo; }
    public String getIdMaterial()       { return idMaterial; }
    public String getIdUsuario()        { return idUsuario; }
    public LocalDateTime getFechaActual() { return fechaActual; }
    public Evaluacion getEvaluacion()   { return evaluacion; }
    public TipoMulta getTipoMulta()     { return tipoMulta; }
    public int getDiasRetraso()         { return diasRetraso; }
    public TipoUsuario getTipoUsuario() { return tipoUsuario; }
    public double getValorMaterial()    { return valorMaterial; }

    public static class Builder {
        private String idPrestamo;
        private String idMaterial;
        private String idUsuario;
        private LocalDateTime fechaActual;
        private Evaluacion evaluacion;
        private TipoMulta tipoMulta;
        private int diasRetraso;
        private TipoUsuario tipoUsuario;
        private double valorMaterial;

        public Builder conPrestamo(String idPrestamo)         { this.idPrestamo = idPrestamo; return this; }
        public Builder conMaterial(String idMaterial)         { this.idMaterial = idMaterial; return this; }
        public Builder conUsuario(String idUsuario)           { this.idUsuario = idUsuario; return this; }
        public Builder conFechaActual(LocalDateTime fecha)    { this.fechaActual = fecha; return this; }
        public Builder conEvaluacion(Evaluacion evaluacion)   { this.evaluacion = evaluacion; return this; }
        public Builder deTipo(TipoMulta tipo)                 { this.tipoMulta = tipo; return this; }
        public Builder conDiasRetraso(int dias)               { this.diasRetraso = dias; return this; }
        public Builder conTipoUsuario(TipoUsuario tipo)       { this.tipoUsuario = tipo; return this; }
        public Builder conValorMaterial(double valor)         { this.valorMaterial = valor; return this; }

        public ContextoMulta build() {
            if (fechaActual == null) this.fechaActual = LocalDateTime.now();
            return new ContextoMulta(this);
        }
    }
}
