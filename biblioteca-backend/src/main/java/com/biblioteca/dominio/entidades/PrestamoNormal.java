package com.biblioteca.dominio.entidades;

import java.time.LocalDateTime;

public class PrestamoNormal extends Prestamo {
    private String ubicacionBiblioteca;
    
    public PrestamoNormal(String id, String idUsuario, String idMaterial, 
                          LocalDateTime fechaDevolucionEsperada, String ubicacionBiblioteca) {
        super(id, idUsuario, idMaterial, fechaDevolucionEsperada);
        
        if (ubicacionBiblioteca == null || ubicacionBiblioteca.trim().isEmpty()) {
            throw new IllegalArgumentException("Ubicación de biblioteca no puede ser nula o vacía");
        }
        this.ubicacionBiblioteca = ubicacionBiblioteca;
    }
    
    public String getUbicacion() {
        return ubicacionBiblioteca;
    }
}