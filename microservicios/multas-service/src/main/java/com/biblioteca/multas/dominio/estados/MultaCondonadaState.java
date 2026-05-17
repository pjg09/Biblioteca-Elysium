package com.biblioteca.multas.dominio.estados;

import java.time.LocalDateTime;

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
                nombreEstado(),
                "Una multa condonada no puede ser pagada"
        );
    }
    
    @Override
    public void condonar(MultaContexto contexto)
            throws OperacionNoPermitidaEnEstadoMultaException {
        
        throw new OperacionNoPermitidaEnEstadoMultaException(
                "condonar",
                nombreEstado(),
                "Una multa ya condonada no puede ser condonada nuevamente"
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
