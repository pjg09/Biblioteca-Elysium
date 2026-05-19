package com.biblioteca.multas.infraestructura.api;

import com.biblioteca.multas.aplicacion.MultaService;
import com.biblioteca.multas.aplicacion.dto.DeudaPendienteDTO;
import com.biblioteca.multas.infraestructura.persistencia.MultaEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class MultaController {

    private final MultaService multaService;

    public MultaController(MultaService multaService) {
        this.multaService = multaService;
    }

    @GetMapping("/multas/{id}")
    public ResponseEntity<MultaEntity> obtenerPorId(@PathVariable String id) {
        Optional<MultaEntity> multa = multaService.obtenerPorId(id);
        return multa
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/multas")
    public ResponseEntity<List<MultaEntity>> obtenerMultas(
            @RequestParam(required = false) String usuarioId,
            @RequestParam(required = false) String estado) {

        List<MultaEntity> multas;
        if (usuarioId != null && !usuarioId.isBlank()) {
            if (estado != null && !estado.isBlank()) {
                multas = multaService.obtenerPorUsuarioYEstado(usuarioId, estado.toUpperCase());
            } else {
                multas = multaService.obtenerPorUsuario(usuarioId);
            }
        } else {
            multas = multaService.obtenerTodas();
        }
        return ResponseEntity.ok(multas);
    }

    @GetMapping("/usuarios/{usuarioId}/deuda-pendiente")
    public ResponseEntity<DeudaPendienteDTO> consultarDeudaPendiente(@PathVariable String usuarioId) {
        DeudaPendienteDTO deuda = multaService.consultarDeudaPendiente(usuarioId);
        return ResponseEntity.ok(deuda);
    }
}
