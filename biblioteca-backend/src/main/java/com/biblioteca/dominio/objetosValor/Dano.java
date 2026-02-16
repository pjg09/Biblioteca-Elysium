package com.biblioteca.dominio.objetosValor;

import com.biblioteca.dominio.enumeraciones.NivelGravedad;
import com.biblioteca.dominio.enumeraciones.TipoDano;

public class Dano {
    private String descripcion;
    private NivelGravedad gravedad;
    private TipoDano tipo;
    
    public Dano(String descripcion, NivelGravedad gravedad, TipoDano tipo) {
        this.descripcion = descripcion;
        this.gravedad = gravedad;
        this.tipo = tipo;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public NivelGravedad getGravedad() {
        return gravedad;
    }
    
    public TipoDano getTipo() {
        return tipo;
    }
    
    @Override
    public String toString() {
        return String.format("Daño: %s | Tipo: %s | Gravedad: %s", 
            descripcion, tipo, gravedad);
    }
}