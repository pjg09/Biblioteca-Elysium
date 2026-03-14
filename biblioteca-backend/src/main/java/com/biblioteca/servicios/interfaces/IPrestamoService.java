package com.biblioteca.servicios.interfaces;

import java.util.List;

import com.biblioteca.dominio.entidades.Prestamo;
import com.biblioteca.dominio.objetosvalor.IdMaterial;
import com.biblioteca.dominio.objetosvalor.IdUsuario;
import com.biblioteca.dominio.objetosvalor.Resultado;

public interface IPrestamoService {
    
    Resultado registrarPrestamo(IdUsuario idUsuario, IdMaterial idMaterial, String tipoPrestamo);
    
    List<Prestamo> obtenerPrestamosActivos(IdUsuario idUsuario);
    
    Prestamo obtenerPrestamoPorId(String idPrestamo);
}