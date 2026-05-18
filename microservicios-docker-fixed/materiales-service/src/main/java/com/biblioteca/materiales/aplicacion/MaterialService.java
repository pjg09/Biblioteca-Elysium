package com.biblioteca.materiales.aplicacion;

import com.biblioteca.materiales.aplicacion.dto.CrearMaterialRequest;
import com.biblioteca.materiales.aplicacion.dto.DisponibilidadDTO;
import com.biblioteca.materiales.infraestructura.persistencia.MaterialEntity;
import com.biblioteca.materiales.infraestructura.persistencia.MaterialJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MaterialService {

    private static final Logger log = LoggerFactory.getLogger(MaterialService.class);

    private final MaterialJpaRepository materialJpaRepository;

    public MaterialService(MaterialJpaRepository materialJpaRepository) {
        this.materialJpaRepository = materialJpaRepository;
    }

    @Transactional(readOnly = true)
    public Optional<MaterialEntity> obtenerPorId(String id) {
        return materialJpaRepository.findById(id);
    }

    /**
     * Endpoint clave: consultado sincrónicamente por circulacion-service (BC1).
     */
    @Transactional(readOnly = true)
    public DisponibilidadDTO consultarDisponibilidad(String id) {
        return materialJpaRepository.findById(id)
                .map(e -> new DisponibilidadDTO(
                        e.getId(),
                        e.getTitulo(),
                        "DISPONIBLE".equals(e.getEstado()),
                        e.getEstado()))
                .orElse(new DisponibilidadDTO(id, null, false, "NO_ENCONTRADO"));
    }

    @Transactional
    public MaterialEntity agregarMaterial(CrearMaterialRequest req) {
        MaterialEntity entity = new MaterialEntity();
        entity.setId(req.getId());
        entity.setTitulo(req.getTitulo());
        entity.setAutor(req.getAutor());
        entity.setTipo(req.getTipo());
        entity.setPrecio(req.getPrecio());
        entity.setEstado("DISPONIBLE");
        entity.setFechaCreacion(LocalDateTime.now());
        MaterialEntity saved = materialJpaRepository.save(entity);
        log.info("Material creado: id={}, titulo={}", saved.getId(), saved.getTitulo());
        return saved;
    }

    @Transactional
    public Optional<MaterialEntity> actualizarEstado(String id, String nuevoEstado) {
        return materialJpaRepository.findById(id).map(entity -> {
            entity.setEstado(nuevoEstado);
            MaterialEntity saved = materialJpaRepository.save(entity);
            log.info("Estado del material id={} actualizado a {}", id, nuevoEstado);
            return saved;
        });
    }

    @Transactional(readOnly = true)
    public List<MaterialEntity> listarTodos() {
        return materialJpaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<MaterialEntity> listarPorTipo(String tipo) {
        return materialJpaRepository.findByTipo(tipo);
    }
}
