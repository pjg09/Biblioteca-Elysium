package com.biblioteca.servicios.implementaciones;

import com.biblioteca.dominio.objetosvalor.Resultado;
import com.biblioteca.servicios.interfaces.INotificacionService;
import java.util.logging.Logger;

public class NotificacionConLoggingDecorator implements INotificacionService {
    private final INotificacionService servicioBase;
    private final Logger logger;
    
    public NotificacionConLoggingDecorator(INotificacionService servicioBase) {
        this.servicioBase = servicioBase;
        this.logger = Logger.getLogger(this.getClass().getName());
    }
    
    @Override
    public Resultado enviarNotificacion(String idUsuario, String mensaje) {
        logger.info("Iniciando envío de notificación a usuario: " + idUsuario);
        
        Resultado resultado = servicioBase.enviarNotificacion(idUsuario, mensaje);
        
        if (resultado.getExito()) {
            logger.info("Notificación enviada exitosamente");
        } else {
            logger.severe("Error al enviar notificación: " + resultado.getMensaje());
        }
        
        return resultado;
    }
}
