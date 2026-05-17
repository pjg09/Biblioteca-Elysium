package com.biblioteca.circulacion.dominio.reglas;

import com.biblioteca.commons.patrones.IReglaValidacion;

/**
 * Regla: El material debe estar disponible para ser prestado.
 * 
 * Validación: material.estado == DISPONIBLE
 */
public class ReglaMaterialDisponible implements IReglaValidacion {
    
    private String mensajeError;
    
    @Override
    public boolean validar(Object contexto) {
        if (!(contexto instanceof ContextoValidacionPrestamo)) {
            this.mensajeError = "Contexto inválido para validación de material disponible";
            return false;
        }
        
        ContextoValidacionPrestamo ctx = (ContextoValidacionPrestamo) contexto;
        
        if (!ctx.isMaterialDisponible()) {
            this.mensajeError = "El material " + ctx.getIdMaterial() + " no está disponible. Estado: " + ctx.getEstadoMaterial();
            return false;
        }
        
        return true;
    }
    
    @Override
    public String obtenerMensajeError() {
        return mensajeError != null ? mensajeError : "Material no cumple validación de disponibilidad";
    }
    
    @Override
    public String getNombre() {
        return "MATERIAL_DISPONIBLE";
    }
}
