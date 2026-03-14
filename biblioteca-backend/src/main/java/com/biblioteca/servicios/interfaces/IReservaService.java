package com.biblioteca.servicios.interfaces;

import com.biblioteca.dominio.objetosvalor.IdMaterial;
import com.biblioteca.dominio.objetosvalor.IdUsuario;
import com.biblioteca.dominio.objetosvalor.Resultado;

public interface IReservaService {
    
    Resultado crearReserva(IdUsuario idUsuario, IdMaterial idMaterial, String tipoReserva);
    
    Resultado cancelarReserva(String idReserva);
    
    Resultado notificarDisponibilidad(String idReserva);
}