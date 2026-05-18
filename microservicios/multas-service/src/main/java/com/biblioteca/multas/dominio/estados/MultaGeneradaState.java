package com.biblioteca.multas.dominio.estados;

import java.time.LocalDateTime;
import com.biblioteca.commons.excepciones.OperacionNoPermitidaEnEstadoMultaException;

/**
 * Estado GENERADA (PENDIENTE) de la Multa.
 * En este estado, la multa acaba de ser creada y está esperando pago o condonación.
 * 
 * Transiciones permitidas:
 * - pagar() → PAGADA (usuario paga la multa)
 * - condonar() → CONDONADA (sistema condona la multa)
 */
public class MultaGeneradaState implements IEstadoMulta {
    
    @Override
    public void pagar(LocalDateTime fechaPago, MultaContexto contexto)
            throws OperacionNoPermitidaEnEstadoMultaException {
        
        contexto.setFechaPago(fechaPago);
        contexto.setEstadoActual(new MultaPagadaState());
    }
    
    @Override
    public void condonar(MultaContexto contexto)
            throws OperacionNoPermitidaEnEstadoMultaException {
        
        contexto.setEstadoActual(new MultaCondonadaState());
    }
    
    @Override
    public String nombreEstado() {
        return "GENERADA";
    }
    
    @Override
    public boolean esPendiente() {
        return true;
    }
}
