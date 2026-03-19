package com.biblioteca.dominio.entidades;

public class ReservaNormal extends Reserva {
    private String ubicacionBiblioteca;
    
    public ReservaNormal(String id, String idUsuario, String idMaterial, String ubicacionBiblioteca) {
        super(id, idUsuario, idMaterial);
        this.ubicacionBiblioteca = ubicacionBiblioteca;
    }
    
    public String getUbicacion() {
        return ubicacionBiblioteca;
    }
}