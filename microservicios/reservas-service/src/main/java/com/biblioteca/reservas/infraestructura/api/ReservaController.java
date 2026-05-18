package com.biblioteca.reservas.infraestructura.api;

import com.biblioteca.reservas.aplicacion.ReservaService;
import com.biblioteca.reservas.aplicacion.dto.CrearReservaRequest;
import com.biblioteca.reservas.infraestructura.persistencia.ReservaEntity;
import com.biblioteca.commons.excepciones.OperacionNoPermitidaEnEstadoReservaException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/reservas")
public class ReservaController {

    private final ReservaService reservaService;

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    /**
     * POST /reservas
     * Crea una nueva reserva en la cola de espera para un material.
     */
    @PostMapping
    public ResponseEntity<ReservaEntity> crearReserva(@RequestBody CrearReservaRequest request) {
        ReservaEntity created = reservaService.crearReserva(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * DELETE /reservas/{id}
     * Cancela una reserva existente y reorganiza la cola.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ReservaEntity> cancelarReserva(@PathVariable String id) throws OperacionNoPermitidaEnEstadoReservaException {
        try {
            ReservaEntity cancelled = reservaService.cancelarReserva(id);
            return ResponseEntity.ok(cancelled);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * GET /reservas/{id}
     * Obtiene una reserva por su identificador.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ReservaEntity> obtenerReserva(@PathVariable String id) {
        Optional<ReservaEntity> reserva = reservaService.obtenerPorId(id);
        return reserva
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * GET /reservas?materialId={id}   → reservas activas de un material ordenadas por posición
     * GET /reservas?usuarioId={id}    → reservas activas de un usuario
     */
    @GetMapping
    public ResponseEntity<List<ReservaEntity>> listarReservas(
            @RequestParam(required = false) String materialId,
            @RequestParam(required = false) String usuarioId) {

        if (materialId != null && !materialId.isBlank()) {
            List<ReservaEntity> lista = reservaService.listarPorMaterial(materialId);
            return ResponseEntity.ok(lista);
        }

        if (usuarioId != null && !usuarioId.isBlank()) {
            List<ReservaEntity> lista = reservaService.listarPorUsuario(usuarioId);
            return ResponseEntity.ok(lista);
        }

        return ResponseEntity.badRequest().build();
    }
}
