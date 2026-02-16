package com.biblioteca.repositorios;

import java.util.List;
import java.util.stream.Collectors;

import com.biblioteca.dominio.entidades.Usuario;
import com.biblioteca.dominio.enumeraciones.EstadoUsuario;
import com.biblioteca.dominio.enumeraciones.TipoUsuario;

public class RepositorioUsuarioEnMemoria extends RepositorioEnMemoria<Usuario> {
    
    public RepositorioUsuarioEnMemoria() {
        super("Usuario");
    }
    
    @Override
    protected String extraerId(Usuario entidad) {
        return entidad != null ? entidad.getId() : null;
    }
    
    public List<Usuario> buscarPorNombre(String nombre) {
        if (nombre == null) return List.of();
        return obtenerTodos().stream()
            .filter(u -> u.getNombre() != null && 
                         u.getNombre().toLowerCase().contains(nombre.toLowerCase()))
            .collect(Collectors.toList());
    }
    
    // CORREGIDO: Retorna Usuario en lugar de Optional
    public Usuario buscarPorEmail(String email) {
        if (email == null) return null;
        return obtenerTodos().stream()
            .filter(u -> u.getEmail() != null && u.getEmail().equalsIgnoreCase(email))
            .findFirst()
            .orElse(null);
    }
    
    public List<Usuario> buscarPorTipo(TipoUsuario tipo) {
        if (tipo == null) return List.of();
        return obtenerTodos().stream()
            .filter(u -> u.getTipo() == tipo)
            .collect(Collectors.toList());
    }
    
    public List<Usuario> buscarPorEstado(EstadoUsuario estado) {
        if (estado == null) return List.of();
        return obtenerTodos().stream()
            .filter(u -> u.getEstado() == estado)
            .collect(Collectors.toList());
    }
    
    public List<Usuario> buscarActivos() {
        return buscarPorEstado(EstadoUsuario.ACTIVO);
    }
    
    public List<Usuario> buscarBloqueados() {
        return obtenerTodos().stream()
            .filter(u -> u.getEstado() == EstadoUsuario.BLOQUEADO_MULTA || 
                         u.getEstado() == EstadoUsuario.BLOQUEADO_PERDIDA)
            .collect(Collectors.toList());
    }
    
    public boolean emailYaRegistrado(String email) {
        return buscarPorEmail(email) != null;
    }
}