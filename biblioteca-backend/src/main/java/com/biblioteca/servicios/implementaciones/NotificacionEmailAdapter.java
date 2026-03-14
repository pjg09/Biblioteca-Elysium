package com.biblioteca.servicios.implementaciones;

import com.biblioteca.dominio.entidades.Usuario;
import com.biblioteca.dominio.objetosvalor.Resultado;
import com.biblioteca.repositorios.IRepositorio;
import com.biblioteca.servicios.externos.SistemaEmailExterno;
import com.biblioteca.servicios.interfaces.INotificacionService;

public class NotificacionEmailAdapter implements INotificacionService {
    private final SistemaEmailExterno sistemaExterno;
    private final IRepositorio<Usuario> repoUsuario;
    
    public NotificacionEmailAdapter(IRepositorio<Usuario> repoUsuario) {
        this.sistemaExterno = new SistemaEmailExterno();
        this.repoUsuario = repoUsuario;
    }
    
    @Override
    public Resultado enviarNotificacion(String idUsuario, String mensaje) {
        Usuario usuario = repoUsuario.obtenerPorId(idUsuario);
        
        if (usuario == null) {
            return Resultado.Fallido("Usuario no encontrado para notificación");
        }
        
        try {
            sistemaExterno.enviarCorreo(
                usuario.getEmail(),
                "Notificación de Biblioteca",
                mensaje
            );
            return Resultado.Exitoso("Notificación enviada vía Adapter", null);
        } catch (Exception e) {
            return Resultado.Fallido("Error al enviar notificación: " + e.getMessage());
        }
    }
}
