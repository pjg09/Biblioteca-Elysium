package com.biblioteca.servicios.implementaciones;

import com.biblioteca.dominio.objetosvalor.Resultado;
import com.biblioteca.servicios.interfaces.INotificacionService;

/**
 * Implementación simple de notificaciones (simulación por consola).
 * En producción, esto se conectaría a un servidor SMTP real.
 * 
 * Respeta SRP: Solo envía notificaciones.
 */
public class NotificacionEmailService implements INotificacionService {
    
    private final String servidorSMTP;

    public NotificacionEmailService(String servidorSMTP) {
        this.servidorSMTP = servidorSMTP != null ? servidorSMTP : "smtp.biblioteca.com";
    }

    @Override
    public Resultado enviarNotificacion(String idUsuario, String mensaje) {
        try {
            // Simulación de envío de email
            System.out.println("===========================================");
            System.out.println("📧 NOTIFICACIÓN ENVIADA");
            System.out.println("Servidor SMTP: " + servidorSMTP);
            System.out.println("Destinatario (ID): " + idUsuario);
            System.out.println("Mensaje: " + mensaje);
            System.out.println("===========================================");
            
            return Resultado.Exitoso("Notificación enviada exitosamente", null);
            
        } catch (Exception e) {
            return Resultado.Fallido("Error al enviar notificación: " + e.getMessage());
        }
    }
}