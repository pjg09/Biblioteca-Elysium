package com.biblioteca.reservas.dominio.estados;

import java.time.LocalDateTime;

/**
 * Interfaz que define el contrato para todos los estados de una Reserva.
 * Implementa el patrón State encapsulando el comportamiento específico de cada estado.
 * 
 * Los estados de una reserva son:
 * - EN_ESPERA: Esperando turno en la cola
 * - NOTIFICADA: Material disponible, usuario notificado (24 horas para recoger)
 * - COMPLETADA: Usuario recogió el material
 * - CANCELADA: Reserva cancelada (terminal)
 * - EXPIRADA: Tiempo de notificación expiró sin recoger (terminal)
 */
public interface IEstadoReserva {
    
    /**
     * Notifica al usuario que el material está disponible.
     * Transición válida: EN_ESPERA → NOTIFICADA
     */
    void notificar(LocalDateTime ahora, ReservaContexto contexto)
            throws OperacionNoPermitidaEnEstadoReservaException;
    
    /**
     * Cancela la reserva por solicitud del usuario o del sistema.
     * Transición válida: EN_ESPERA → CANCELADA, NOTIFICADA → CANCELADA
     */
    void cancelar(ReservaContexto contexto)
            throws OperacionNoPermitidaEnEstadoReservaException;
    
    /**
     * Expira la reserva cuando el usuario no recogió en 24 horas.
     * Transición válida: NOTIFICADA → EXPIRADA
     */
    void expirar(ReservaContexto contexto)
            throws OperacionNoPermitidaEnEstadoReservaException;
    
    /**
     * Completa la reserva cuando el usuario recoge el material.
     * Transición válida: NOTIFICADA → COMPLETADA
     */
    void completar(ReservaContexto contexto)
            throws OperacionNoPermitidaEnEstadoReservaException;
    
    /**
     * Devuelve el nombre/identificador del estado.
     */
    String nombreEstado();
    
    /**
     * Indica si la reserva es activa (aún pendiente de resolución).
     */
    boolean esActiva();
}
