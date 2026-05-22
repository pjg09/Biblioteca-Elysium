package com.biblioteca.multas.aplicacion.consumidores;

import com.biblioteca.multas.dominio.agregados.Multa;
import com.biblioteca.multas.dominio.servicios.CalculadorMultaService;
import com.biblioteca.multas.infraestructura.mensajeria.RabbitMQConfig;
import com.biblioteca.multas.infraestructura.persistencia.MultaEntity;
import com.biblioteca.multas.infraestructura.persistencia.MultaJpaRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class MultaEventHandler {

    private static final Logger log = LoggerFactory.getLogger(MultaEventHandler.class);

    private final MultaJpaRepository multaJpaRepository;
    private final CalculadorMultaService calculadorMultaService;
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public MultaEventHandler(MultaJpaRepository multaJpaRepository,
                              CalculadorMultaService calculadorMultaService,
                              RabbitTemplate rabbitTemplate,
                              ObjectMapper objectMapper) {
        this.multaJpaRepository = multaJpaRepository;
        this.calculadorMultaService = calculadorMultaService;
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = RabbitMQConfig.MULTAS_QUEUE)
    public void handleEvent(String payload,
                            @Header(AmqpHeaders.RECEIVED_ROUTING_KEY) String routingKey) {
        log.debug("Received event with routing key: {} payload: {}", routingKey, payload);

        try {
            switch (routingKey) {
                case RabbitMQConfig.ROUTING_KEY_MATERIAL_DEVUELTO:
                    handleMaterialDevuelto(payload);
                    break;
                case RabbitMQConfig.ROUTING_KEY_MULTA_PAGADA:
                    handleMultaPagada(payload);
                    break;
                default:
                    log.warn("Unhandled routing key: {}", routingKey);
            }
        } catch (Exception e) {
            log.error("Error processing event with routing key {}: {}", routingKey, e.getMessage(), e);
        }
    }

    private void handleMaterialDevuelto(String payload) throws Exception {
        JsonNode node = objectMapper.readTree(payload);

        String prestamoId = node.path("prestamoId").asText();
        String materialId = node.path("materialId").asText();
        String usuarioId = node.path("usuarioId").asText();
        int diasRetraso = node.path("diasRetraso").asInt(0);
        boolean esUsable = node.path("esUsable").asBoolean(true);
        String tipoUsuario = node.has("tipoUsuario") ? node.path("tipoUsuario").asText("ESTUDIANTE") : "ESTUDIANTE";
        double valorMaterial = node.path("valorMaterial").asDouble(0.0);

        if (diasRetraso > 0) {
            double monto = calculadorMultaService.calcularMontoRetraso(diasRetraso, tipoUsuario);

            Multa multaRetraso = new Multa(
                UUID.randomUUID().toString(),
                prestamoId,
                usuarioId,
                "POR_RETRASO",
                monto,
                "GENERADA",
                LocalDateTime.now(),
                null,
                "Retraso de " + diasRetraso + " días en la devolución del material " + materialId
            );

            MultaEntity savedRetraso = multaJpaRepository.save(MultaEntity.fromDomain(multaRetraso));
            log.info("Fine for delay created: id={} usuario={} monto={}", savedRetraso.getId(), usuarioId, monto);

            publishMultaGenerada(savedRetraso);
        }

        if (!esUsable) {
            double montoDano = (valorMaterial > 0)
                ? calculadorMultaService.calcularMontoDano(valorMaterial, "MODERADO")
                : valorMaterial * 0.4;

            Multa multaDano = new Multa(
                UUID.randomUUID().toString(),
                prestamoId,
                usuarioId,
                "POR_DANO",
                montoDano,
                "GENERADA",
                LocalDateTime.now(),
                null,
                "Material " + materialId + " devuelto en condición no apta para uso"
            );

            MultaEntity savedDano = multaJpaRepository.save(MultaEntity.fromDomain(multaDano));
            log.info("Fine for damage created: id={} usuario={} monto={}", savedDano.getId(), usuarioId, montoDano);

            publishMultaGenerada(savedDano);
        }
    }

    private void handleMultaPagada(String payload) throws Exception {
        JsonNode node = objectMapper.readTree(payload);

        String multaId = node.path("multaId").asText();
        String fechaPagoStr = node.path("fechaPago").asText();

        LocalDateTime fechaPago;
        try {
            fechaPago = LocalDateTime.parse(fechaPagoStr);
        } catch (Exception e) {
            fechaPago = LocalDateTime.now();
            log.warn("Could not parse fechaPago '{}', using now()", fechaPagoStr);
        }

        Optional<MultaEntity> optEntity = multaJpaRepository.findById(multaId);
        if (optEntity.isEmpty()) {
            log.warn("MultaPagada event received for unknown multaId: {}", multaId);
            return;
        }

        MultaEntity entity = optEntity.get();
        Multa multa = entity.toDomain();

        try {
            multa.pagar(fechaPago);
        } catch (IllegalStateException e) {
            log.warn("Cannot pay fine {}: {}", multaId, e.getMessage());
            return;
        }

        MultaEntity updated = MultaEntity.fromDomain(multa);
        multaJpaRepository.save(updated);
        log.info("Fine {} marked as PAGADA at {}", multaId, fechaPago);
    }

    private void publishMultaGenerada(MultaEntity entity) {
        Map<String, Object> event = new HashMap<>();
        event.put("multaId", entity.getId());
        event.put("idPrestamo", entity.getIdPrestamo());
        event.put("idUsuario", entity.getIdUsuario());
        event.put("tipoMulta", entity.getTipoMulta());
        event.put("montoTotal", entity.getMontoTotal());
        event.put("estado", entity.getEstado());
        event.put("fechaGeneracion", entity.getFechaGeneracion().toString());
        event.put("motivo", entity.getMotivo());

        rabbitTemplate.convertAndSend(
            RabbitMQConfig.EXCHANGE_NAME,
            RabbitMQConfig.ROUTING_KEY_MULTA_GENERADA,
            event
        );
        log.debug("Published multa.generada event for multaId={}", entity.getId());
    }
}
