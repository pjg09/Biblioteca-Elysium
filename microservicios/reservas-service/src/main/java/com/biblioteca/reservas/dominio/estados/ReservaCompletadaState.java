package com.biblioteca.reservas.dominio.estados;

import com.biblioteca.commons.excepciones.OperacionNoPermitidaEnEstadoReservaException;
import java.time.LocalDateTime;

/**
 * Estado COMPLETADA de la Reserva.
 * En este estado, el usuario recogió el material. Es un estado terminal.
 */
public class ReservaCompletadaState implements IEstadoReserva {
    
    @Override
    public void notificar(LocalDateTime ahora, ReservaContexto contexto)
            throws OperacionNoPermitidaEnEstadoReservaException {
        throw new OperacionNoPermitidaEnEstadoReservaException(
                "notificar",
                "[COMPLETADA] Una reserva completada no puede ser notificada"
        );
    }
    
    @Override
    public void cancelar(ReservaContexto contexto)
            throws OperacionNoPermitidaEnEstadoReservaException {
        throw new OperacionNoPermitidaEnEstadoReservaException(
                "cancelar",
                "[COMPLETADA] Una reserva completada no puede ser cancelada"
        );
    }
    
    @Override
    public void expirar(ReservaContexto contexto)
            throws OperacionNoPermitidaEnEstadoReservaException {
        throw new OperacionNoPermitidaEnEstadoReservaException(
                "expirar",
                "[COMPLETADA] Una reserva completada no puede expirar"
        );
    }
    
    @Override
    public void completar(ReservaContexto contexto)
            throws OperacionNoPermitidaEnEstadoReservaException {
        throw new OperacionNoPermitidaEnEstadoReservaException(
                "completar",
                "[COMPLETADA] Una reserva ya completada no puede ser completada nuevamente"
        );
    }
    
    @Override
    public String nombreEstado() {
        return "COMPLETADA";
    }
    
    @Override
    public boolean esActiva() {
        return false;
    }
}
