package com.biblioteca.reservas.aplicacion.facades;

import com.biblioteca.reservas.aplicacion.dto.CrearReservaRequest;
import com.biblioteca.reservas.infraestructura.persistencia.ReservaEntity;

import java.util.List;
import java.util.Optional;

/**
 * Facade para operaciones de gestión de reservas.
 * 
 * Coordina operaciones de:
 * - Creación de reservas en cola
 * - Cancelación de reservas
 * - Consulta de reservas
 * - Reorganización de cola
 * 
 * Propósito:
 * - Proporcionar una interfaz única para gestión de colas de espera
 * - Encapsular lógica de posicionamiento en cola
 * - Publicar eventos de cambio de estado
 * 
 * Cumple con:
 * - Facade Pattern (interfaz simplificada)
 * - Single Responsibility (cada operación bien delimitada)
 * - Dependency Inversion (depende de abstracciones)
 * - Domain Events (publicación en RabbitMQ)
 */
public interface IReservasFacade {
    
    /**
     * Crea una nueva reserva en la cola para un material.
     * 
     * Realiza:
     * 1. Cálculo de posición en cola (activas + 1)
     * 2. Validación usando ReservaBuilder
     * 3. Persistencia
     * 4. Publicación de evento "reserva.creada"
     * 
     * @param request Datos de la reserva (idUsuario, idMaterial, sede)
     * @return ReservaEntity persistido
     * @throws IllegalArgumentException si los datos no son válidos
     */
    ReservaEntity crearReserva(CrearReservaRequest request);
    
    /**
     * Cancela una reserva existente.
     * 
     * Realiza:
     * 1. Búsqueda de la reserva
     * 2. Cambio de estado a CANCELADA mediante State Pattern
     * 3. Reorganización de cola (actualizar posiciones)
     * 4. Publicación de evento "reserva.cancelada"
     * 
     * @param id ID de la reserva a cancelar
     * @return ReservaEntity con estado actualizado
     * @throws IllegalArgumentException si la reserva no existe
     */
    ReservaEntity cancelarReserva(String id);
    
    /**
     * Obtiene una reserva por su ID.
     */
    Optional<ReservaEntity> obtenerPorId(String id);
    
    /**
     * Lista las reservas activas (EN_ESPERA, NOTIFICADA) de un material.
     * Ordenadas por posición en cola.
     */
    List<ReservaEntity> listarPorMaterial(String idMaterial);
    
    /**
     * Lista las reservas activas de un usuario.
     */
    List<ReservaEntity> listarPorUsuario(String idUsuario);
    
    /**
     * Reorganiza las posiciones de la cola EN_ESPERA para un material.
     * Asigna posiciones 1, 2, 3... consecutivamente.
     */
    void reorganizarCola(String idMaterial);
}
