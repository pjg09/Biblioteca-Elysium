package com.biblioteca.dominio.builders.interfaces;

import com.biblioteca.dominio.entidades.Reserva;
import com.biblioteca.dominio.objetosvalor.IdMaterial;
import com.biblioteca.dominio.objetosvalor.IdUsuario;

public interface IBuilderReserva {
    IBuilderReserva paraUsuario(IdUsuario idUsuario);
    IBuilderReserva deMaterial(IdMaterial idMaterial);
    
    IBuilderReserva conId(String id);
    
    IBuilderReserva tipoNormal(String ubicacionBiblioteca);
    IBuilderReserva tipoInterbibliotecaria(String bibliotecaOrigen);
    
    Reserva construir();
}
