package com.biblioteca.servicios.interfaces;

import com.biblioteca.dominio.enumeraciones.TipoUsuario;
import com.biblioteca.dominio.objetosvalor.ResultadoValidacion;

public interface ILimitePrestamoService {
    ResultadoValidacion validarLimite(String idUsuario, TipoUsuario tipoUsuario);
    int obtenerLimiteMaximo(TipoUsuario tipoUsuario);
    int cantidadActualPrestada(String idUsuario);
}