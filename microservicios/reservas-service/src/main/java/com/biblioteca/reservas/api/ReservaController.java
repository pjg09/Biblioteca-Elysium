package com.biblioteca.reservas.api;

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

    @PostMapping
    public ResponseEntity<ReservaEntity> crearReserva(@RequestBody CrearReservaRequest request) {
        ReservaEntity created = reservaService.crearReserva(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

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

    @GetMapping("/{id}")
    public ResponseEntity<ReservaEntity> obtenerReserva(@PathVariable String id) {
        Optional<ReservaEntity> reserva = reservaService.obtenerPorId(id);
        return reserva
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<ReservaEntity>> listarReservas(
            @RequestParam(required = false) String materialId,
            @RequestParam(required = false) String usuarioId) {

        if (materialId != null && !materialId.isBlank()) {
            return ResponseEntity.ok(reservaService.listarPorMaterial(materialId));
        }

        if (usuarioId != null && !usuarioId.isBlank()) {
            return ResponseEntity.ok(reservaService.listarPorUsuario(usuarioId));
        }

        return ResponseEntity.ok(reservaService.listarTodas());
    }
}
