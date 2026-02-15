package com.biblioteca.dominio.entidades;

import java.time.LocalDateTime;

public class PrestamoNormal extends Prestamo {
    private String ubicacionBiblioteca;
    
    public PrestamoNormal(String idUsuario, String idMaterial, LocalDateTime fechaPrestamo, 
                          LocalDateTime fechaDevolucionEsperada, String ubicacionBiblioteca) {
        super(idUsuario, idMaterial, fechaPrestamo, fechaDevolucionEsperada);
        this.ubicacionBiblioteca = ubicacionBiblioteca;
    }
    
    public String getUbicacion() {
        return ubicacionBiblioteca;
    }
}