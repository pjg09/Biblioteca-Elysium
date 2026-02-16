package com.biblioteca.servicios.interfaces;

import com.biblioteca.dominio.objetosvalor.Resultado;

/**
 * Servicio para enviar notificaciones a usuarios.
 * 
 * Respeta SRP: Solo se encarga de notificaciones.
 * Respeta OCP: Podemos cambiar el medio (email, SMS, push) sin afectar a quien lo usa.
 */
public interface INotificacionService {
    
    /**
     * Envía una notificación a un usuario.
     * 
     * @param idUsuario ID del usuario destinatario
     * @param mensaje Contenido del mensaje
     * @return Resultado indicando éxito o fallo
     */
    Resultado enviarNotificacion(String idUsuario, String mensaje);
}