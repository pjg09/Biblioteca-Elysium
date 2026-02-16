package com.biblioteca.servicios.implementaciones;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.biblioteca.dominio.entidades.Material;
import com.biblioteca.dominio.entidades.Usuario;
import com.biblioteca.dominio.objetosvalor.ResultadoValidacion;
import com.biblioteca.repositorios.RepositorioMaterialEnMemoria;
import com.biblioteca.repositorios.RepositorioUsuarioEnMemoria;
import com.biblioteca.servicios.interfaces.IDisponibilidadService;
import com.biblioteca.servicios.interfaces.IGestorBloqueoService;
import com.biblioteca.servicios.interfaces.ILimitePrestamoService;
import com.biblioteca.servicios.interfaces.IReglaValidacion;
import com.biblioteca.servicios.interfaces.IValidadorReglasService;

/**
 * ValidadorReglasService
 * 
 * Responsabilidad (SRP):
 * - Orquestar todas las validaciones de reglas de negocio
 * - Combinar resultados de múltiples validadores
 * - Ejecutar reglas en orden de prioridad
 * 
 * Dependencias (DIP):
 * - Depende de interfaces, no de implementaciones concretas
 * - Usa ILimitePrestamoService, IDisponibilidadService, IGestorBloqueoService
 * - Usa lista de IReglaValidacion (Strategy Pattern)
 */
public class ValidadorReglasService implements IValidadorReglasService {
    
    // Servicios de los que depende
    private final ILimitePrestamoService limiteService;
    private final IDisponibilidadService disponibilidadService;
    private final IGestorBloqueoService bloqueoService;
    
    // Repositorios para obtener entidades
    private final RepositorioUsuarioEnMemoria repoUsuario;
    private final RepositorioMaterialEnMemoria repoMaterial;
    
    // Lista de reglas personalizadas (Strategy Pattern)
    private final List<IReglaValidacion> reglas;
    
    /**
     * Constructor con inyección de dependencias
     */
    public ValidadorReglasService(
            ILimitePrestamoService limiteService,
            IDisponibilidadService disponibilidadService,
            IGestorBloqueoService bloqueoService,
            RepositorioUsuarioEnMemoria repoUsuario,
            RepositorioMaterialEnMemoria repoMaterial) {
        
        this.limiteService = limiteService;
        this.disponibilidadService = disponibilidadService;
        this.bloqueoService = bloqueoService;
        this.repoUsuario = repoUsuario;
        this.repoMaterial = repoMaterial;
        this.reglas = new ArrayList<>();
    }
    
    /**
     * Registrar una nueva regla de validación
     * Permite extensibilidad (OCP)
     */
    public void registrarRegla(IReglaValidacion regla) {
        this.reglas.add(regla);
    }
    
    /**
     * Eliminar una regla de validación
     */
    public boolean eliminarRegla(IReglaValidacion regla) {
        return this.reglas.remove(regla);
    }
    
    /**
     * Obtener todas las reglas registradas
     */
    public List<IReglaValidacion> obtenerReglas() {
        return new ArrayList<>(reglas);
    }
    
    /**
     * Limpiar todas las reglas
     */
    public void limpiarReglas() {
        reglas.clear();
    }
    
    /**
     * Valida si un préstamo puede ser realizado
     * 
     * Orquesta múltiples validaciones:
     * 1. Usuario existe y está activo
     * 2. Material existe y está disponible
     * 3. Usuario no está bloqueado
     * 4. Usuario no excede su límite
     * 5. Reglas personalizadas adicionales
     */
    @Override
    public ResultadoValidacion validarPrestamo(String idUsuario, String idMaterial) {
        
        // Validar parámetros de entrada
        if (idUsuario == null || idUsuario.trim().isEmpty()) {
            return ResultadoValidacion.Invalido("ID de usuario no puede estar vacío");
        }
        if (idMaterial == null || idMaterial.trim().isEmpty()) {
            return ResultadoValidacion.Invalido("ID de material no puede estar vacío");
        }
        
        // 1. Validar que el usuario existe
        Usuario usuario = repoUsuario.obtenerPorId(idUsuario);
        if (usuario == null) {
            return ResultadoValidacion.Invalido("Usuario no encontrado: " + idUsuario);
        }
        
        // 2. Validar que el material existe
        Material material = repoMaterial.obtenerPorId(idMaterial);
        if (material == null) {
            return ResultadoValidacion.Invalido("Material no encontrado: " + idMaterial);
        }
        
        // Comenzar con resultado válido
        ResultadoValidacion resultado = ResultadoValidacion.Valido();
        
        // 3. Validar disponibilidad del material
        if (!disponibilidadService.verificarDisponibilidad(idMaterial)) {
            resultado = resultado.combinar(
                ResultadoValidacion.Invalido("Material no disponible: " + material.getTitulo())
            );
        }
        
        // 4. Validar que el usuario no está bloqueado
        ResultadoValidacion validacionBloqueo = bloqueoService.verificarSiDebeBloquear(idUsuario);
        resultado = resultado.combinar(validacionBloqueo);
        
        // 5. Validar límites del usuario (solo si las validaciones anteriores pasaron)
        if (resultado.esValido()) {
            ResultadoValidacion validacionLimite = limiteService.validarLimite(idUsuario, usuario.getTipo());
            resultado = resultado.combinar(validacionLimite);
        }
        
        // 6. Aplicar reglas personalizadas (ordenadas por prioridad)
        resultado = aplicarReglas(idUsuario, idMaterial, resultado);
        
        return resultado;
    }
    
    /**
     * Valida si una reserva puede ser creada
     */
    @Override
    public ResultadoValidacion validarReserva(String idUsuario, String idMaterial) {
        
        // Validar parámetros de entrada
        if (idUsuario == null || idUsuario.trim().isEmpty()) {
            return ResultadoValidacion.Invalido("ID de usuario no puede estar vacío");
        }
        if (idMaterial == null || idMaterial.trim().isEmpty()) {
            return ResultadoValidacion.Invalido("ID de material no puede estar vacío");
        }
        
        // 1. Validar que el usuario existe
        Usuario usuario = repoUsuario.obtenerPorId(idUsuario);
        if (usuario == null) {
            return ResultadoValidacion.Invalido("Usuario no encontrado: " + idUsuario);
        }
        
        // 2. Validar que el material existe
        Material material = repoMaterial.obtenerPorId(idMaterial);
        if (material == null) {
            return ResultadoValidacion.Invalido("Material no encontrado: " + idMaterial);
        }
        
        // Comenzar con resultado válido
        ResultadoValidacion resultado = ResultadoValidacion.Valido();
        
        // 3. Validar que el usuario no está bloqueado
        ResultadoValidacion validacionBloqueo = bloqueoService.verificarSiDebeBloquear(idUsuario);
        resultado = resultado.combinar(validacionBloqueo);
        
        // 4. Para reserva, el material NO debe estar disponible (esa es la razón de reservar)
        if (disponibilidadService.verificarDisponibilidad(idMaterial)) {
            resultado = resultado.combinar(
                ResultadoValidacion.Invalido("El material está disponible. No requiere reserva: " + material.getTitulo())
            );
        }
        
        // 5. Aplicar reglas personalizadas
        resultado = aplicarReglas(idUsuario, idMaterial, resultado);
        
        return resultado;
    }
    
    /**
     * Valida si un préstamo puede ser renovado
     */
    @Override
    public ResultadoValidacion validarRenovacion(String idPrestamo) {
        
        // Validar parámetros de entrada
        if (idPrestamo == null || idPrestamo.trim().isEmpty()) {
            return ResultadoValidacion.Invalido("ID de préstamo no puede estar vacío");
        }
        
        // Las validaciones específicas de renovación se delegan a las reglas
        // Comenzar con resultado válido
        ResultadoValidacion resultado = ResultadoValidacion.Valido();
        
        // Crear contexto simplificado para renovación (sin usuario/material específicos)
        ContextoValidacion contexto = new ContextoValidacion(null, null, null, null);
        
        // Aplicar reglas que apliquen a renovación
        List<IReglaValidacion> reglasOrdenadas = new ArrayList<>(reglas);
        reglasOrdenadas.sort(Comparator.comparingInt(IReglaValidacion::obtenerPrioridad));
        
        for (IReglaValidacion regla : reglasOrdenadas) {
            // Solo aplicar reglas que sean relevantes para renovación
            if (esReglaAplicableARenovacion(regla)) {
                ResultadoValidacion resultadoRegla = regla.validar(contexto);
                resultado = resultado.combinar(resultadoRegla);
                
                // Si ya es inválido, podemos detenernos
                if (!resultado.esValido()) {
                    break;
                }
            }
        }
        
        return resultado;
    }
    
    /**
     * Valida si una devolución puede ser procesada
     */
    public ResultadoValidacion validarDevolucion(String idPrestamo) {
        
        // Validar parámetros de entrada
        if (idPrestamo == null || idPrestamo.trim().isEmpty()) {
            return ResultadoValidacion.Invalido("ID de préstamo no puede estar vacío");
        }
        
        // Las devoluciones generalmente tienen menos validaciones
        return ResultadoValidacion.Valido();
    }
    
    /**
     * Determina si una regla es aplicable para renovación
     */
    private boolean esReglaAplicableARenovacion(IReglaValidacion regla) {
        // Las reglas de usuario y material generalmente aplican
        String nombreRegla = regla.getClass().getSimpleName();
        return nombreRegla.contains("Usuario") || 
               nombreRegla.contains("Material") ||
               nombreRegla.contains("Limite");
    }
    
    /**
     * Aplica todas las reglas personalizadas registradas
     * Las ejecuta en orden de prioridad (Chain of Responsibility)
     * 
     * @param idUsuario ID del usuario
     * @param idMaterial ID del material
     * @param resultadoAcumulado Resultado acumulado hasta el momento
     * @return Resultado combinado de todas las reglas
     */
    private ResultadoValidacion aplicarReglas(String idUsuario, String idMaterial, 
                                               ResultadoValidacion resultadoAcumulado) {
        
        // Si no hay reglas, retornar el resultado acumulado
        if (reglas.isEmpty()) {
            return resultadoAcumulado;
        }
        
        // Obtener usuario y material (pueden ser null)
        Usuario usuario = repoUsuario.obtenerPorId(idUsuario);
        Material material = repoMaterial.obtenerPorId(idMaterial);
        
        // Crear contexto para las reglas
        ContextoValidacion contexto = new ContextoValidacion(idUsuario, idMaterial, usuario, material);
        
        // Ordenar reglas por prioridad (menor número = mayor prioridad)
        List<IReglaValidacion> reglasOrdenadas = new ArrayList<>(reglas);
        reglasOrdenadas.sort(Comparator.comparingInt(IReglaValidacion::obtenerPrioridad));
        
        // Aplicar cada regla
        ResultadoValidacion resultado = resultadoAcumulado;
        for (IReglaValidacion regla : reglasOrdenadas) {
            
            // Ejecutar la regla
            ResultadoValidacion resultadoRegla = regla.validar(contexto);
            
            // Combinar con el resultado acumulado
            resultado = resultado.combinar(resultadoRegla);
            
            // Si alguna regla falla crítica, podemos parar aquí (opcional)
            if (!resultado.esValido()) {
                break;
            }
        }
        
        return resultado;
    }
    
    /**
     * Clase interna para encapsular el contexto de validación
     * Objeto de valor que pasa información a las reglas
     */
    public static class ContextoValidacion {
        private final String idUsuario;
        private final String idMaterial;
        private final Usuario usuario;
        private final Material material;
        
        public ContextoValidacion(String idUsuario, String idMaterial, 
                                  Usuario usuario, Material material) {
            this.idUsuario = idUsuario;
            this.idMaterial = idMaterial;
            this.usuario = usuario;
            this.material = material;
        }
        
        public String getIdUsuario() { return idUsuario; }
        public String getIdMaterial() { return idMaterial; }
        public Usuario getUsuario() { return usuario; }
        public Material getMaterial() { return material; }
        
        public boolean tieneUsuario() { return usuario != null; }
        public boolean tieneMaterial() { return material != null; }
    }
}