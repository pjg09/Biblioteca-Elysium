package com.biblioteca.circulacion.dominio.reglas;

import com.biblioteca.circulacion.dominio.objetosvalor.ContextoValidacionPrestamo;
import com.biblioteca.commons.patrones.IReglaValidacion;

/**
 * Regla: El usuario no debe tener deuda pendiente de multas.
 * 
 * Validación: deudaPendiente == 0
 * 
 * Propósito: Evitar que usuarios con deuda contraigan más préstamos
 * hasta que hayan saldado sus multas.
 */
public class ReglaUsuarioNoMoroso implements IReglaValidacion {
    
    private String mensajeError;
    
    @Override
    public boolean validar(Object contexto) {
        if (!(contexto instanceof ContextoValidacionPrestamo)) {
            this.mensajeError = "Contexto inválido para validación de usuario moroso";
            return false;
        }
        
        ContextoValidacionPrestamo ctx = (ContextoValidacionPrestamo) contexto;
        
        if (ctx.getDeudaPendiente() > 0) {
            this.mensajeError = "El usuario " + ctx.getIdUsuario() + " tiene deuda pendiente de " + 
                    ctx.getDeudaPendiente() + ". No puede prestar hasta saldarlo.";
            return false;
        }
        
        return true;
    }
    
    @Override
    public String obtenerMensajeError() {
        return mensajeError != null ? mensajeError : "Usuario tiene deuda pendiente";
    }
    
    @Override
    public String getNombre() {
        return "USUARIO_NO_MOROSO";
    }
}
