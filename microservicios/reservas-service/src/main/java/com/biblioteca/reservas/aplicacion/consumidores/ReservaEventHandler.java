package com.biblioteca.reservas.aplicacion.consumidores;

import com.biblioteca.reservas.dominio.agregados.Reserva;
import com.biblioteca.reservas.infraestructura.mensajeria.RabbitMQConfig;
import com.biblioteca.reservas.infraestructura.persistencia.ReservaEntity;
import com.biblioteca.reservas.infraestructura.persistencia.ReservaJpaRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReservaEventHandler {

    private static final Logger log = LoggerFactory.getLogger(ReservaEventHandler.class);

    private final ReservaJpaRepository reservaJpaRepository;
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public ReservaEventHandler(ReservaJpaRepository reservaJpaRepository,
                                RabbitTemplate rabbitTemplate,
                                ObjectMapper objectMapper) {
        this.reservaJpaRepository = reservaJpaRepository;
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    @Transactional
    public void handleEvent(String message) {
        try {
            JsonNode node = objectMapper.readTree(message);
            String eventType = node.has("eventType") ? node.get("eventType").asText() : "";
            log.info("Evento recibido en reservas.queue: eventType={}", eventType);

            if ("material.devuelto".equals(eventType)) {
                handleMaterialDevuelto(node);
            } else {
                log.debug("Evento ignorado por reservas-service: {}", eventType);
            }

        } catch (Exception e) {
            log.error("Error procesando evento en reservas.queue: {}", e.getMessage(), e);
        }
    }

    private void handleMaterialDevuelto(JsonNode node) {
        String materialId = node.has("materialId") ? node.get("materialId").asText() : null;
        if (materialId == null || materialId.isBlank()) {
            log.warn("Evento material.devuelto sin materialId, ignorando.");
            return;
        }

        List<ReservaEntity> enEspera = reservaJpaRepository
                .findByIdMaterialAndEstadoReserva(materialId, "EN_ESPERA");

        if (enEspera.isEmpty()) {
            log.info("No hay reservas EN_ESPERA para material={}", materialId);
            return;
        }

        ReservaEntity primeraEntity = enEspera.get(0);

        if (primeraEntity.getPosicionCola() != 1) {
            log.warn("Primera reserva en cola no tiene posicion=1 para material={}, posicion={}",
                    materialId, primeraEntity.getPosicionCola());
            return;
        }

        try {
            LocalDateTime ahora = LocalDateTime.now();
            Reserva dominio = primeraEntity.toDomain();
            dominio.notificarDisponibilidad(ahora);

            primeraEntity.setEstadoReserva(dominio.getEstadoReserva());
            primeraEntity.setFechaNotificacion(dominio.getFechaNotificacion());
            primeraEntity.setFechaExpiracion(dominio.getFechaExpiracion());
            reservaJpaRepository.save(primeraEntity);

            Map<String, Object> evento = new HashMap<>();
            evento.put("eventType", "reserva.notificada");
            evento.put("reservaId", primeraEntity.getId());
            evento.put("usuarioId", primeraEntity.getIdUsuario());
            evento.put("materialId", primeraEntity.getIdMaterial());
            evento.put("fechaNotificacion", dominio.getFechaNotificacion().toString());
            evento.put("fechaExpiracion", dominio.getFechaExpiracion().toString());

            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EXCHANGE_NAME,
                    RabbitMQConfig.ROUTING_KEY_RESERVA_NOTIFICADA,
                    evento);

            log.info("ReservaNotificada publicada: reservaId={}, usuarioId={}, materialId={}",
                    primeraEntity.getId(), primeraEntity.getIdUsuario(), materialId);

        } catch (IllegalStateException e) {
            log.error("Error de estado al notificar reserva id={}: {}", primeraEntity.getId(), e.getMessage());
        } catch (com.biblioteca.commons.excepciones.OperacionNoPermitidaEnEstadoReservaException e) {
            log.error("Operación no permitida al notificar reserva id={}: {}", primeraEntity.getId(), e.getMessage());
        }
    }
}
