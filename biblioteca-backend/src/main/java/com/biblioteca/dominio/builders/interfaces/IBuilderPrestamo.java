package com.biblioteca.dominio.builders.interfaces;

import java.time.LocalDateTime;

import com.biblioteca.dominio.entidades.Prestamo;
import com.biblioteca.dominio.objetosvalor.IdMaterial;
import com.biblioteca.dominio.objetosvalor.IdUsuario;

public interface IBuilderPrestamo {
    IBuilderPrestamo paraUsuario(IdUsuario idUsuario);
    IBuilderPrestamo deMaterial(IdMaterial idMaterial);
    
    IBuilderPrestamo conId(String id);
    IBuilderPrestamo conVencimiento(LocalDateTime fechaDevolucion);
    IBuilderPrestamo porDias(int dias);
    IBuilderPrestamo enUbicacion(String ubicacion);
    
    IBuilderPrestamo tipoNormal();
    IBuilderPrestamo tipoInterbibliotecario(String origen, String destino, double costo);
    
    Prestamo construir();
}
