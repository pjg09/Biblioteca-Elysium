package com.biblioteca.dominio.entidades;

public class ReservaNormal extends Reserva {
    private String ubicacionBiblioteca;
    
    public ReservaNormal(String idUsuario, String idMaterial, String ubicacionBiblioteca) {
        super(idUsuario, idMaterial);
        this.ubicacionBiblioteca = ubicacionBiblioteca;
    }
    
    public String getUbicacion() {
        return ubicacionBiblioteca;
    }
}