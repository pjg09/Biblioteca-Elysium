package com.biblioteca.servicios.interfaces;

import com.biblioteca.dominio.objetosvalor.ResultadoValidacion;

/**
 * Servicio orquestador que ejecuta todas las reglas de validación.
 * 
 * Respeta SRP: Solo coordina la ejecución de reglas.
 * Respeta DIP: Depende de IReglaValidacion (abstracción).
 */
public interface IValidadorReglasService {
    
    /**
     * Valida si se puede realizar un préstamo ejecutando todas las reglas.
     */
    ResultadoValidacion validarPrestamo(String idUsuario, String idMaterial);
    
    /**
     * Valida si se puede realizar una reserva.
     */
    ResultadoValidacion validarReserva(String idUsuario, String idMaterial);
    
    /**
     * Valida si se puede renovar un préstamo.
     */
    ResultadoValidacion validarRenovacion(String idPrestamo);
}