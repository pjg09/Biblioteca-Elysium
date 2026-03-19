package com.biblioteca.repositorios;

import java.util.List;
import java.util.stream.Collectors;

import com.biblioteca.dominio.entidades.Multa;
import com.biblioteca.dominio.enumeraciones.EstadoMulta;

public class RepositorioMultaEnMemoria extends RepositorioEnMemoria<Multa> {
    
    public RepositorioMultaEnMemoria() {
        super("Multa");
    }
    
    @Override
    protected String extraerId(Multa entidad) {
        return entidad != null ? entidad.getId() : null;
    }
    
    public List<Multa> buscarPorUsuario(String idUsuario) {
        if (idUsuario == null) return List.of();
        return obtenerTodos().stream()
            .filter(m -> idUsuario.equals(m.getIdUsuario()))
            .collect(Collectors.toList());
    }
    
    public List<Multa> buscarPorPrestamo(String idPrestamo) {
        if (idPrestamo == null) return List.of();
        return obtenerTodos().stream()
            .filter(m -> idPrestamo.equals(m.getIdPrestamo()))
            .collect(Collectors.toList());
    }
    
    public List<Multa> buscarPendientes() {
        return obtenerTodos().stream()
            .filter(m -> m.getEstado() == EstadoMulta.PENDIENTE)
            .collect(Collectors.toList());
    }
    
    public List<Multa> buscarPendientesPorUsuario(String idUsuario) {
        if (idUsuario == null) return List.of();
        return buscarPorUsuario(idUsuario).stream()
            .filter(m -> m.getEstado() == EstadoMulta.PENDIENTE)
            .collect(Collectors.toList());
    }
    
    public double calcularTotalMultasPendientes(String idUsuario) {
        return buscarPendientesPorUsuario(idUsuario).stream()
            .mapToDouble(Multa::calcularMontoTotal)
            .sum();
    }
    
    public boolean tieneMultasPendientes(String idUsuario) {
        return !buscarPendientesPorUsuario(idUsuario).isEmpty();
    }
    
    public List<Multa> buscarPagadas() {
        return obtenerTodos().stream()
            .filter(m -> m.getEstado() == EstadoMulta.PAGADA)
            .collect(Collectors.toList());
    }
}