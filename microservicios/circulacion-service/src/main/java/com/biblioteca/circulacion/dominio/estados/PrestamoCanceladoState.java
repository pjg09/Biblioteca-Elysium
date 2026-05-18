package com.biblioteca.circulacion.dominio.estados;

import java.time.LocalDateTime;
import com.biblioteca.commons.excepciones.OperacionNoPermitidaEnEstadoException;

/**
 * Estado CANCELADO del Préstamo.
 * En este estado, el préstamo fue cancelado.
 * - NO puede ser renovado
 * - NO puede ser devuelto
 * - NO puede ser cancelado nuevamente
 * 
 * Es un estado terminal.
 */
public class PrestamoCanceladoState implements IEstadoPrestamo {
    
    @Override
    public void renovar(LocalDateTime nuevaFecha, int maxRenovaciones, PrestamoContexto contexto)
            throws OperacionNoPermitidaEnEstadoException {
        throw new OperacionNoPermitidaEnEstadoException(
                "renovar",
                "[CANCELADO] Un préstamo cancelado no puede ser renovado"
        );
    }
    
    @Override
    public void devolver(LocalDateTime fechaDevolucionReal, PrestamoContexto contexto)
            throws OperacionNoPermitidaEnEstadoException {
        throw new OperacionNoPermitidaEnEstadoException(
                "devolver",
                "[CANCELADO] Un préstamo cancelado no puede ser devuelto"
        );
    }
    
    @Override
    public void cancelar(PrestamoContexto contexto)
            throws OperacionNoPermitidaEnEstadoException {
        throw new OperacionNoPermitidaEnEstadoException(
                "cancelar",
                "[CANCELADO] Un préstamo ya cancelado no puede ser cancelado nuevamente"
        );
    }
    
    @Override
    public String nombreEstado() {
        return "CANCELADO";
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
