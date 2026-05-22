package com.biblioteca.circulacion.api;

import com.biblioteca.circulacion.aplicacion.CirculacionService;
import com.biblioteca.circulacion.aplicacion.dto.DevolverMaterialRequest;
import com.biblioteca.circulacion.aplicacion.dto.RegistrarPrestamoRequest;
import com.biblioteca.circulacion.aplicacion.dto.ResultadoOperacion;
import com.biblioteca.circulacion.infraestructura.persistencia.PrestamoEntity;
import com.biblioteca.circulacion.infraestructura.persistencia.PrestamoJpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/prestamos")
public class CirculacionController {

    private final CirculacionService circulacionService;
    private final PrestamoJpaRepository prestamoRepository;

    public CirculacionController(CirculacionService circulacionService,
                                  PrestamoJpaRepository prestamoRepository) {
        this.circulacionService = circulacionService;
        this.prestamoRepository = prestamoRepository;
    }

    @PostMapping
    public ResponseEntity<ResultadoOperacion> registrarPrestamo(
            @RequestBody RegistrarPrestamoRequest request) {
        ResultadoOperacion resultado = circulacionService.registrarPrestamo(request);
        if (resultado.isExito()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(resultado);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resultado);
    }

    @PostMapping("/{id}/devolucion")
    public ResponseEntity<ResultadoOperacion> registrarDevolucion(
            @PathVariable String id,
            @RequestBody DevolverMaterialRequest request) {
        ResultadoOperacion resultado = circulacionService.registrarDevolucion(id, request);
        if (resultado.isExito()) {
            return ResponseEntity.ok(resultado);
        }
        if (resultado.getMensaje() != null && resultado.getMensaje().contains("no encontrado")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resultado);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resultado);
    }

    @PostMapping("/{id}/renovacion")
    public ResponseEntity<ResultadoOperacion> renovarPrestamo(@PathVariable String id) {
        ResultadoOperacion resultado = circulacionService.renovarPrestamo(id);
        if (resultado.isExito()) {
            return ResponseEntity.ok(resultado);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resultado);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PrestamoEntity> obtenerPrestamo(@PathVariable String id) {
        Optional<PrestamoEntity> entity = prestamoRepository.findById(id);
        return entity.map(ResponseEntity::ok)
                     .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<PrestamoEntity>> listarPrestamos(
            @RequestParam(required = false) String usuarioId) {
        if (usuarioId != null && !usuarioId.isBlank()) {
            return ResponseEntity.ok(prestamoRepository.findByIdUsuario(usuarioId));
        }
        return ResponseEntity.ok(prestamoRepository.findAll());
    }
}
