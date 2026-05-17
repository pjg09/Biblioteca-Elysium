package com.biblioteca.reservas.dominio.estados;

import java.time.LocalDateTime;

/**
 * Estado EXPIRADA de la Reserva.
 * En este estado, el usuario no recogió el material en las 24 horas después de ser notificado.
 * Es un estado terminal.
 */
public class ReservaExpiradaState implements IEstadoReserva {
    
    @Override
    public void notificar(LocalDateTime ahora, ReservaContexto contexto)
            throws OperacionNoPermitidaEnEstadoReservaException {
        throw new OperacionNoPermitidaEnEstadoReservaException(
                "notificar",
                nombreEstado(),
                "Una reserva expirada no puede ser notificada"
        );
    }
    
    @Override
    public void cancelar(ReservaContexto contexto)
            throws OperacionNoPermitidaEnEstadoReservaException {
        throw new OperacionNoPermitidaEnEstadoReservaException(
                "cancelar",
                nombreEstado(),
                "Una reserva expirada no puede ser cancelada"
        );
    }
    
    @Override
    public void expirar(ReservaContexto contexto)
            throws OperacionNoPermitidaEnEstadoReservaException {
        throw new OperacionNoPermitidaEnEstadoReservaException(
                "expirar",
                nombreEstado(),
                "Una reserva ya expirada no puede expirar nuevamente"
        );
    }
    
    @Override
    public void completar(ReservaContexto contexto)
            throws OperacionNoPermitidaEnEstadoReservaException {
        throw new OperacionNoPermitidaEnEstadoReservaException(
                "completar",
                nombreEstado(),
                "Una reserva expirada no puede ser completada"
        );
    }
    
    @Override
    public String nombreEstado() {
        return "EXPIRADA";
    }
    
    @Override
    public boolean esActiva() {
        return false;
    }
}
