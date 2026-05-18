package com.biblioteca.multas.aplicacion.facades;

import com.biblioteca.multas.infraestructura.persistencia.MultaEntity;
import com.biblioteca.multas.aplicacion.dto.DeudaPendienteDTO;
import com.biblioteca.commons.excepciones.OperacionNoPermitidaEnEstadoMultaException;
import java.util.List;
import java.util.Optional;

/**
 * Interfaz para la Facade de Multas.
 * Define el contrato para operaciones de multas en el sistema.
 * 
 * Aplicación de SOLID:
 * - Interface Segregation: interfaz específica solo para multas
 * - Dependency Inversion: los clientes dependen de esta interfaz, no de MultasFacade
 */
public interface IMultasFacade {

    /**
     * Obtiene una multa por su ID.
     */
    Optional<MultaEntity> obtenerPorId(String id);

    /**
     * Obtiene todas las multas de un usuario.
     */
    List<MultaEntity> obtenerPorUsuario(String idUsuario);

    /**
     * Consulta la deuda pendiente (multas en estado GENERADA) de un usuario.
     * Devuelve un DTO con el monto total, cantidad de multas y fecha de vencimiento más próxima.
     */
    DeudaPendienteDTO consultarDeudaPendiente(String idUsuario);

    /**
     * Registra el pago de una multa por su ID.
     * Transiciona de GENERADA → PAGADA.
     * 
     * @throws OperacionNoPermitidaEnEstadoMultaException si la multa no está en estado GENERADA
     */
    MultaEntity pagarMulta(String id) throws OperacionNoPermitidaEnEstadoMultaException;

    /**
     * Condona una multa por su ID.
     * Transiciona de GENERADA → CONDONADA.
     * 
     * @throws OperacionNoPermitidaEnEstadoMultaException si la multa no está en estado GENERADA
     */
    MultaEntity condonarMulta(String id) throws OperacionNoPermitidaEnEstadoMultaException;

    /**
     * Obtiene todas las multas del sistema.
     */
    List<MultaEntity> obtenerTodas();
}
