package com.biblioteca.servicios.calculadores;

import com.biblioteca.dominio.entidades.Multa;
import com.biblioteca.dominio.entidades.MultaPorDano;
import com.biblioteca.dominio.enumeraciones.TipoMulta;
import com.biblioteca.dominio.objetosValor.ContextoMulta;
import com.biblioteca.servicios.interfaces.ICalculadorCostoDanoService;
import com.biblioteca.servicios.interfaces.ICalculadorMulta;

public class CalculadorMultaPorDano implements ICalculadorMulta {
    private ICalculadorCostoDanoService calculadorCosto;
    
    public CalculadorMultaPorDano(ICalculadorCostoDanoService calculadorCosto) {
        this.calculadorCosto = calculadorCosto;
    }
    
    @Override
    public Multa calcular(ContextoMulta contexto) {
        if (contexto.getEvaluacion() == null || !contexto.getEvaluacion().tieneDanos()) {
            return null;
        }
        
        MultaPorDano multa = new MultaPorDano(
            contexto.getIdPrestamo(),
            contexto.getIdUsuario(),
            contexto.getEvaluacion().getDanos()
        );
        
        return multa;
    }
    
    @Override
    public boolean puedeCalcular(ContextoMulta contexto) {
        return contexto.getTipoMulta() == TipoMulta.POR_DANO;
    }
}