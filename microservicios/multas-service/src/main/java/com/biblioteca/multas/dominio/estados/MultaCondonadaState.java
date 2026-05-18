package com.biblioteca.multas.dominio.estados;

import java.time.LocalDateTime;
import com.biblioteca.commons.excepciones.OperacionNoPermitidaEnEstadoMultaException;

/**
 * Estado CONDONADA de la Multa.
 * En este estado, la multa fue condonada por el sistema. Es un estado terminal.
 */
public class MultaCondonadaState implements IEstadoMulta {
    
    @Override
    public void pagar(LocalDateTime fechaPago, MultaContexto contexto)
            throws OperacionNoPermitidaEnEstadoMultaException {
        
        throw new OperacionNoPermitidaEnEstadoMultaException(
                "pagar",
                "[CONDONADA] Una multa condonada no puede ser pagada"
        );
    }
    
    @Override
    public void condonar(MultaContexto contexto)
            throws OperacionNoPermitidaEnEstadoMultaException {
        
        throw new OperacionNoPermitidaEnEstadoMultaException(
                "condonar",
                "[CONDONADA] Una multa condonada es un estado terminal"
        );
    }
    
    @Override
    public String nombreEstado() {
        return "CONDONADA";
    }
    
    @Override
    public boolean esPendiente() {
        return false;
    }
}
