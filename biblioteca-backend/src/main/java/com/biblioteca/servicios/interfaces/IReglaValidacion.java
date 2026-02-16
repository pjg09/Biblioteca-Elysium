package com.biblioteca.servicios.interfaces;

import com.biblioteca.dominio.objetosvalor.ResultadoValidacion;
import com.biblioteca.servicios.implementaciones.ValidadorReglasService.ContextoValidacion;

public interface IReglaValidacion {
    ResultadoValidacion validar(ContextoValidacion contexto);
    int obtenerPrioridad();
}