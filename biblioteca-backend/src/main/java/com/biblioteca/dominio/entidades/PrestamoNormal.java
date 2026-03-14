package com.biblioteca.dominio.entidades;

import java.time.LocalDateTime;

import com.biblioteca.dominio.objetosvalor.IdUsuario;
import com.biblioteca.dominio.objetosvalor.IdMaterial;

public class PrestamoNormal extends Prestamo {
    private String ubicacionBiblioteca;
    
    public PrestamoNormal(String id, IdUsuario idUsuario, IdMaterial idMaterial, 
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