package com.biblioteca.cobros.infraestructura.api;

import com.biblioteca.cobros.aplicacion.CobroService;
import com.biblioteca.cobros.aplicacion.dto.RegistrarPagoRequest;
import com.biblioteca.cobros.infraestructura.persistencia.RegistroPagoEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class CobroController {

    private final CobroService cobroService;

    public CobroController(CobroService cobroService) {
        this.cobroService = cobroService;
    }

    @PostMapping("/pagos")
    public ResponseEntity<RegistroPagoEntity> registrarPago(@RequestBody RegistrarPagoRequest request) {
        RegistroPagoEntity resultado = cobroService.registrarPago(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(resultado);
    }

    @GetMapping("/pagos/{id}")
    public ResponseEntity<RegistroPagoEntity> obtenerPorId(@PathVariable String id) {
        Optional<RegistroPagoEntity> pago = cobroService.obtenerPorId(id);
        return pago
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/pagos")
    public ResponseEntity<List<RegistroPagoEntity>> obtenerPagos(
            @RequestParam(required = false) String usuarioId) {
        List<RegistroPagoEntity> pagos = (usuarioId != null && !usuarioId.isBlank())
                ? cobroService.obtenerPorUsuario(usuarioId)
                : cobroService.obtenerTodos();
        return ResponseEntity.ok(pagos);
    }
}
