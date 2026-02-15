package com.biblioteca.dominio.objetosvalor;

import com.biblioteca.dominio.enumeraciones.NivelGravedad;
import com.biblioteca.dominio.enumeraciones.TipoDano;

public class Dano {
    private String descripcion;
    private NivelGravedad gravedad;
    private double costoReparacion;
    private TipoDano tipo;
    
    public Dano(String descripcion, NivelGravedad gravedad, double costoReparacion, TipoDano tipo) {
        this.descripcion = descripcion;
        this.gravedad = gravedad;
        this.costoReparacion = costoReparacion;
        this.tipo = tipo;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public NivelGravedad getGravedad() {
        return gravedad;
    }
    
    public double getCostoReparacion() {
        return costoReparacion;
    }
    
    public TipoDano getTipo() {
        return tipo;
    }
    
    @Override
    public String toString() {
        return "Daño: " + descripcion + " (" + tipo + " - " + gravedad + ") Costo: $" + costoReparacion;
    }
}