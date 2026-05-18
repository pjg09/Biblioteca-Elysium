package com.biblioteca.multas.dominio.estados;

import java.time.LocalDateTime;
import com.biblioteca.commons.excepciones.OperacionNoPermitidaEnEstadoMultaException;

/**
 * Interfaz que define el contrato para todos los estados de una Multa.
 * Implementa el patrón State para gestionar el ciclo de vida de las multas.
 * 
 * Los estados de una multa son:
 * - GENERADA (PENDIENTE): Multa creada, aguardando pago
 * - PAGADA: Multa pagada por el usuario (terminal)
 * - CONDONADA: Multa perdonada por el sistema (terminal)
 */
public interface IEstadoMulta {
    
    /**
     * Registra el pago de la multa.
     * Transición válida: GENERADA → PAGADA
     */
    void pagar(LocalDateTime fechaPago, MultaContexto contexto)
            throws OperacionNoPermitidaEnEstadoMultaException;
    
    /**
     * Condona la multa (perdón por el sistema).
     * Transición válida: GENERADA → CONDONADA
     */
    void condonar(MultaContexto contexto)
            throws OperacionNoPermitidaEnEstadoMultaException;
    
    /**
     * Devuelve el nombre/identificador del estado.
     */
    String nombreEstado();
    
    /**
     * Indica si la multa está pendiente de resolución.
     */
    boolean esPendiente();
}
