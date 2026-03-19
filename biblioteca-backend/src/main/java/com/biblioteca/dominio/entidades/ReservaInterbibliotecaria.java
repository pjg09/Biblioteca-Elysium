package com.biblioteca.dominio.entidades;

public class ReservaInterbibliotecaria extends Reserva {
    private String bibliotecaDestino;
    
    public ReservaInterbibliotecaria(String id, String idUsuario, String idMaterial, String bibliotecaDestino) {
        super(id, idUsuario, idMaterial);
        this.bibliotecaDestino = bibliotecaDestino;
    }
    
    public String getBibliotecaDestino() {
        return bibliotecaDestino;
    }
}