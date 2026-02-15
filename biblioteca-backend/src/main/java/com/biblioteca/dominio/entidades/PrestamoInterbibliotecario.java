package com.biblioteca.dominio.entidades;

import java.time.LocalDateTime;

public class PrestamoInterbibliotecario extends Prestamo {
    private String bibliotecaOrigen;
    private String bibliotecaDestino;
    private double costoTransferencia;
    
    public PrestamoInterbibliotecario(String idUsuario, String idMaterial, LocalDateTime fechaPrestamo,
                                       LocalDateTime fechaDevolucionEsperada, String bibliotecaOrigen,
                                       String bibliotecaDestino, double costoTransferencia) {
        super(idUsuario, idMaterial, fechaPrestamo, fechaDevolucionEsperada);
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