package com.biblioteca.materiales.aplicacion.facades;

import com.biblioteca.materiales.aplicacion.MaterialService;
import com.biblioteca.materiales.aplicacion.dto.CrearMaterialRequest;
import com.biblioteca.materiales.aplicacion.dto.DisponibilidadDTO;
import com.biblioteca.materiales.infraestructura.persistencia.MaterialEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Implementación de IMateriales Facade.
 * 
 * Delega todas las operaciones al MaterialService subyacente.
 */
@Component
public class MaterialesFacade implements IMateriales {
    
    private final MaterialService materialService;
    
    public MaterialesFacade(MaterialService materialService) {
        this.materialService = materialService;
    }
    
    @Override
    public Optional<MaterialEntity> obtenerPorId(String id) {
        return materialService.obtenerPorId(id);
    }
    
    @Override
    public DisponibilidadDTO consultarDisponibilidad(String id) {
        return materialService.consultarDisponibilidad(id);
    }
    
    @Override
    public MaterialEntity agregarMaterial(CrearMaterialRequest request) {
        return materialService.agregarMaterial(request);
    }
    
    @Override
    public void actualizarEstado(String id, String nuevoEstado) {
        materialService.actualizarEstado(id, nuevoEstado);
    }
    
    @Override
    public List<MaterialEntity> listarTodos() {
        return materialService.listarTodos();
    }
    
    public List<MaterialEntity> listarPorTipo(String tipo) {
        return materialService.listarPorTipo(tipo);
    }
}
