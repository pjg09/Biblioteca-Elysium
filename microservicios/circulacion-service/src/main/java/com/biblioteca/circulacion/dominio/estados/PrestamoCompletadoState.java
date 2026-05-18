package com.biblioteca.circulacion.dominio.estados;

import java.time.LocalDateTime;
import com.biblioteca.commons.excepciones.OperacionNoPermitidaEnEstadoException;

/**
 * Estado COMPLETADO del Préstamo.
 * En este estado, el préstamo ya fue devuelto.
 * - NO puede ser renovado
 * - NO puede ser devuelto nuevamente
 * - NO puede ser cancelado
 * 
 * Es un estado terminal.
 */
public class PrestamoCompletadoState implements IEstadoPrestamo {
    
    @Override
    public void renovar(LocalDateTime nuevaFecha, int maxRenovaciones, PrestamoContexto contexto)
            throws OperacionNoPermitidaEnEstadoException {
        throw new OperacionNoPermitidaEnEstadoException(
                "renovar",
                "[COMPLETADO] Un préstamo completado no puede ser renovado"
        );
    }
    
    @Override
    public void devolver(LocalDateTime fechaDevolucionReal, PrestamoContexto contexto)
            throws OperacionNoPermitidaEnEstadoException {
        throw new OperacionNoPermitidaEnEstadoException(
                "devolver",
                "[COMPLETADO] Un préstamo ya completado no puede ser devuelto nuevamente"
        );
    }
    
    @Override
    public void cancelar(PrestamoContexto contexto)
            throws OperacionNoPermitidaEnEstadoException {
        throw new OperacionNoPermitidaEnEstadoException(
                "cancelar",
                "[COMPLETADO] Un préstamo completado no puede ser cancelado"
        );
    }
    
    @Override
    public String nombreEstado() {
        return "COMPLETADO";
    }
    
    @Override
    public boolean puedeRenovarse() {
        return false;
    }
    
    @Override
    public boolean puedeDevolvirse() {
        return false;
    }
    
    @Override
    public boolean puedeCancelarse() {
        return false;
    }
}
