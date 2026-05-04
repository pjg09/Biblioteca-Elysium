package com.biblioteca.cobros.infraestructura.mensajeria;

import com.biblioteca.cobros.infraestructura.persistencia.DeudaPendienteEntity;
import com.biblioteca.cobros.infraestructura.persistencia.DeudaPendienteJpaRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CobroEventHandler {

    private static final Logger log = LoggerFactory.getLogger(CobroEventHandler.class);

    private final DeudaPendienteJpaRepository deudaPendienteJpaRepository;
    private final ObjectMapper objectMapper;

    public CobroEventHandler(DeudaPendienteJpaRepository deudaPendienteJpaRepository,
                              ObjectMapper objectMapper) {
        this.deudaPendienteJpaRepository = deudaPendienteJpaRepository;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = RabbitMQConfig.COBROS_QUEUE)
    public void handleEvent(String payload,
                            @Header(AmqpHeaders.RECEIVED_ROUTING_KEY) String routingKey) {
        log.debug("Received event with routing key: {} payload: {}", routingKey, payload);

        try {
            if (RabbitMQConfig.ROUTING_KEY_MULTA_GENERADA.equals(routingKey)) {
                handleMultaGenerada(payload);
            } else {
                log.warn("Unhandled routing key in cobros-service: {}", routingKey);
            }
        } catch (Exception e) {
            log.error("Error processing event with routing key {}: {}", routingKey, e.getMessage(), e);
        }
    }

    private void handleMultaGenerada(String payload) throws Exception {
        JsonNode node = objectMapper.readTree(payload);

        String multaId = node.path("multaId").asText();
        String idUsuario = node.path("idUsuario").asText();
        double montoTotal = node.path("montoTotal").asDouble(0.0);

        Optional<DeudaPendienteEntity> existing = deudaPendienteJpaRepository.findByMultaId(multaId);
        if (existing.isPresent()) {
            log.debug("Deuda for multaId {} already tracked, skipping", multaId);
            return;
        }

        DeudaPendienteEntity deuda = new DeudaPendienteEntity(multaId, idUsuario, montoTotal);
        deudaPendienteJpaRepository.save(deuda);
        log.info("Tracked outstanding debt: multaId={} usuario={} monto={}", multaId, idUsuario, montoTotal);
    }
}
