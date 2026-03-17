package com.biblioteca.servicios.implementaciones.reglas;

import com.biblioteca.dominio.entidades.Material;
import com.biblioteca.dominio.objetosvalor.ResultadoValidacion;
import com.biblioteca.dominio.objetosvalor.ContextoValidacion;
import com.biblioteca.dominio.enumeraciones.TipoOperacion;
import com.biblioteca.servicios.interfaces.IDisponibilidadService;
import com.biblioteca.servicios.interfaces.IReglaValidacion;

public class ReglaMaterialDisponible implements IReglaValidacion {
    
    private IDisponibilidadService disponibilidadService;
    
    public ReglaMaterialDisponible(IDisponibilidadService disponibilidadService) {
        this.disponibilidadService = disponibilidadService;
    }
    
    @Override
    public ResultadoValidacion validar(ContextoValidacion contexto) {
        if (!contexto.tieneMaterial()) {
            return ResultadoValidacion.Invalido("Material no encontrado en el contexto");
        }
        
        Material material = contexto.getMaterial();
        
        // Si es reserva, la regla de estar disponible no aplica
        if (contexto.getOperacion() == TipoOperacion.RESERVA) {
            return ResultadoValidacion.Valido();
        }
        
        // Usar el servicio de disponibilidad
        boolean disponible = disponibilidadService.verificarDisponibilidad(material.getId().getValor());
        
        if (!disponible) {
            return ResultadoValidacion.Invalido(
                "Material no disponible. Estado actual: " + material.getEstado()
            );
        }
        
        return ResultadoValidacion.Valido();
    }
    
    @Override
    public int obtenerPrioridad() {
        return 2;
    }
}