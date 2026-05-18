package com.biblioteca.circulacion.dominio.estados;

import java.time.LocalDateTime;
import com.biblioteca.commons.excepciones.OperacionNoPermitidaEnEstadoException;

/**
 * Interfaz que define el contrato para todos los estados de un Préstamo.
 * Implementa el patrón State encapsulando el comportamiento específico de cada estado.
 * 
 * Beneficios:
 * - Elimina largas cadenas de if/else en la lógica de préstamos
 * - Cada estado es testeable independientemente
 * - Fácil agregar nuevos estados sin modificar código existente (OCP)
 */
public interface IEstadoPrestamo {
    
    /**
     * Intenta renovar el préstamo.
     * Solo los préstamos en estado ACTIVO pueden renovarse.
     */
    void renovar(LocalDateTime nuevaFecha, int maxRenovaciones, PrestamoContexto contexto)
            throws OperacionNoPermitidaEnEstadoException;
    
    /**
     * Intenta devolver el préstamo.
     * Solo los préstamos en estado ACTIVO pueden devolverse.
     */
    void devolver(LocalDateTime fechaDevolucionReal, PrestamoContexto contexto)
            throws OperacionNoPermitidaEnEstadoException;
    
    /**
     * Intenta cancelar el préstamo.
     * Solo ciertos estados pueden ser cancelados.
     */
    void cancelar(PrestamoContexto contexto)
            throws OperacionNoPermitidaEnEstadoException;
    
    /**
     * Devuelve el nombre/identificador del estado.
     */
    String nombreEstado();
    
    /**
     * Indica si el préstamo puede ser renovado en este estado.
     */
    boolean puedeRenovarse();
    
    /**
     * Indica si el préstamo puede ser devuelto en este estado.
     */
    boolean puedeDevolvirse();
    
    /**
     * Indica si el préstamo puede ser cancelado en este estado.
     */
    boolean puedeCancelarse();
}
