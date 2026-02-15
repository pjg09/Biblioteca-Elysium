package com.biblioteca.dominio.objetosvalor;

import java.util.ArrayList;
import java.util.List;

public class Evaluacion {
    private boolean materialUsable;
    private List<Dano> danosEncontrados;
    private double costoReparacion;
    
    public Evaluacion(boolean materialUsable, List<Dano> danosEncontrados) {
        this.materialUsable = materialUsable;
        this.danosEncontrados = danosEncontrados != null ? danosEncontrados : new ArrayList<>();
        this.costoReparacion = this.danosEncontrados.stream()
                .mapToDouble(Dano::getCostoReparacion)
                .sum();
    }
    
    public boolean esUsable() {
        return materialUsable;
    }
    
    public List<Dano> getDanos() {
        return new ArrayList<>(danosEncontrados);
    }
    
    public double getCostoReparacion() {
        return costoReparacion;
    }
    
    @Override
    public String toString() {
        return "Evaluación: " + (materialUsable ? "Material usable" : "Material NO usable") +
                ", Daños: " + danosEncontrados.size() + ", Costo reparación: $" + costoReparacion;
    }
}