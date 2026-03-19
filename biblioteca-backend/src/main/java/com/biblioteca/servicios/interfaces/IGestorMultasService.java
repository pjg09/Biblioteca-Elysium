package com.biblioteca.servicios.interfaces;

import com.biblioteca.dominio.entidades.Multa;
import com.biblioteca.dominio.objetosvalor.ContextoMulta;
import com.biblioteca.dominio.objetosvalor.Resultado;

public interface IGestorMultasService {
    void registrarCalculador(ICalculadorMulta calculador);
    Multa calcularMulta(ContextoMulta contexto);
    Resultado pagarMulta(String idMulta);
}
