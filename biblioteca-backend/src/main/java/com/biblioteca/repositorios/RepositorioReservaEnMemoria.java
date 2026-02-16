package com.biblioteca.repositorios;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.biblioteca.dominio.entidades.Reserva;
import com.biblioteca.dominio.enumeraciones.EstadoTransaccion;

public class RepositorioReservaEnMemoria extends RepositorioEnMemoria<Reserva> {
    
    public RepositorioReservaEnMemoria() {
        super("Reserva");
    }
    
    @Override
    protected String extraerId(Reserva entidad) {
        return entidad != null ? entidad.getId() : null;
    }
    
    // Métodos específicos para Reserva
    public List<Reserva> buscarPorUsuario(String idUsuario) {
        return obtenerTodos().stream()
            .filter(r -> r.getIdUsuario().equals(idUsuario))
            .collect(Collectors.toList());
    }
    
    public List<Reserva> buscarPorMaterial(String idMaterial) {
        return obtenerTodos().stream()
            .filter(r -> r.getIdMaterial().equals(idMaterial))
            .collect(Collectors.toList());
    }
    
    public List<Reserva> buscarActivas() {
        LocalDateTime ahora = LocalDateTime.now();
        return obtenerTodos().stream()
            .filter(r -> r.getEstado() == EstadoTransaccion.ACTIVA)
            .filter(r -> r.getFechaExpiracion().isAfter(ahora))
            .collect(Collectors.toList());
    }
    
    public List<Reserva> buscarActivasPorMaterial(String idMaterial) {
        LocalDateTime ahora = LocalDateTime.now();
        return buscarPorMaterial(idMaterial).stream()
            .filter(r -> r.getEstado() == EstadoTransaccion.ACTIVA)
            .filter(r -> r.getFechaExpiracion().isAfter(ahora))
            .sorted((r1, r2) -> Integer.compare(r1.getPosicionCola(), r2.getPosicionCola()))
            .collect(Collectors.toList());
    }
    
    public List<Reserva> buscarExpiradas() {
        LocalDateTime ahora = LocalDateTime.now();
        return obtenerTodos().stream()
            .filter(r -> r.getEstado() == EstadoTransaccion.ACTIVA)
            .filter(r -> r.getFechaExpiracion().isBefore(ahora))
            .collect(Collectors.toList());
    }
    
    // CORREGIDO: Cambiado de Optional<Reserva> a Reserva (puede retornar null)
    public Reserva buscarPrimeraEnCola(String idMaterial) {
        List<Reserva> activas = buscarActivasPorMaterial(idMaterial);
        if (activas.isEmpty()) {
            return null;
        }
        return activas.get(0); // La primera ya está ordenada por posición
    }
    
    public boolean existeReservaActiva(String idUsuario, String idMaterial) {
        LocalDateTime ahora = LocalDateTime.now();
        return obtenerTodos().stream()
            .anyMatch(r -> r.getIdUsuario().equals(idUsuario) &&
                          r.getIdMaterial().equals(idMaterial) &&
                          r.getEstado() == EstadoTransaccion.ACTIVA &&
                          r.getFechaExpiracion().isAfter(ahora));
    }
    
    public long contarReservasActivasPorMaterial(String idMaterial) {
        return buscarActivasPorMaterial(idMaterial).size();
    }
    
    public List<Reserva> buscarReservasPorUsuarioYEstado(String idUsuario, EstadoTransaccion estado) {
        return buscarPorUsuario(idUsuario).stream()
            .filter(r -> r.getEstado() == estado)
            .collect(Collectors.toList());
    }
    
    public void cancelarReservasExpiradas() {
        List<Reserva> expiradas = buscarExpiradas();
        for (Reserva reserva : expiradas) {
            reserva.setEstado(EstadoTransaccion.CANCELADA);
            actualizar(reserva);
        }
    }
}