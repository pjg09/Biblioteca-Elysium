package com.biblioteca.prestamosexternos.api;

import com.biblioteca.prestamosexternos.aplicacion.SolicitudExternaService;
import com.biblioteca.prestamosexternos.aplicacion.dto.CrearSolicitudRequest;
import com.biblioteca.prestamosexternos.infraestructura.persistencia.SolicitudExternaEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/solicitudes-externas")
public class SolicitudExternaController {

    private final SolicitudExternaService solicitudService;

    public SolicitudExternaController(SolicitudExternaService solicitudService) {
        this.solicitudService = solicitudService;
    }

    @PostMapping
    public ResponseEntity<SolicitudExternaEntity> crearSolicitud(
            @RequestBody CrearSolicitudRequest request) {
        SolicitudExternaEntity created = solicitudService.crearSolicitud(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SolicitudExternaEntity> obtenerSolicitud(@PathVariable String id) {
        Optional<SolicitudExternaEntity> solicitud = solicitudService.obtenerPorId(id);
        return solicitud
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<?> listar(
            @RequestParam(required = false) String prestamoId,
            @RequestParam(required = false) String usuarioId) {

        if (prestamoId != null && !prestamoId.isBlank()) {
            Optional<SolicitudExternaEntity> result = solicitudService.obtenerPorPrestamoId(prestamoId);
            return result
                    .<ResponseEntity<?>>map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        }

        if (usuarioId != null && !usuarioId.isBlank()) {
            List<SolicitudExternaEntity> lista = solicitudService.listarPorUsuario(usuarioId);
            return ResponseEntity.ok(lista);
        }

        return ResponseEntity.badRequest()
                .body("Debe proporcionar el parámetro 'prestamoId' o 'usuarioId'.");
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<SolicitudExternaEntity> actualizarEstado(
            @PathVariable String id,
            @RequestBody Map<String, String> body) {

        String nuevoEstado = body.get("estado");
        if (nuevoEstado == null || nuevoEstado.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        try {
            SolicitudExternaEntity updated = solicitudService.actualizarEstado(id, nuevoEstado);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
