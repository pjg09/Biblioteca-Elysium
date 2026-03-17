package com.biblioteca.servicios.interfaces;

public interface IServicioReportes {
    String generarEstadisticasGenerales();
    String generarEstadoUsuario(String idUsuario);
    String generarLimitesUsuario();
    String generarPoliticasTiempo();
    String generarReporteCompleto();
}
