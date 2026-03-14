package com.biblioteca.servicios.implementaciones;

import com.biblioteca.servicios.interfaces.IBibliotecaFactory;
import com.biblioteca.servicios.interfaces.INotificacionService;
import com.biblioteca.servicios.interfaces.IPrestamoFactory;

public class BibliotecaUniversitariaFactory implements IBibliotecaFactory {
    
    @Override
    public IPrestamoFactory crearPrestamoFactory() {
        return new PrestamoInterbibliotecarioFactory(
            "Biblioteca Central Universitaria",
            "Biblioteca de Facultad",
            0.0 // Gratis para universitarios
        );
    }

    @Override
    public INotificacionService crearNotificacionService() {
        return new NotificacionEmailService("smtp.universidad.edu");
    }
}
