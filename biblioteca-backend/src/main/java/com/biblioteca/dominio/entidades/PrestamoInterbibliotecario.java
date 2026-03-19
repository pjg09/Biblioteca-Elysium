package com.biblioteca.dominio.entidades;

import java.time.LocalDateTime;

public class PrestamoInterbibliotecario extends Prestamo {
    private String bibliotecaOrigen;
    private String bibliotecaDestino;
    private double costoTransferencia;
    
    public PrestamoInterbibliotecario(String id, String idUsuario, String idMaterial, 
                                       LocalDateTime fechaDevolucionEsperada, String bibliotecaOrigen,
                                       String bibliotecaDestino, double costoTransferencia) {
        super(id, idUsuario, idMaterial, fechaDevolucionEsperada);
        
        if (bibliotecaOrigen == null || bibliotecaOrigen.trim().isEmpty()) {
            throw new IllegalArgumentException("Biblioteca origen no puede ser nula o vacía");
        }
        if (bibliotecaDestino == null || bibliotecaDestino.trim().isEmpty()) {
            throw new IllegalArgumentException("Biblioteca destino no puede ser nula o vacía");
        }
        
        this.bibliotecaOrigen = bibliotecaOrigen;
        this.bibliotecaDestino = bibliotecaDestino;
        this.costoTransferencia = costoTransferencia;
    }
    
    public String getBibliotecaOrigen() {
        return bibliotecaOrigen;
    }
    
    public String getBibliotecaDestino() {
        return bibliotecaDestino;
    }
    
    public double getCostoTransferencia() {
        return costoTransferencia;
    }
}