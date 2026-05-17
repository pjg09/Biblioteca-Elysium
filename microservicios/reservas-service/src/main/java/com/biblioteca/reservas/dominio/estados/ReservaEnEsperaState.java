package com.biblioteca.reservas.dominio.estados;

import java.time.LocalDateTime;

/**
 * Estado EN_ESPERA de la Reserva.
 * En este estado, la reserva está en la cola esperando que el material esté disponible.
 * 
 * Transiciones permitidas:
 * - notificar() → NOTIFICADA (cuando material está disponible)
 * - cancelar() → CANCELADA (usuario cancela la reserva)
 */
public class ReservaEnEsperaState implements IEstadoReserva {
    
    @Override
    public void notificar(LocalDateTime ahora, ReservaContexto contexto)
            throws OperacionNoPermitidaEnEstadoReservaException {
        
        if (contexto.getPosicionCola() != 1) {
            throw new OperacionNoPermitidaEnEstadoReservaException(
                    "notificar",
                    nombreEstado(),
                    "Solo se puede notificar la reserva en posición 1. Posición actual: " + contexto.getPosicionCola()
            );
        }
        
        contexto.setFechaNotificacion(ahora);
        contexto.setFechaExpiracion(ahora.plusHours(24));
        contexto.setEstadoActual(new ReservaNotificadaState());
    }
    
    @Override
    public void cancelar(ReservaContexto contexto)
            throws OperacionNoPermitidaEnEstadoReservaException {
        
        contexto.setEstadoActual(new ReservaCanceladaState());
    }
    
    @Override
    public void expirar(ReservaContexto contexto)
            throws OperacionNoPermitidaEnEstadoReservaException {
        
        throw new OperacionNoPermitidaEnEstadoReservaException(
                "expirar",
                nombreEstado(),
                "Una reserva en estado EN_ESPERA no puede expirar"
        );
    }
    
    @Override
    public void completar(ReservaContexto contexto)
            throws OperacionNoPermitidaEnEstadoReservaException {
        
        throw new OperacionNoPermitidaEnEstadoReservaException(
                "completar",
                nombreEstado(),
                "Una reserva en estado EN_ESPERA no puede ser completada"
        );
    }
    
    @Override
    public String nombreEstado() {
        return "EN_ESPERA";
    }
    
    @Override
    public boolean esActiva() {
        return true;
    }
}
