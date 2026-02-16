package com.biblioteca.servicios.interfaces;

import com.biblioteca.dominio.enumeraciones.TipoUsuario;
import com.biblioteca.dominio.objetosvalor.Resultado;
import com.biblioteca.dominio.objetosvalor.ResultadoValidacion;

public interface IRenovacionService {
    
    ResultadoValidacion validarRenovacion(String idPrestamo);
    
    Resultado renovarPrestamo(String idPrestamo);
    
    int obtenerRenovacionesDisponibles(String idPrestamo, TipoUsuario tipoUsuario);
}