package com.biblioteca.dominio.estados;

import com.biblioteca.dominio.entidades.Prestamo;
import com.biblioteca.dominio.enumeraciones.EstadoTransaccion;
import com.biblioteca.dominio.objetosvalor.Resultado;
import com.biblioteca.dominio.objetosvalor.ResultadoValidacion;

public interface IEstadoPrestamo {
    ResultadoValidacion puedeRenovarse(Prestamo prestamo);
    Resultado devolver(Prestamo prestamo);
    EstadoTransaccion obtenerEstado();
}
