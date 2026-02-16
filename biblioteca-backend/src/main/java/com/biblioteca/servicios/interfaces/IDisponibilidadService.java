package com.biblioteca.servicios.interfaces;

import com.biblioteca.dominio.enumeraciones.EstadoMaterial;
import com.biblioteca.dominio.enumeraciones.TipoMaterial;

/**
 * Servicio para verificar disponibilidad de materiales.
 * 
 * Respeta SRP: Solo se encarga de verificar disponibilidad.
 */
public interface IDisponibilidadService {
    
    /**
     * Verifica si un material está disponible para préstamo.
     */
    boolean verificarDisponibilidad(String idMaterial);
    
    /**
     * Obtiene el estado actual de un material.
     */
    EstadoMaterial obtenerEstadoActual(String idMaterial);
    
    /**
     * Determina si un tipo de material es prestable según reglas de negocio.
     * (Por ejemplo: libros de referencia no son prestables)
     */
    boolean materialEsPrestable(String idMaterial, TipoMaterial tipoMaterial);
}