package com.biblioteca.circulacion.dominio.reglas;

import com.biblioteca.commons.patrones.IReglaValidacion;

/**
 * Regla: El usuario debe estar activo para poder prestar materiales.
 * 
 * Validación: usuario.estado == ACTIVO
 */
public class ReglaUsuarioActivo implements IReglaValidacion {
    
    private String mensajeError;
    
    @Override
    public boolean validar(Object contexto) {
        if (!(contexto instanceof ContextoValidacionPrestamo)) {
            this.mensajeError = "Contexto inválido para validación de usuario activo";
            return false;
        }
        
        ContextoValidacionPrestamo ctx = (ContextoValidacionPrestamo) contexto;
        
        if (!ctx.isUsuarioActivo()) {
            this.mensajeError = "El usuario " + ctx.getIdUsuario() + " no está activo en el sistema";
            return false;
        }
        
        return true;
    }
    
    @Override
    public String obtenerMensajeError() {
        return mensajeError != null ? mensajeError : "Usuario no cumple validación de estado activo";
    }
    
    @Override
    public String getNombre() {
        return "USUARIO_ACTIVO";
    }
}
