package com.biblioteca.servicios.interfaces;

public interface IBibliotecaFactory {
    IPrestamoFactory crearPrestamoFactory();
    INotificacionService crearNotificacionService();
}
