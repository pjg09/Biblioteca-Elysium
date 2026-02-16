package com.biblioteca.servicios.implementaciones.reglas;

import com.biblioteca.dominio.objetosvalor.ResultadoValidacion;
import com.biblioteca.servicios.implementaciones.ValidadorReglasService.ContextoValidacion;
import com.biblioteca.servicios.interfaces.IReglaValidacion;

public class ReglaMaterialExiste implements IReglaValidacion {
    
    @Override
    public ResultadoValidacion validar(ContextoValidacion contexto) {
        if (!contexto.tieneMaterial()) {
            return ResultadoValidacion.Invalido("Material no encontrado");
        }
        return ResultadoValidacion.Valido();
    }
    
    @Override
    public int obtenerPrioridad() {
        return 3;
    }
}