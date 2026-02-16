package com.biblioteca.repositorios;

import java.util.List;
import java.util.stream.Collectors;

import com.biblioteca.dominio.entidades.Material;
import com.biblioteca.dominio.enumeraciones.EstadoMaterial;
import com.biblioteca.dominio.enumeraciones.TipoMaterial;

public class RepositorioMaterialEnMemoria extends RepositorioEnMemoria<Material> {
    
    public RepositorioMaterialEnMemoria() {
        super("Material");
    }
    
    @Override
    protected String extraerId(Material entidad) {
        return entidad != null ? entidad.getId() : null;
    }
    
    public List<Material> buscarPorTitulo(String titulo) {
        if (titulo == null) return List.of();
        return obtenerTodos().stream()
            .filter(m -> m.getTitulo() != null && 
                         m.getTitulo().toLowerCase().contains(titulo.toLowerCase()))
            .collect(Collectors.toList());
    }
    
    public List<Material> buscarPorAutor(String autor) {
        if (autor == null) return List.of();
        return obtenerTodos().stream()
            .filter(m -> m.getAutor() != null && 
                         m.getAutor().toLowerCase().contains(autor.toLowerCase()))
            .collect(Collectors.toList());
    }
    
    public List<Material> buscarPorTipo(TipoMaterial tipo) {
        if (tipo == null) return List.of();
        return obtenerTodos().stream()
            .filter(m -> m.getTipo() == tipo)
            .collect(Collectors.toList());
    }
    
    public List<Material> buscarPorEstado(EstadoMaterial estado) {
        if (estado == null) return List.of();
        return obtenerTodos().stream()
            .filter(m -> m.getEstado() == estado)
            .collect(Collectors.toList());
    }
    
    public List<Material> buscarDisponibles() {
        return buscarPorEstado(EstadoMaterial.DISPONIBLE);
    }
    
    public boolean estaDisponible(String id) {
        Material material = obtenerPorId(id);
        return material != null && material.getEstado() == EstadoMaterial.DISPONIBLE;
    }
}