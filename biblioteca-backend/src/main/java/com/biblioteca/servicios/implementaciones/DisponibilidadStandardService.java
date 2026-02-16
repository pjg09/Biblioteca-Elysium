package com.biblioteca.servicios.implementaciones;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import com.biblioteca.dominio.entidades.Material;
import com.biblioteca.dominio.entidades.Prestamo;
import com.biblioteca.dominio.enumeraciones.EstadoMaterial;
import com.biblioteca.dominio.enumeraciones.EstadoTransaccion;
import com.biblioteca.dominio.enumeraciones.TipoMaterial;
import com.biblioteca.dominio.excepciones.MaterialNoDisponibleException;
import com.biblioteca.repositorios.IRepositorio;
import com.biblioteca.servicios.interfaces.IDisponibilidadService;

public class DisponibilidadStandardService implements IDisponibilidadService {
    
    private final IRepositorio<Material> repoMaterial;
    private final IRepositorio<Prestamo> repoPrestamo;
    private final Set<TipoMaterial> materialesNoPrestables;
    
    public DisponibilidadStandardService(
            IRepositorio<Material> repoMaterial,
            IRepositorio<Prestamo> repoPrestamo) {
        
        this.repoMaterial = repoMaterial;
        this.repoPrestamo = repoPrestamo;
        this.materialesNoPrestables = new HashSet<>();
        inicializarMaterialesNoPrestables();
    }
    
    private void inicializarMaterialesNoPrestables() {
        // Materiales de referencia no se prestan
        materialesNoPrestables.add(TipoMaterial.LIBRO_REFERENCIA);
        // Puedes agregar más según políticas de la biblioteca
    }
    
    @Override
    public boolean verificarDisponibilidad(String idMaterial) {
        Material material = repoMaterial.obtenerPorId(idMaterial);
        if (material == null) {
            return false;
        }
        
        return material.getEstado() == EstadoMaterial.DISPONIBLE;
    }
    
    @Override
    public EstadoMaterial obtenerEstadoActual(String idMaterial) {
        Material material = repoMaterial.obtenerPorId(idMaterial);
        if (material == null) {
            return null;
        }
        return material.getEstado();
    }
    
    @Override
    public boolean materialEsPrestable(String idMaterial, TipoMaterial tipoMaterial) {
        if (materialesNoPrestables.contains(tipoMaterial)) {
            return false;
        }
        
        Material material = repoMaterial.obtenerPorId(idMaterial);
        if (material == null) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Verifica disponibilidad y lanza excepción si no está disponible
     * @throws MaterialNoDisponibleException
     */
    public void verificarDisponibilidadOLanzarExcepcion(String idMaterial) 
            throws MaterialNoDisponibleException {
        
        Material material = repoMaterial.obtenerPorId(idMaterial);
        if (material == null) {
            throw new MaterialNoDisponibleException(idMaterial, null);
        }
        
        if (material.getEstado() != EstadoMaterial.DISPONIBLE) {
            throw new MaterialNoDisponibleException(idMaterial, material.getEstado());
        }
    }
    
    /**
     * Verifica si hay ejemplares disponibles (para materiales con múltiples copias)
     */
    public boolean hayEjemplaresDisponibles(String idMaterial) {
        // Por ahora, asumimos que cada material es único
        return verificarDisponibilidad(idMaterial);
    }
    
    /**
     * Obtiene la fecha estimada de disponibilidad
     */
    public LocalDateTime obtenerFechaDisponibilidadEstimada(String idMaterial) {
        Material material = repoMaterial.obtenerPorId(idMaterial);
        if (material == null || material.getEstado() == EstadoMaterial.DISPONIBLE) {
            return LocalDateTime.now();
        }
        
        // Buscar préstamo activo para calcular fecha estimada
        return repoPrestamo.obtenerTodos().stream()
            .filter(p -> p.getIdMaterial().equals(idMaterial))
            .filter(p -> p.getEstado() == EstadoTransaccion.ACTIVA)
            .filter(p -> p.getFechaDevolucionReal() == null)
            .map(Prestamo::getFechaDevolucionEsperada)
            .max(LocalDateTime::compareTo)
            .orElse(LocalDateTime.now().plusDays(7)); // Default: 7 días
    }
    
    /**
     * Verifica si un material puede ser reservado
     */
    public boolean puedeSerReservado(String idMaterial) {
        Material material = repoMaterial.obtenerPorId(idMaterial);
        if (material == null) {
            return false;
        }
        
        // Los materiales no prestables no pueden reservarse
        if (materialesNoPrestables.contains(material.getTipo())) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Agrega un tipo de material a la lista de no prestables
     */
    public void agregarMaterialNoPrestable(TipoMaterial tipo) {
        materialesNoPrestables.add(tipo);
    }
    
    /**
     * Quita un tipo de material de la lista de no prestables
     */
    public void quitarMaterialNoPrestable(TipoMaterial tipo) {
        materialesNoPrestables.remove(tipo);
    }
    
    /**
     * Obtiene la lista de materiales no prestables
     */
    public Set<TipoMaterial> getMaterialesNoPrestables() {
        return new HashSet<>(materialesNoPrestables);
    }
}