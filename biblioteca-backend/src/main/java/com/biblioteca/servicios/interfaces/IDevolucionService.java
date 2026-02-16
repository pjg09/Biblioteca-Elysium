package com.biblioteca.servicios.interfaces;

import com.biblioteca.dominio.objetosvalor.Evaluacion;
import com.biblioteca.dominio.objetosvalor.Resultado;

public interface IDevolucionService {
    Resultado registrarDevolucion(String idPrestamo, Evaluacion evaluacion);
}