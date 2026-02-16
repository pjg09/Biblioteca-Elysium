package com.biblioteca.servicios.interfaces;

import com.biblioteca.dominio.objetosvalor.Resultado;

public interface IReservaService {
    
    Resultado crearReserva(String idUsuario, String idMaterial, String tipoReserva);
    
    Resultado cancelarReserva(String idReserva);
    
    Resultado notificarDisponibilidad(String idReserva);
}