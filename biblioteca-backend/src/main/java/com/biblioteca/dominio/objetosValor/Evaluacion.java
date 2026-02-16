package com.biblioteca.dominio.objetosvalor;

import java.util.ArrayList;
import java.util.List;

public class Evaluacion {
    private boolean materialUsable;
    private List<Dano> danosEncontrados;
    
    public Evaluacion(boolean materialUsable, List<Dano> danosEncontrados) {
        this.materialUsable = materialUsable;
        this.danosEncontrados = danosEncontrados != null ? danosEncontrados : new ArrayList<>();
    }
    
    public boolean esUsable() {
        return materialUsable;
    }
    
    public List<Dano> getDanos() {
        return new ArrayList<>(danosEncontrados);
    }
    
    public boolean tieneDanos() {
        return !danosEncontrados.isEmpty();
    }
    
    @Override
    public String toString() {
        if (!tieneDanos()) {
            return "Evaluación: Material en buen estado";
        }
        return String.format("Evaluación: %s | Daños encontrados: %d", 
            materialUsable ? "Usable" : "NO usable", 
            danosEncontrados.size());
    }
}