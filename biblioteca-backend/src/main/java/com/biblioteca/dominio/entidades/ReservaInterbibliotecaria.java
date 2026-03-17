package com.biblioteca.dominio.entidades;

import com.biblioteca.dominio.objetosvalor.IdMaterial;
import com.biblioteca.dominio.objetosvalor.IdUsuario;
import com.biblioteca.dominio.objetosvalor.IdTransaccion;

public class ReservaInterbibliotecaria extends Reserva {
    private String bibliotecaDestino;
    
    public ReservaInterbibliotecaria(IdTransaccion id, IdUsuario idUsuario, IdMaterial idMaterial, String bibliotecaDestino) {
        super(id, idUsuario, idMaterial);
        this.bibliotecaDestino = bibliotecaDestino;
    }
    
    public String getBibliotecaDestino() {
        return bibliotecaDestino;
    }
}