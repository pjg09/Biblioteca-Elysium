package com.biblioteca.servicios.implementaciones;

import com.biblioteca.servicios.interfaces.IBibliotecaFactory;
import com.biblioteca.servicios.interfaces.INotificacionService;
import com.biblioteca.servicios.interfaces.IPoliticaTiempoService;

public class BibliotecaPublicaFactory implements IBibliotecaFactory {
    
    @Override
    public IPoliticaTiempoService crearPoliticaTiempoService() {
        return new PoliticaTiempoPorTipoService();
    }

    @Override
    public INotificacionService crearNotificacionService() {
        return new NotificacionEmailService("smtp.publica.gov");
    }
}
