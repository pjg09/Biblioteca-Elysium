package com.biblioteca.materiales.api;

import com.biblioteca.materiales.aplicacion.MaterialService;
import com.biblioteca.materiales.aplicacion.dto.CrearMaterialRequest;
import com.biblioteca.materiales.aplicacion.dto.DisponibilidadDTO;
import com.biblioteca.materiales.infraestructura.persistencia.MaterialEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/materiales")
public class MaterialController {

    private final MaterialService materialService;

    public MaterialController(MaterialService materialService) {
        this.materialService = materialService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<MaterialEntity> obtenerPorId(@PathVariable String id) {
        return materialService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/disponibilidad")
    public ResponseEntity<DisponibilidadDTO> consultarDisponibilidad(@PathVariable String id) {
        DisponibilidadDTO dto = materialService.consultarDisponibilidad(id);
        if ("NO_ENCONTRADO".equals(dto.getEstado())) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dto);
    }

    @GetMapping
    public ResponseEntity<List<MaterialEntity>> listar(@RequestParam(required = false) String tipo) {
        List<MaterialEntity> result = (tipo != null && !tipo.isBlank())
                ? materialService.listarPorTipo(tipo)
                : materialService.listarTodos();
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody CrearMaterialRequest request) {
        MaterialEntity created = materialService.agregarMaterial(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleValidacion(IllegalArgumentException e) {
        Map<String, String> error = new HashMap<>();
        error.put("error", e.getMessage());
        return ResponseEntity.badRequest().body(error);
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<MaterialEntity> actualizarEstado(
            @PathVariable String id,
            @RequestBody Map<String, String> body) {
        String nuevoEstado = body.get("estado");
        if (nuevoEstado == null || nuevoEstado.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        return materialService.actualizarEstado(id, nuevoEstado)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
