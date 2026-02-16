package com.biblioteca.servicios.interfaces;

import com.biblioteca.dominio.objetosvalor.Evaluacion;

public interface IInspeccionMaterialService {
    Evaluacion inspeccionarMaterial(String idMaterial);
}