package com.biblioteca.servicios.interfaces;

import com.biblioteca.dominio.entidades.Multa;
import com.biblioteca.dominio.objetosvalor.ContextoMulta;

public interface ICalculadorMulta {
    Multa calcular(ContextoMulta contexto);
    boolean puedeCalcular(ContextoMulta contexto);
}