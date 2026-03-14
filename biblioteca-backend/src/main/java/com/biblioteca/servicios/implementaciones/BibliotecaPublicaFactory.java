package com.biblioteca.servicios.implementaciones;

import com.biblioteca.servicios.interfaces.IBibliotecaFactory;
import com.biblioteca.servicios.interfaces.INotificacionService;
import com.biblioteca.servicios.interfaces.IPrestamoFactory;

public class BibliotecaPublicaFactory implements IBibliotecaFactory {
    
    @Override
    public IPrestamoFactory crearPrestamoFactory() {
        return new PrestamoNormalFactory("Sede Pública Principal");
    }

    @Override
    public INotificacionService crearNotificacionService() {
        return new NotificacionEmailService("smtp.publica.gov");
    }
}
