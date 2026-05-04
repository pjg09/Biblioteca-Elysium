package com.biblioteca.notificaciones.infraestructura.api;

import com.biblioteca.notificaciones.dominio.Notificacion;
import com.biblioteca.notificaciones.infraestructura.persistencia.NotificacionRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/notificaciones")
public class NotificacionController {

    private final NotificacionRepository notificacionRepository;

    public NotificacionController(NotificacionRepository notificacionRepository) {
        this.notificacionRepository = notificacionRepository;
    }

    @GetMapping
    public ResponseEntity<List<Notificacion>> obtenerNotificaciones(
            @RequestParam String usuarioId,
            @RequestParam(required = false, defaultValue = "false") boolean noLeidas) {

        List<Notificacion> notificaciones;
        if (noLeidas) {
            notificaciones = notificacionRepository.findByUsuarioIdAndLeida(usuarioId, false);
        } else {
            notificaciones = notificacionRepository.findByUsuarioIdOrderByFechaCreacionDesc(usuarioId);
        }
        return ResponseEntity.ok(notificaciones);
    }

    @PutMapping("/{id}/leer")
    public ResponseEntity<Void> marcarLeida(@PathVariable String id) {
        return notificacionRepository.findById(id)
                .map(notificacion -> {
                    notificacion.marcarLeida();
                    notificacionRepository.save(notificacion);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElseGet(() -> ResponseEntity.notFound().<Void>build());
    }
}
