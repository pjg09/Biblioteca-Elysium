package com.biblioteca.multas.aplicacion;

import com.biblioteca.multas.aplicacion.dto.DeudaPendienteDTO;
import com.biblioteca.multas.dominio.Multa;
import com.biblioteca.commons.excepciones.OperacionNoPermitidaEnEstadoMultaException;
import com.biblioteca.multas.infraestructura.persistencia.MultaEntity;
import com.biblioteca.multas.infraestructura.persistencia.MultaJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MultaService {

    private static final Logger log = LoggerFactory.getLogger(MultaService.class);

    private final MultaJpaRepository multaJpaRepository;

    public MultaService(MultaJpaRepository multaJpaRepository) {
        this.multaJpaRepository = multaJpaRepository;
    }

    @Transactional(readOnly = true)
    public Optional<MultaEntity> obtenerPorId(String id) {
        return multaJpaRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<MultaEntity> obtenerPorUsuario(String idUsuario) {
        return multaJpaRepository.findByIdUsuario(idUsuario);
    }

    @Transactional(readOnly = true)
    public DeudaPendienteDTO consultarDeudaPendiente(String idUsuario) {
        // Buscar multas en estado GENERADA (pendientes, no PAGADA ni CONDONADA)
        List<MultaEntity> multasPendientes = multaJpaRepository.findByIdUsuarioAndEstado(idUsuario, "GENERADA");

        double totalDeuda = multasPendientes.stream()
            .mapToDouble(MultaEntity::getMontoTotal)
            .sum();

        return new DeudaPendienteDTO(idUsuario, totalDeuda, multasPendientes.size());
    }

    @Transactional(readOnly = true)
    public List<MultaEntity> obtenerPorPrestamo(String idPrestamo) {
        return multaJpaRepository.findByIdPrestamo(idPrestamo);
    }

    @Transactional(readOnly = true)
    public List<MultaEntity> obtenerPorUsuarioYEstado(String idUsuario, String estado) {
        return multaJpaRepository.findByIdUsuarioAndEstado(idUsuario, estado);
    }

    /**
     * Registra el pago de una multa.
     * 
     * Aplica el State Pattern: transición GENERADA → PAGADA
     */
    @Transactional
    public MultaEntity pagarMulta(String id) throws OperacionNoPermitidaEnEstadoMultaException {
        MultaEntity entity = multaJpaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Multa no encontrada: " + id));

        Multa dominio = entity.toDomain();
        dominio.pagar(LocalDateTime.now());  // ← Lanza OperacionNoPermitidaEnEstadoMultaException

        entity.setEstado(dominio.getEstado());
        entity.setFechaPago(dominio.getFechaPago());
        MultaEntity saved = multaJpaRepository.save(entity);

        log.info("Multa pagada: id={}, usuario={}, monto={}", id, entity.getIdUsuario(), entity.getMontoTotal());
        return saved;
    }

    /**
     * Condona una multa (perdón administrativo).
     * 
     * Aplica el State Pattern: transición GENERADA → CONDONADA
     */
    @Transactional
    public MultaEntity condonarMulta(String id) throws OperacionNoPermitidaEnEstadoMultaException {
        MultaEntity entity = multaJpaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Multa no encontrada: " + id));

        Multa dominio = entity.toDomain();
        dominio.condonar();  // ← Lanza OperacionNoPermitidaEnEstadoMultaException

        entity.setEstado(dominio.getEstado());
        MultaEntity saved = multaJpaRepository.save(entity);

        log.info("Multa condonada: id={}, usuario={}, monto={}", id, entity.getIdUsuario(), entity.getMontoTotal());
        return saved;
    }

    @Transactional(readOnly = true)
    public List<MultaEntity> obtenerTodas() {
        return multaJpaRepository.findAll();
    }
}
