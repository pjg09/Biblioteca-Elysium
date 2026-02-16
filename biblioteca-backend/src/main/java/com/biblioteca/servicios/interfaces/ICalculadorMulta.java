package com.biblioteca.servicios.interfaces;

import com.biblioteca.dominio.entidades.Multa;
import com.biblioteca.dominio.objetosValor.ContextoMulta;

public interface ICalculadorMulta {
    Multa calcular(ContextoMulta contexto);
    boolean puedeCalcular(ContextoMulta contexto);
}