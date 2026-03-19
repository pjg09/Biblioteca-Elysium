package com.biblioteca.servicios.implementaciones;

import com.biblioteca.dominio.objetosvalor.Resultado;
import com.biblioteca.servicios.interfaces.INotificacionService;

public class NotificacionEmailService implements INotificacionService {

    private final String servidorSMTP;

    // Constructor con parámetro
    public NotificacionEmailService(String servidorSMTP) {
        this.servidorSMTP = servidorSMTP != null ? servidorSMTP : "smtp.biblioteca.com";
    }

    public NotificacionEmailService() {
        this.servidorSMTP = "smtp.biblioteca.com";
    }

    @Override
    public Resultado enviarNotificacion(String idUsuario, String mensaje) {
        try {
            System.out.println("NOTIFICACIÓN ENVIADA");
            System.out.println("Servidor SMTP: " + servidorSMTP);
            System.out.println("Destinatario (ID): " + idUsuario);
            System.out.println("Mensaje: " + mensaje);

            return Resultado.Exitoso("Notificación enviada exitosamente", null);

        } catch (Exception e) {
            return Resultado.Fallido("Error al enviar notificación: " + e.getMessage());
        }
    }
}