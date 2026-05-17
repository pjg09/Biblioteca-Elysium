package com.biblioteca.circulacion.aplicacion.facades;

import com.biblioteca.circulacion.aplicacion.dto.DevolverMaterialRequest;
import com.biblioteca.circulacion.aplicacion.dto.RegistrarPrestamoRequest;
import com.biblioteca.circulacion.aplicacion.dto.ResultadoOperacion;

/**
 * Facade para operaciones de circulación (loans, returns, renewals).
 * 
 * Coordina múltiples servicios de aplicación subyacentes:
 * - PrestamoService: Crear y gestionar préstamos
 * - DevolucionService: Registrar devoluciones
 * - RenovacionService: Renovar préstamos
 * 
 * Propósito:
 * - Proporcionar una interfaz única y simplificada para clientes externos (Controllers, eventos)
 * - Encapsular la lógica de coordinación entre servicios
 * - Aplicar el patrón Facade para reducir acoplamiento
 * 
 * Cumple con:
 * - Facade Pattern (una interfaz única para múltiples servicios)
 * - Single Responsibility (cada operación en un solo método)
 * - Dependency Inversion (depende de abstracciones, no de concretos)
 */
public interface ICirculacionFacade {
    
    /**
     * Registra un nuevo préstamo.
     * 
     * Realiza:
     * 1. Validación del estado del usuario
     * 2. Verificación de disponibilidad del material
     * 3. Validación de límite de préstamos
     * 4. Creación del préstamo con Builder pattern
     * 5. Persistencia
     * 6. Actualización de estado del material
     * 7. Publicación de eventos
     * 
     * @param request Datos del préstamo a registrar
     * @return ResultadoOperacion con estatus y detalles
     */
    ResultadoOperacion registrarPrestamo(RegistrarPrestamoRequest request);
    
    /**
     * Registra la devolución de un préstamo.
     * 
     * Realiza:
     * 1. Búsqueda del préstamo
     * 2. Validación del estado (debe estar ACTIVO)
     * 3. Cambio de estado a COMPLETADO mediante State Pattern
     * 4. Actualización de estado del material
     * 5. Publicación de eventos
     * 
     * @param idPrestamo ID del préstamo a devolver
     * @param request Datos de la devolución (usable, observaciones)
     * @return ResultadoOperacion con estatus y detalles
     */
    ResultadoOperacion registrarDevolucion(String idPrestamo, DevolverMaterialRequest request);
    
    /**
     * Renueva un préstamo existente.
     * 
     * Realiza:
     * 1. Búsqueda del préstamo
     * 2. Validación de deuda pendiente del usuario
     * 3. Verificación de reservas activas
     * 4. Validación de límite de renovaciones
     * 5. Cambio de fecha de devolución
     * 6. Publicación de eventos
     * 
     * @param idPrestamo ID del préstamo a renovar
     * @return ResultadoOperacion con estatus y detalles
     */
    ResultadoOperacion renovarPrestamo(String idPrestamo);
}
