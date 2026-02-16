package com.biblioteca.dominio.entidades;

public class MultaPorRetraso extends Multa {
    private int diasRetraso;
    private double tarifaDiaria;
    
    public MultaPorRetraso(String idPrestamo, String idUsuario, int diasRetraso, double tarifaDiaria) {
        super(idPrestamo, idUsuario, "Retraso en devolución - " + diasRetraso + " días");
        this.diasRetraso = diasRetraso;
        this.tarifaDiaria = tarifaDiaria;
    }
    
    public int getDiasRetraso() {
        return diasRetraso;
    }
    
    public double getTarifaDiaria() {
        return tarifaDiaria;
    }
    
    @Override
    public double calcularMontoTotal() {
        return diasRetraso * tarifaDiaria;
    }
}