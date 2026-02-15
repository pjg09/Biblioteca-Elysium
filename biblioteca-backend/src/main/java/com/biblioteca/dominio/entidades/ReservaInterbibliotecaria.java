package com.biblioteca.dominio.entidades;

public class ReservaInterbibliotecaria extends Reserva {
    private String bibliotecaDestino;
    
    public ReservaInterbibliotecaria(String idUsuario, String idMaterial, String bibliotecaDestino) {
        super(idUsuario, idMaterial);
        this.bibliotecaDestino = bibliotecaDestino;
    }
    
    public String getBibliotecaDestino() {
        return bibliotecaDestino;
    }
}