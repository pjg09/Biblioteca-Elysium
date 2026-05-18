package com.biblioteca.reservas.dominio.estados;

import com.biblioteca.commons.excepciones.OperacionNoPermitidaEnEstadoReservaException;
import java.time.LocalDateTime;

/**
 * Estado NOTIFICADA de la Reserva.
 * En este estado, la reserva ha sido notificada (material disponible) y el usuario tiene 24 horas para recogerlo.
 * 
 * Transiciones permitidas:
 * - completar() → COMPLETADA (usuario recoge el material)
 * - expirar() → EXPIRADA (pasaron 24 horas sin recoger)
 * - cancelar() → CANCELADA (usuario cancela después de notificado)
 */
public class ReservaNotificadaState implements IEstadoReserva {
    
    @Override
    public void notificar(LocalDateTime ahora, ReservaContexto contexto)
            throws OperacionNoPermitidaEnEstadoReservaException {
        
        throw new OperacionNoPermitidaEnEstadoReservaException(
                "notificar",
                "[NOTIFICADA] Una reserva ya notificada no puede ser notificada nuevamente"
        );
    }
    
    @Override
    public void cancelar(ReservaContexto contexto)
            throws OperacionNoPermitidaEnEstadoReservaException {
        
        contexto.setEstadoActual(new ReservaCanceladaState());
    }
    
    @Override
    public void expirar(ReservaContexto contexto)
            throws OperacionNoPermitidaEnEstadoReservaException {
        
        contexto.setEstadoActual(new ReservaExpiradaState());
    }
    
    @Override
    public void completar(ReservaContexto contexto)
            throws OperacionNoPermitidaEnEstadoReservaException {
        
        contexto.setEstadoActual(new ReservaCompletadaState());
    }
    
    @Override
    public String nombreEstado() {
        return "NOTIFICADA";
    }
    
    @Override
    public boolean esActiva() {
        return true;
    }
}
