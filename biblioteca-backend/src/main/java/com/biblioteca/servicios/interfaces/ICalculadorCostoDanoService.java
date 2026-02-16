package com.biblioteca.servicios.interfaces;

import java.util.List;

import com.biblioteca.dominio.objetosvalor.Dano;

public interface ICalculadorCostoDanoService {
    double calcularCosto(Dano dano);
    double calcularCostoTotal(List<Dano> danos);
}