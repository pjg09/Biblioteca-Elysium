package com.biblioteca.dominio.entidades;

import com.biblioteca.dominio.objetosvalor.IdMaterial;
import com.biblioteca.dominio.objetosvalor.IdUsuario;

public class ReservaNormal extends Reserva {
    private String ubicacionBiblioteca;
    
    public ReservaNormal(String id, IdUsuario idUsuario, IdMaterial idMaterial, String ubicacionBiblioteca) {
        super(id, idUsuario, idMaterial);
        this.ubicacionBiblioteca = ubicacionBiblioteca;
    }
    
    public String getUbicacion() {
        return ubicacionBiblioteca;
    }
}