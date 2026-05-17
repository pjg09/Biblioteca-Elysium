package com.biblioteca.multas.dominio.estados;

import java.time.LocalDateTime;

/**
 * Estado PAGADA de la Multa.
 * En este estado, la multa fue pagada por el usuario. Es un estado terminal.
 */
public class MultaPagadaState implements IEstadoMulta {
    
    @Override
    public void pagar(LocalDateTime fechaPago, MultaContexto contexto)
            throws OperacionNoPermitidaEnEstadoMultaException {
        
        throw new OperacionNoPermitidaEnEstadoMultaException(
                "pagar",
                nombreEstado(),
                "Una multa ya pagada no puede ser pagada nuevamente"
        );
    }
    
    @Override
    public void condonar(MultaContexto contexto)
            throws OperacionNoPermitidaEnEstadoMultaException {
        
        throw new OperacionNoPermitidaEnEstadoMultaException(
                "condonar",
                nombreEstado(),
                "Una multa ya pagada no puede ser condonada"
        );
    }
    
    @Override
    public String nombreEstado() {
        return "PAGADA";
    }
    
    @Override
    public boolean esPendiente() {
        return false;
    }
}
