package com.biblioteca.servicios.implementaciones;

import java.util.ArrayList;
import java.util.List;

import com.biblioteca.dominio.entidades.Multa;
import com.biblioteca.dominio.objetosValor.ContextoMulta;
import com.biblioteca.servicios.interfaces.ICalculadorMulta;

public class GestorMultasService {
    private List<ICalculadorMulta> calculadores;
    
    public GestorMultasService() {
        this.calculadores = new ArrayList<>();
    }
    
    public void registrarCalculador(ICalculadorMulta calculador) {
        calculadores.add(calculador);
    }
    
    public Multa calcularMulta(ContextoMulta contexto) {
        for (ICalculadorMulta calculador : calculadores) {
            if (calculador.puedeCalcular(contexto)) {
                return calculador.calcular(contexto);
            }
        }
        throw new IllegalArgumentException("No hay calculador disponible para el tipo de multa: " + contexto.getTipoMulta());
    }
}