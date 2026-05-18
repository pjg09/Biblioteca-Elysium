package com.biblioteca.prestamosexternos.aplicacion;

import com.biblioteca.prestamosexternos.aplicacion.dto.CrearSolicitudRequest;
import com.biblioteca.prestamosexternos.dominio.SolicitudExterna;
import com.biblioteca.prestamosexternos.infraestructura.persistencia.SolicitudExternaEntity;
import com.biblioteca.prestamosexternos.infraestructura.persistencia.SolicitudExternaJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class SolicitudExternaService {

    private static final Logger log = LoggerFactory.getLogger(SolicitudExternaService.class);

    private final SolicitudExternaJpaRepository solicitudRepo;

    public SolicitudExternaService(SolicitudExternaJpaRepository solicitudRepo) {
        this.solicitudRepo = solicitudRepo;
    }

    /**
     * Crea una nueva solicitud de préstamo interbibliotecario.
     * Llamado sincrónicamente por circulacion-service cuando tipo=INTERBIBLIOTECARIO.
     */
    @Transactional
    public SolicitudExternaEntity crearSolicitud(CrearSolicitudRequest req) {
        SolicitudExterna dominio = SolicitudExterna.crear(
                req.getPrestamoId(),
                req.getIdUsuario(),
                req.getIdMaterial(),
                req.getBibliotecaOrigen(),
                req.getBibliotecaDestino(),
                req.getCostoTransporte()
        );

        SolicitudExternaEntity entity = SolicitudExternaEntity.fromDomain(dominio);
        SolicitudExternaEntity saved = solicitudRepo.save(entity);

        log.info("SolicitudExterna creada: id={}, prestamo={}, usuario={}, material={}",
                saved.getId(), saved.getPrestamoId(), saved.getIdUsuario(), saved.getIdMaterial());

        return saved;
    }

    /**
     * Obtiene una solicitud por su id.
     */
    @Transactional(readOnly = true)
    public Optional<SolicitudExternaEntity> obtenerPorId(String id) {
        return solicitudRepo.findById(id);
    }

    /**
     * Obtiene la solicitud asociada a un préstamo de circulacion-service.
     */
    @Transactional(readOnly = true)
    public Optional<SolicitudExternaEntity> obtenerPorPrestamoId(String prestamoId) {
        return solicitudRepo.findByPrestamoId(prestamoId);
    }

    /**
     * Aplica una transición de estado a la solicitud (aprobar / rechazar / completar).
     */
    @Transactional
    public SolicitudExternaEntity actualizarEstado(String id, String nuevoEstado) {
        SolicitudExternaEntity entity = solicitudRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("SolicitudExterna no encontrada: " + id));

        SolicitudExterna dominio = entity.toDomain();

        switch (nuevoEstado.toUpperCase()) {
            case "APROBADA"   -> dominio.aprobar();
            case "RECHAZADA"  -> dominio.rechazar();
            case "COMPLETADA" -> dominio.completar();
            default -> throw new IllegalArgumentException(
                    "Transición de estado no reconocida: " + nuevoEstado
                            + ". Valores válidos: APROBADA, RECHAZADA, COMPLETADA");
        }

        entity.setEstado(dominio.getEstado());
        SolicitudExternaEntity saved = solicitudRepo.save(entity);
        log.info("SolicitudExterna id={} actualizada a estado={}", id, saved.getEstado());
        return saved;
    }

    /**
     * Lista todas las solicitudes de un usuario.
     */
    @Transactional(readOnly = true)
    public List<SolicitudExternaEntity> listarPorUsuario(String idUsuario) {
        return solicitudRepo.findByIdUsuario(idUsuario);
    }
}
