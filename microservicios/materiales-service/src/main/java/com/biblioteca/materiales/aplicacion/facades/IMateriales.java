package com.biblioteca.materiales.aplicacion.facades;

import com.biblioteca.materiales.aplicacion.dto.CrearMaterialRequest;
import com.biblioteca.materiales.aplicacion.dto.DisponibilidadDTO;
import com.biblioteca.materiales.infraestructura.persistencia.MaterialEntity;

import java.util.List;
import java.util.Optional;

/**
 * Facade para operaciones de gestión de materiales.
 * 
 * Coordina operaciones de:
 * - Consulta de disponibilidad
 * - Creación de materiales
 * - Actualización de estado
 * - Listado de materiales
 * 
 * Propósito:
 * - Proporcionar una interfaz única para acceso a catálogo
 * - Coordinar entre MaterialService y controladores REST
 * - Encapsular lógica de disponibilidad
 * 
 * Cumple con:
 * - Facade Pattern
 */
public interface IMateriales {
    
    /**
     * Consulta disponibilidad de un material.
     */
    DisponibilidadDTO consultarDisponibilidad(String idMaterial);
    
    /**
     * Obtiene un material por ID.
     */
    Optional<MaterialEntity> obtenerPorId(String id);
    
    /**
     * Lista todos los materiales.
     */
    List<MaterialEntity> listarTodos();
    
    /**
     * Crea un nuevo material.
     */
    MaterialEntity agregarMaterial(CrearMaterialRequest request);
    
    /**
     * Actualiza el estado de un material.
     */
    void actualizarEstado(String idMaterial, String nuevoEstado);
}
