package com.biblioteca.usuarios.aplicacion.facades;

import com.biblioteca.usuarios.aplicacion.dto.CrearUsuarioRequest;
import com.biblioteca.usuarios.aplicacion.dto.EstadoUsuarioDTO;
import com.biblioteca.usuarios.aplicacion.dto.LimitePrestamoDTO;
import com.biblioteca.usuarios.infraestructura.persistencia.UsuarioEntity;

import java.util.List;
import java.util.Optional;

/**
 * Facade para operaciones de gestión de usuarios.
 * 
 * Coordina operaciones de:
 * - Consulta de estado del usuario
 * - Consulta de límite de préstamos
 * - Registro y gestión de usuarios
 * 
 * Propósito:
 * - Proporcionar una interfaz única para acceso a datos de usuario
 * - Encapsular validaciones y reglas de negocio
 * - Separar consultas síncronas (usadas por circulacion-service) de operaciones
 * 
 * Cumple con:
 * - Facade Pattern (interfaz simplificada)
 * - Single Responsibility (cada operación bien delimitada)
 * - Dependency Inversion (depende de abstracciones)
 */
public interface IUsuariosFacade {
    
    /**
     * Obtiene un usuario por su ID.
     */
    Optional<UsuarioEntity> obtenerPorId(String id);
    
    /**
     * Consulta el estado actual del usuario.
     * Usado por circulacion-service para validar si puede prestar.
     * 
     * @param id ID del usuario
     * @return DTO con estado (activo/inactivo) y tipo de usuario
     */
    EstadoUsuarioDTO consultarEstado(String id);
    
    /**
     * Consulta el límite de préstamos del usuario.
     * Usado por circulacion-service para validar cuota máxima.
     * 
     * @param id ID del usuario
     * @return DTO con límite máximo de préstamos simultáneos
     */
    LimitePrestamoDTO consultarLimite(String id);
    
    /**
     * Registra un nuevo usuario en el sistema.
     * 
     * Realiza:
     * 1. Validación de datos usando UsuarioBuilder
     * 2. Verificación de reglas de negocio
     * 3. Persistencia
     * 4. Publicación de eventos (si aplica)
     * 
     * @param request Datos del usuario a registrar
     * @return UsuarioEntity persistido
     * @throws IllegalArgumentException si los datos no son válidos
     */
    UsuarioEntity registrarUsuario(CrearUsuarioRequest request);
    
    /**
     * Lista todos los usuarios del sistema.
     */
    List<UsuarioEntity> listarTodos();
}
