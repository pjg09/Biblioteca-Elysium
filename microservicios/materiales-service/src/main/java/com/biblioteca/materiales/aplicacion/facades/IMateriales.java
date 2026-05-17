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
 * - Encapsular validaciones de integridad
 * - Separar consultas síncronas (usadas por circulacion-service) de operaciones
 * 
 * Cumple con:
 * - Facade Pattern (interfaz simplificada)
 * - Single Responsibility (cada operación bien delimitada)
 * - Dependency Inversion (depende de abstracciones)
 */
public interface IMateriales Facade {
    
    /**
     * Obtiene un material por su ID.
     */
    Optional<MaterialEntity> obtenerPorId(String id);
    
    /**
     * Consulta la disponibilidad de un material.
     * Usado por circulacion-service para validar si puede prestar.
     * 
     * @param id ID del material
     * @return DTO con estado (disponible/no disponible) del material
     */
    DisponibilidadDTO consultarDisponibilidad(String id);
    
    /**
     * Agrega un nuevo material al catálogo.
     * 
     * Realiza:
     * 1. Validación de datos usando MaterialBuilder
     * 2. Verificación de reglas de negocio
     * 3. Persistencia
     * 
     * @param request Datos del material a agregar
     * @return MaterialEntity persistido
     * @throws IllegalArgumentException si los datos no son válidos
     */
    MaterialEntity agregarMaterial(CrearMaterialRequest request);
    
    /**
     * Actualiza el estado de un material (DISPONIBLE, PRESTADO, EN_REPARACION, etc).
     * 
     * @param id ID del material
     * @param nuevoEstado Nuevo estado del material
     * @return Optional con el material actualizado, o vacío si no existe
     */
    Optional<MaterialEntity> actualizarEstado(String id, String nuevoEstado);
    
    /**
     * Lista todos los materiales del catálogo.
     */
    List<MaterialEntity> listarTodos();
    
    /**
     * Lista materiales filtrados por tipo (LIBRO, DVD, EBOOK, etc).
     */
    List<MaterialEntity> listarPorTipo(String tipo);
}
