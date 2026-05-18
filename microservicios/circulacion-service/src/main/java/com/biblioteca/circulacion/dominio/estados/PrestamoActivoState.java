package com.biblioteca.circulacion.dominio.estados;

import java.time.LocalDateTime;
import com.biblioteca.commons.excepciones.OperacionNoPermitidaEnEstadoException;

/**
 * Estado ACTIVO del Préstamo.
 * En este estado, el préstamo:
 * - Puede ser renovado (hasta el límite de renovaciones)
 * - Puede ser devuelto
 * - Puede ser cancelado
 */
public class PrestamoActivoState implements IEstadoPrestamo {
    
    @Override
    public void renovar(LocalDateTime nuevaFecha, int maxRenovaciones, PrestamoContexto contexto)
            throws OperacionNoPermitidaEnEstadoException {
        
        if (contexto.getRenovacionesUsadas() >= maxRenovaciones) {
            throw new OperacionNoPermitidaEnEstadoException(
                    "renovar",
                    "[ACTIVO] Se ha alcanzado el límite de renovaciones (" + maxRenovaciones + ")"
            );
        }
        
        contexto.setFechaDevolucionEsperada(nuevaFecha);
        contexto.setRenovacionesUsadas(contexto.getRenovacionesUsadas() + 1);
        // El estado sigue siendo ACTIVO
    }
    
    @Override
    public void devolver(LocalDateTime fechaDevolucionReal, PrestamoContexto contexto)
            throws OperacionNoPermitidaEnEstadoException {
        
        contexto.setFechaDevolucionReal(fechaDevolucionReal);
        contexto.setEstadoActual(new PrestamoCompletadoState());
    }
    
    @Override
    public void cancelar(PrestamoContexto contexto)
            throws OperacionNoPermitidaEnEstadoException {
        
        contexto.setEstadoActual(new PrestamoCanceladoState());
    }
    
    @Override
    public String nombreEstado() {
        return "ACTIVO";
    }
    
    @Override
    public boolean puedeRenovarse() {
        return true;
    }
    
    @Override
    public boolean puedeDevolvirse() {
        return true;
    }
    
    @Override
    public boolean puedeCancelarse() {
        return true;
    }
}
