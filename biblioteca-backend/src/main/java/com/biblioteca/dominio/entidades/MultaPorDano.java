package com.biblioteca.dominio.entidades;

import java.util.List;

import com.biblioteca.dominio.objetosValor.Dano;
import com.biblioteca.servicios.interfaces.ICalculadorCostoDanoService;

public class MultaPorDano extends Multa {
    private List<Dano> danos;
    private transient ICalculadorCostoDanoService calculadorCosto; // transient para no serializar
    
    public MultaPorDano(String idPrestamo, String idUsuario, List<Dano> danos) {
        super(idPrestamo, idUsuario, "Daños al material");
        this.danos = danos;
    }
    
    public List<Dano> getDanos() {
        return danos;
    }
    
    /**
     * Método para inyectar el calculador de costos
     */
    public void setCalculadorCosto(ICalculadorCostoDanoService calculadorCosto) {
        this.calculadorCosto = calculadorCosto;
    }
    
    @Override
    public double calcularMontoTotal() {
        if (calculadorCosto != null) {
            return calculadorCosto.calcularCostoTotal(danos);
        }
        // Si no hay calculador, retornar 0 (o podrías lanzar una excepción)
        return 0.0;
    }
    
    @Override
    public String toString() {
        return String.format("Multa por Daños - %d daño(s), Total: $%.2f", 
            danos.size(), calcularMontoTotal());
    }
}