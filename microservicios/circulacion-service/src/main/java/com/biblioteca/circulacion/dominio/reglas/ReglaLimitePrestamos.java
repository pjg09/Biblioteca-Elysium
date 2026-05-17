package com.biblioteca.circulacion.dominio.reglas;

import com.biblioteca.commons.patrones.IReglaValidacion;

/**
 * Regla: El usuario no debe haber alcanzado su límite máximo de préstamos simultáneos.
 * 
 * Validación: prestamosActivos < limiteMaximoPrestamos
 * 
 * El límite varía según el tipo de usuario:
 * - ESTUDIANTE: 3 préstamos
 * - PROFESOR: 5 préstamos
 * - INVESTIGADOR: 10 préstamos
 * - PÚBLICO_GENERAL: 2 préstamos
 */
public class ReglaLimitePrestamos implements IReglaValidacion {
    
    private String mensajeError;
    
    @Override
    public boolean validar(Object contexto) {
        if (!(contexto instanceof ContextoValidacionPrestamo)) {
            this.mensajeError = "Contexto inválido para validación de límite de préstamos";
            return false;
        }
        
        ContextoValidacionPrestamo ctx = (ContextoValidacionPrestamo) contexto;
        
        if (ctx.getPrestamosActivos() >= ctx.getLimiteMaximoPrestamos()) {
            this.mensajeError = "El usuario " + ctx.getIdUsuario() + " ha alcanzado su límite de " + 
                    ctx.getLimiteMaximoPrestamos() + " préstamos activos. Actualmente tiene " + 
                    ctx.getPrestamosActivos();
            return false;
        }
        
        return true;
    }
    
    @Override
    public String obtenerMensajeError() {
        return mensajeError != null ? mensajeError : "Usuario excede límite de préstamos";
    }
    
    @Override
    public String getNombre() {
        return "LIMITE_PRESTAMOS";
    }
}
