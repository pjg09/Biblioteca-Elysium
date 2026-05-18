package com.biblioteca.reservas.aplicacion;

import com.biblioteca.commons.excepciones.OperacionNoPermitidaEnEstadoReservaException;
import com.biblioteca.reservas.dominio.Reserva;
import com.biblioteca.reservas.infraestructura.mensajeria.RabbitMQConfig;
import com.biblioteca.reservas.infraestructura.persistencia.ReservaEntity;
import com.biblioteca.reservas.infraestructura.persistencia.ReservaJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class LimpiezaReservasScheduler {

    private static final Logger log = LoggerFactory.getLogger(LimpiezaReservasScheduler.class);

    private final ReservaJpaRepository reservaJpaRepository;
    private final RabbitTemplate rabbitTemplate;
    private final ReservaService reservaService;

    public LimpiezaReservasScheduler(ReservaJpaRepository reservaJpaRepository,
                                     RabbitTemplate rabbitTemplate,
                                     ReservaService reservaService) {
        this.reservaJpaRepository = reservaJpaRepository;
        this.rabbitTemplate = rabbitTemplate;
        this.reservaService = reservaService;
    }

    /**
     * Tarea programada que se ejecuta cada hora (3 600 000 ms).
     * Detecta reservas NOTIFICADA cuya fechaExpiracion ya pasó,
     * las expira, publica ReservaExpirada y reorganiza la cola del material.
     */
    @Scheduled(fixedDelay = 3600000)
    @Transactional
    public void limpiarReservasExpiradas() {
        LocalDateTime ahora = LocalDateTime.now();
        log.info("Iniciando limpieza de reservas expiradas. Hora actual: {}", ahora);

        List<ReservaEntity> notificadas = reservaJpaRepository.findAllByEstadoReserva("NOTIFICADA");
        Set<String> materialesAfectados = new HashSet<>();

        for (ReservaEntity entity : notificadas) {
            if (entity.getFechaExpiracion() == null || !entity.getFechaExpiracion().isBefore(ahora)) {
                continue;
            }

            try {
                Reserva dominio = entity.toDomain();
                dominio.expirar(); // ← ESTA LÍNEA LANZA LA EXCEPCIÓN

                entity.setEstadoReserva(dominio.getEstadoReserva());
                entity.setFechaExpiracion(dominio.getFechaExpiracion());
                reservaJpaRepository.save(entity);

                materialesAfectados.add(entity.getIdMaterial());
                log.info("Reserva expirada: id={}, usuario={}, material={}", 
                    entity.getId(), entity.getIdUsuario(), entity.getIdMaterial());

                // Publicar evento ReservaExpirada
                Map<String, Object> evento = new HashMap<>();
                evento.put("eventType", "reserva.expirada");
                evento.put("reservaId", entity.getId());
                evento.put("usuarioId", entity.getIdUsuario());
                evento.put("materialId", entity.getIdMaterial());
                
            } catch (OperacionNoPermitidaEnEstadoReservaException e) {
                log.error("No se puede expirar reserva en estado {}: operación={}, motivo={}", 
                    entity.getEstadoReserva(), e.getOperacion(), e.getMotivo());
            }
        }

        // Reorganizar cola para cada material afectado
        for (String materialId : materialesAfectados) {
            reservaService.reorganizarCola(materialId);
        }

        log.info("Limpieza completada. Reservas expiradas: {}. Materiales reorganizados: {}",
                materialesAfectados.size(), materialesAfectados.size());
    }
}
