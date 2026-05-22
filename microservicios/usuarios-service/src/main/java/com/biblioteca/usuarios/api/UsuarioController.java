package com.biblioteca.usuarios.api;

import com.biblioteca.usuarios.aplicacion.UsuarioService;
import com.biblioteca.usuarios.aplicacion.dto.CrearUsuarioRequest;
import com.biblioteca.usuarios.aplicacion.dto.EstadoUsuarioDTO;
import com.biblioteca.usuarios.aplicacion.dto.LimitePrestamoDTO;
import com.biblioteca.usuarios.infraestructura.persistencia.UsuarioEntity;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioEntity> obtenerPorId(@PathVariable String id) {
        return usuarioService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/estado")
    public ResponseEntity<EstadoUsuarioDTO> consultarEstado(@PathVariable String id) {
        EstadoUsuarioDTO dto = usuarioService.consultarEstado(id);
        if ("NO_ENCONTRADO".equals(dto.getEstadoUsuario())) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/{id}/limite-prestamos")
    public ResponseEntity<LimitePrestamoDTO> consultarLimite(@PathVariable String id) {
        LimitePrestamoDTO dto = usuarioService.consultarLimite(id);
        if (dto.getTipoUsuario() == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dto);
    }

    @GetMapping
    public ResponseEntity<List<UsuarioEntity>> listarTodos() {
        return ResponseEntity.ok(usuarioService.listarTodos());
    }

    @PostMapping
    public ResponseEntity<?> registrar(@RequestBody CrearUsuarioRequest request) {
        UsuarioEntity created = usuarioService.registrarUsuario(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleValidacion(IllegalArgumentException e) {
        Map<String, String> error = new HashMap<>();
        error.put("error", e.getMessage());
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleDuplicado(DataIntegrityViolationException e) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Ya existe un usuario con ese ID o email");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }
}
