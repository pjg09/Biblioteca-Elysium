package com.biblioteca.reservas.dominio.estados;

import java.time.LocalDateTime;

/**
 * Estado CANCELADA de la Reserva.
 * En este estado, la reserva fue cancelada. Es un estado terminal.
 */
public class ReservaCanceladaState implements IEstadoReserva {
    
    @Override
    public void notificar(LocalDateTime ahora, ReservaContexto contexto)
            throws OperacionNoPermitidaEnEstadoReservaException {
        throw new OperacionNoPermitidaEnEstadoReservaException(
                "notificar",
                nombreEstado(),
                "Una reserva cancelada no puede ser notificada"
        );
    }
    
    @Override
    public void cancelar(ReservaContexto contexto)
            throws OperacionNoPermitidaEnEstadoReservaException {
        throw new OperacionNoPermitidaEnEstadoReservaException(
                "cancelar",
                nombreEstado(),
                "Una reserva ya cancelada no puede ser cancelada nuevamente"
        );
    }
    
    @Override
    public void expirar(ReservaContexto contexto)
            throws OperacionNoPermitidaEnEstadoReservaException {
        throw new OperacionNoPermitidaEnEstadoReservaException(
                "expirar",
                nombreEstado(),
                "Una reserva cancelada no puede expirar"
        );
    }
    
    @Override
    public void completar(ReservaContexto contexto)
            throws OperacionNoPermitidaEnEstadoReservaException {
        throw new OperacionNoPermitidaEnEstadoReservaException(
                "completar",
                nombreEstado(),
                "Una reserva cancelada no puede ser completada"
        );
    }
    
    @Override
    public String nombreEstado() {
        return "CANCELADA";
    }
    
    @Override
    public boolean esActiva() {
        return false;
    }
}
