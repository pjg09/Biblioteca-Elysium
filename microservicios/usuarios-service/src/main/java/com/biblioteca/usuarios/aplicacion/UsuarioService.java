package com.biblioteca.usuarios.aplicacion;

import com.biblioteca.usuarios.aplicacion.dto.CrearUsuarioRequest;
import com.biblioteca.usuarios.aplicacion.dto.EstadoUsuarioDTO;
import com.biblioteca.usuarios.aplicacion.dto.LimitePrestamoDTO;
import com.biblioteca.usuarios.dominio.Usuario;
import com.biblioteca.usuarios.dominio.builders.UsuarioBuilder;
import com.biblioteca.usuarios.infraestructura.persistencia.UsuarioEntity;
import com.biblioteca.usuarios.infraestructura.persistencia.UsuarioJpaRepository;
import com.biblioteca.commons.objetosvalor.Resultado;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private static final Logger log = LoggerFactory.getLogger(UsuarioService.class);

    private final UsuarioJpaRepository usuarioJpaRepository;

    public UsuarioService(UsuarioJpaRepository usuarioJpaRepository) {
        this.usuarioJpaRepository = usuarioJpaRepository;
    }

    @Transactional(readOnly = true)
    public Optional<UsuarioEntity> obtenerPorId(String id) {
        return usuarioJpaRepository.findById(id);
    }

    /**
     * Endpoint clave consultado sincrónicamente por circulacion-service (BC1)
     * para verificar si un usuario puede realizar un préstamo.
     */
    @Transactional(readOnly = true)
    public EstadoUsuarioDTO consultarEstado(String id) {
        return usuarioJpaRepository.findById(id)
                .map(e -> new EstadoUsuarioDTO(
                        e.getId(),
                        e.getNombre(),
                        "ACTIVO".equals(e.getEstadoUsuario()),
                        e.getEstadoUsuario(),
                        e.getTipoUsuario()))
                .orElse(new EstadoUsuarioDTO(id, null, false, "NO_ENCONTRADO", null));
    }

    /**
     * Retorna el límite de préstamos del usuario.
     * Usado por circulacion-service para validar cuota máxima.
     */
    @Transactional(readOnly = true)
    public LimitePrestamoDTO consultarLimite(String id) {
        return usuarioJpaRepository.findById(id)
                .map(e -> new LimitePrestamoDTO(
                        e.getId(),
                        e.getTipoUsuario(),
                        e.getLimiteMaximoPrestamos()))
                .orElse(new LimitePrestamoDTO(id, null, 0));
    }

    private int limitePorTipo(String tipo) {
        return switch (tipo == null ? "" : tipo.toUpperCase()) {
            case "PROFESOR"        -> 10;
            case "INVESTIGADOR"    -> 15;
            case "PUBLICO_GENERAL" -> 3;
            default                -> 5; // ESTUDIANTE y cualquier otro
        };
    }

    @Transactional
    public UsuarioEntity registrarUsuario(CrearUsuarioRequest req) {
        if (req.getId() != null && !req.getId().isBlank() && usuarioJpaRepository.existsById(req.getId())) {
            throw new IllegalArgumentException("Ya existe un usuario con el ID: " + req.getId() + ". Use otro ID u opción de edición.");
        }

        int limite = limitePorTipo(req.getTipoUsuario());

        UsuarioBuilder builder = new UsuarioBuilder()
                .conId(req.getId())
                .conNombre(req.getNombre())
                .conEmail(req.getEmail())
                .conTipoUsuario(req.getTipoUsuario())
                .conLimiteMaximoPrestamos(limite);

        Resultado<Usuario> resultado = builder.construir();

        if (resultado.esError()) {
            // En un servicio REST, esto debería lanzar una excepción apropiada
            throw new IllegalArgumentException(resultado.getMensajeError());
        }

        // Usuario válido construido, convertir a Entity para persistir
        Usuario usuarioValido = resultado.getValor();
        UsuarioEntity entity = new UsuarioEntity();
        entity.setId(usuarioValido.getId());
        entity.setNombre(usuarioValido.getNombre());
        entity.setEmail(usuarioValido.getEmail());
        entity.setTipoUsuario(usuarioValido.getTipoUsuario());
        entity.setEstadoUsuario(usuarioValido.getEstadoUsuario());
        entity.setLimiteMaximoPrestamos(usuarioValido.getLimiteMaximoPrestamos());
        entity.setFechaCreacion(LocalDateTime.now());

        UsuarioEntity saved = usuarioJpaRepository.save(entity);
        log.info("Usuario registrado con validaciones de dominio: id={}, nombre={}", saved.getId(), saved.getNombre());
        return saved;
    }

    @Transactional(readOnly = true)
    public List<UsuarioEntity> listarTodos() {
        return usuarioJpaRepository.findAll();
    }
}
