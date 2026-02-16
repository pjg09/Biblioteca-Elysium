package com.biblioteca.repositorios;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.biblioteca.dominio.objetosValor.Resultado;

public class RepositorioEnMemoria<T> implements IRepositorio<T> {
    protected Map<String, T> almacenamiento;
    protected String nombreTipo;
    
    public RepositorioEnMemoria() {
        this.almacenamiento = new ConcurrentHashMap<>();
        this.nombreTipo = "Entidad";
    }
    
    public RepositorioEnMemoria(String nombreTipo) {
        this.almacenamiento = new ConcurrentHashMap<>();
        this.nombreTipo = nombreTipo;
    }
    
    @Override
    public Resultado agregar(T entidad) {
        try {
            String id = extraerId(entidad);
            if (id == null || id.isEmpty()) {
                return Resultado.Fallido("La entidad no tiene un ID válido");
            }
            
            if (almacenamiento.containsKey(id)) {
                return Resultado.Fallido(nombreTipo + " con ID " + id + " ya existe");
            }
            
            almacenamiento.put(id, entidad);
            return Resultado.Exitoso(nombreTipo + " agregada exitosamente", entidad);
            
        } catch (Exception e) {
            return Resultado.Fallido("Error al agregar: " + e.getMessage());
        }
    }
    
    @Override
    public Resultado actualizar(T entidad) {
        try {
            String id = extraerId(entidad);
            if (id == null || id.isEmpty()) {
                return Resultado.Fallido("La entidad no tiene un ID válido");
            }
            
            if (!almacenamiento.containsKey(id)) {
                return Resultado.Fallido(nombreTipo + " con ID " + id + " no encontrada");
            }
            
            almacenamiento.put(id, entidad);
            return Resultado.Exitoso(nombreTipo + " actualizada exitosamente", entidad);
            
        } catch (Exception e) {
            return Resultado.Fallido("Error al actualizar: " + e.getMessage());
        }
    }
    
    @Override
    public Resultado eliminar(String id) {
        try {
            if (id == null || id.isEmpty()) {
                return Resultado.Fallido("ID no válido");
            }
            
            T entidad = almacenamiento.remove(id);
            if (entidad == null) {
                return Resultado.Fallido(nombreTipo + " con ID " + id + " no encontrada");
            }
            
            return Resultado.Exitoso(nombreTipo + " eliminada exitosamente", entidad);
            
        } catch (Exception e) {
            return Resultado.Fallido("Error al eliminar: " + e.getMessage());
        }
    }
    
    @Override
    public T obtenerPorId(String id) {
        if (id == null || id.isEmpty()) {
            return null;
        }
        return almacenamiento.get(id);
    }
    
    @Override
    public List<T> obtenerTodos() {
        return new ArrayList<>(almacenamiento.values());
    }
    
    @Override
    public boolean existe(String id) {
        return id != null && almacenamiento.containsKey(id);
    }
    
    @Override
    public long contar() {
        return almacenamiento.size();
    }
    
    /**
     * Método auxiliar para extraer el ID de la entidad usando reflexión.
     * Las subclases pueden sobrescribir este método para mejor performance.
     */
    @SuppressWarnings("unchecked")
    protected String extraerId(T entidad) {
        try {
            var method = entidad.getClass().getMethod("getId");
            Object idObj = method.invoke(entidad);
            return idObj != null ? idObj.toString() : null;
        } catch (Exception e) {
            return null;
        }
    }
    
    public void limpiar() {
        almacenamiento.clear();
    }
}