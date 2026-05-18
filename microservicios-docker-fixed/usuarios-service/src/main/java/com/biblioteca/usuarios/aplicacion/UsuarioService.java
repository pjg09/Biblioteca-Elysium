package com.biblioteca.usuarios.aplicacion;

import com.biblioteca.usuarios.aplicacion.dto.CrearUsuarioRequest;
import com.biblioteca.usuarios.aplicacion.dto.EstadoUsuarioDTO;
import com.biblioteca.usuarios.aplicacion.dto.LimitePrestamoDTO;
import com.biblioteca.usuarios.infraestructura.persistencia.UsuarioEntity;
import com.biblioteca.usuarios.infraestructura.persistencia.UsuarioJpaRepository;
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

    @Transactional
    public UsuarioEntity registrarUsuario(CrearUsuarioRequest req) {
        UsuarioEntity entity = new UsuarioEntity();
        entity.setId(req.getId());
        entity.setNombre(req.getNombre());
        entity.setEmail(req.getEmail());
        entity.setTipoUsuario(req.getTipoUsuario());
        entity.setEstadoUsuario("ACTIVO");
        entity.setLimiteMaximoPrestamos(req.getLimiteMaximoPrestamos());
        entity.setFechaCreacion(LocalDateTime.now());
        UsuarioEntity saved = usuarioJpaRepository.save(entity);
        log.info("Usuario registrado: id={}, nombre={}", saved.getId(), saved.getNombre());
        return saved;
    }

    @Transactional(readOnly = true)
    public List<UsuarioEntity> listarTodos() {
        return usuarioJpaRepository.findAll();
    }
}
