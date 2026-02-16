package com.biblioteca.dominio.entidades;

public class MultaPorPerdida extends Multa {
    private double valorMaterial;
    private double porcentajeRecargo;
    
    public MultaPorPerdida(String idPrestamo, String idUsuario, double valorMaterial, double porcentajeRecargo) {
        super(idPrestamo, idUsuario, "Pérdida de material");
        this.valorMaterial = valorMaterial;
        this.porcentajeRecargo = porcentajeRecargo;
    }
    
    public double getValorMaterial() {
        return valorMaterial;
    }
    
    public double getPorcentajeRecargo() {
        return porcentajeRecargo;
    }
    
    @Override
    public double calcularMontoTotal() {
        return valorMaterial * (1 + porcentajeRecargo);
    }
    
    @Override
    public String toString() {
        return String.format("Multa por Pérdida - Material: $%.2f, Recargo: %.0f%%, Total: $%.2f", 
            valorMaterial, porcentajeRecargo * 100, calcularMontoTotal());
    }
}