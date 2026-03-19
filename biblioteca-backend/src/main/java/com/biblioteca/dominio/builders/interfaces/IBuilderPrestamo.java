package com.biblioteca.dominio.builders.interfaces;

import java.time.LocalDateTime;

import com.biblioteca.dominio.entidades.Prestamo;
public interface IBuilderPrestamo {
    IBuilderPrestamo paraUsuario(String idUsuario);
    IBuilderPrestamo deMaterial(String idMaterial);
    
    IBuilderPrestamo conId(String id);
    IBuilderPrestamo conVencimiento(LocalDateTime fechaDevolucion);
    IBuilderPrestamo porDias(int dias);
    IBuilderPrestamo enUbicacion(String ubicacion);
    
    IBuilderPrestamo tipoNormal();
    IBuilderPrestamo tipoInterbibliotecario(String origen, String destino, double costo);
    
    Prestamo construir();
}
