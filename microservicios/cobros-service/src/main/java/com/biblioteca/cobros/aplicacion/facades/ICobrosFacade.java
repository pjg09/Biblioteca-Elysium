package com.biblioteca.cobros.aplicacion.facades;

import com.biblioteca.cobros.aplicacion.dto.RegistrarPagoRequest;
import com.biblioteca.cobros.infraestructura.persistencia.RegistroPagoEntity;

import java.util.List;
import java.util.Optional;

/**
 * Facade para operaciones de cobro y pagos de multas.
 * 
 * Coordina operaciones de:
 * - Registro de pagos
 * - Consulta de pagos
 * - Actualización de estado de deuda
 * - Publicación de eventos de pago y desbloqueo
 * 
 * Propósito:
 * - Interfaz única para registrar pagos de multas
 * - Coordinación con multas-service y usuarios-service
 * - Publicación de eventos en RabbitMQ
 * - Lógica de desbloqueo cuando deuda = 0
 * 
 * Cumple con:
 * - Facade Pattern
 * - Domain Events (publicación de multa.pagada, usuario.desbloqueo.peticion)
 * - Transactional consistency
 */
public interface ICobrosFacade {
    
    /**
     * Registra el pago de una multa.
     * 
     * Realiza:
     * 1. Creación de registro de pago
     * 2. Publicación de evento "multa.pagada"
     * 3. Actualización de estado de deuda
     * 4. Si deuda total = 0: publicación de "usuario.desbloqueo.peticion"
     * 
     * @param request Datos del pago (multaId, usuarioId, monto)
     * @return RegistroPagoEntity persistido
     */
    RegistroPagoEntity registrarPago(RegistrarPagoRequest request);
    
    /**
     * Obtiene un registro de pago por su ID.
     */
    Optional<RegistroPagoEntity> obtenerPorId(String id);
    
    /**
     * Lista todos los pagos registrados de un usuario.
     */
    List<RegistroPagoEntity> obtenerPorUsuario(String usuarioId);
}
