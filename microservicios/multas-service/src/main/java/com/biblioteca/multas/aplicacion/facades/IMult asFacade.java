package com.biblioteca.multas.aplicacion.facades;

import com.biblioteca.multas.aplicacion.dto.DeudaPendienteDTO;
import com.biblioteca.multas.infraestructura.persistencia.MultaEntity;

import java.util.List;
import java.util.Optional;

/**
 * Facade para operaciones de gestión de multas.
 * 
 * Coordina operaciones de:
 * - Consulta de multas pendientes
 * - Consulta de deuda total
 * - Pago de multas
 * - Condonación de multas
 * - Generación de multas por retraso/pérdida/daño
 * 
 * Propósito:
 * - Proporcionar interfaz única para gestión de multas
 * - Encapsular cálculos de multas usando Strategy Pattern
 * - Aplicar transiciones de estado mediante State Pattern
 * - Publicar eventos de cambio de estado
 * 
 * Cumple con:
 * - Facade Pattern
 * - Strategy Pattern (CalculadorMultaContext)
 * - State Pattern (Multa.pagar(), Multa.condonar())
 * - Domain Events (publicación en RabbitMQ)
 */
public interface IMult asFacade {
    
    /**
     * Obtiene una multa por su ID.
     */
    Optional<MultaEntity> obtenerPorId(String id);
    
    /**
     * Lista todas las multas de un usuario.
     */
    List<MultaEntity> obtenerPorUsuario(String idUsuario);
    
    /**
     * Consulta la deuda pendiente total de un usuario.
     * 
     * Suma todas las multas en estado GENERADA y retorna:
     * - Total de deuda en pesos
     * - Cantidad de multas pendientes
     * 
     * Usado por circulacion-service para validar renovaciones.
     * 
     * @param idUsuario ID del usuario
     * @return DTO con deuda total y cantidad de multas
     */
    DeudaPendienteDTO consultarDeudaPendiente(String idUsuario);
    
    /**
     * Lista las multas asociadas a un préstamo.
     */
    List<MultaEntity> obtenerPorPrestamo(String idPrestamo);
    
    /**
     * Lista multas de un usuario filtradas por estado.
     */
    List<MultaEntity> obtenerPorUsuarioYEstado(String idUsuario, String estado);
    
    /**
     * Registra el pago de una multa.
     * 
     * Realiza:
     * 1. Búsqueda de la multa
     * 2. Validación: debe estar en estado GENERADA
     * 3. Cambio de estado a PAGADA mediante State Pattern
     * 4. Persistencia
     * 5. Publicación de evento "multa.pagada"
     * 
     * @param id ID de la multa a pagar
     * @return MultaEntity con estado actualizado
     * @throws IllegalArgumentException si multa no existe o no está GENERADA
     */
    MultaEntity pagarMulta(String id);
    
    /**
     * Condona una multa (perdón administrativo).
     * 
     * Realiza:
     * 1. Búsqueda de la multa
     * 2. Validación: debe estar en estado GENERADA
     * 3. Cambio de estado a CONDONADA mediante State Pattern
     * 4. Persistencia
     * 5. Publicación de evento "multa.condonada"
     * 
     * @param id ID de la multa a condonar
     * @return MultaEntity con estado actualizado
     * @throws IllegalArgumentException si multa no existe o no está GENERADA
     */
    MultaEntity condonarMulta(String id);
}
