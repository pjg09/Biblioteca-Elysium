package com.biblioteca.servicios.interfaces;

import java.util.List;

import com.biblioteca.dominio.entidades.Prestamo;
import com.biblioteca.dominio.objetosvalor.Resultado;

public interface IPrestamoService {
    
    Resultado registrarPrestamo(String idUsuario, String idMaterial, String tipoPrestamo);
    

    List<Prestamo> obtenerPrestamosActivos(String idUsuario);
    

    Prestamo obtenerPrestamoPorId(String idPrestamo);
}