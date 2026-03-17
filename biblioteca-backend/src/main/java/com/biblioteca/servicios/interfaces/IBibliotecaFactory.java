package com.biblioteca.servicios.interfaces;

public interface IBibliotecaFactory {
    IPoliticaTiempoService crearPoliticaTiempoService();
    INotificacionService crearNotificacionService();
}
