package com.biblioteca.dominio.builders.interfaces;

import com.biblioteca.dominio.entidades.Multa;

public interface IBuilderMulta {
    IBuilderMulta paraPrestamo(String idPrestamo);
    IBuilderMulta aUsuario(String idUsuario);
    
    IBuilderMulta porRetraso(int diasRetraso, double tarifaDiaria);
    IBuilderMulta porPerdida(double costoMaterial, double recargo);
    IBuilderMulta porDano(java.util.List<com.biblioteca.dominio.objetosvalor.Dano> danos);
    
    Multa construir();
}
