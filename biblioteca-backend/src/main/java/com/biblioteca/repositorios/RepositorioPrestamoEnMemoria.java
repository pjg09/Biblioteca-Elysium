package com.biblioteca.repositorios;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.biblioteca.dominio.entidades.Prestamo;
import com.biblioteca.dominio.enumeraciones.EstadoTransaccion;

public class RepositorioPrestamoEnMemoria extends RepositorioEnMemoria<Prestamo> {
    
    public RepositorioPrestamoEnMemoria() {
        super("Préstamo");
    }
    
    @Override
    protected String extraerId(Prestamo entidad) {
        return entidad != null ? entidad.getId() : null;
    }
    
    public List<Prestamo> buscarPorUsuario(String idUsuario) {
        if (idUsuario == null) return List.of();
        return obtenerTodos().stream()
            .filter(p -> idUsuario.equals(p.getIdUsuario()))
            .collect(Collectors.toList());
    }
    
    public List<Prestamo> buscarPorMaterial(String idMaterial) {
        if (idMaterial == null) return List.of();
        return obtenerTodos().stream()
            .filter(p -> idMaterial.equals(p.getIdMaterial()))
            .collect(Collectors.toList());
    }
    
    public List<Prestamo> buscarActivos() {
        return obtenerTodos().stream()
            .filter(p -> p.getEstado() == EstadoTransaccion.ACTIVA)
            .filter(p -> p.getFechaDevolucionReal() == null)
            .collect(Collectors.toList());
    }
    
    public List<Prestamo> buscarActivosPorUsuario(String idUsuario) {
        if (idUsuario == null) return List.of();
        return buscarPorUsuario(idUsuario).stream()
            .filter(p -> p.getEstado() == EstadoTransaccion.ACTIVA)
            .filter(p -> p.getFechaDevolucionReal() == null)
            .collect(Collectors.toList());
    }
    
    public List<Prestamo> buscarVencidos() {
        LocalDateTime ahora = LocalDateTime.now();
        return obtenerTodos().stream()
            .filter(p -> p.getEstado() == EstadoTransaccion.ACTIVA)
            .filter(p -> p.getFechaDevolucionReal() == null)
            .filter(p -> p.getFechaDevolucionEsperada().isBefore(ahora))
            .collect(Collectors.toList());
    }
    
    public List<Prestamo> buscarVencidosPorUsuario(String idUsuario) {
        if (idUsuario == null) return List.of();
        LocalDateTime ahora = LocalDateTime.now();
        return buscarPorUsuario(idUsuario).stream()
            .filter(p -> p.getEstado() == EstadoTransaccion.ACTIVA)
            .filter(p -> p.getFechaDevolucionReal() == null)
            .filter(p -> p.getFechaDevolucionEsperada().isBefore(ahora))
            .collect(Collectors.toList());
    }
    
    public long contarActivosPorUsuario(String idUsuario) {
        return buscarActivosPorUsuario(idUsuario).size();
    }
    
    public boolean tienePrestamosActivos(String idUsuario) {
        return !buscarActivosPorUsuario(idUsuario).isEmpty();
    }
}