package com.biblioteca.materiales.aplicacion.consumidores;

import com.biblioteca.materiales.infraestructura.mensajeria.RabbitMQConfig;
import com.biblioteca.materiales.infraestructura.persistencia.MaterialEntity;
import com.biblioteca.materiales.infraestructura.persistencia.MaterialJpaRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MaterialEventHandler {

    private static final Logger log = LoggerFactory.getLogger(MaterialEventHandler.class);

    private final MaterialJpaRepository materialJpaRepository;
    private final ObjectMapper objectMapper;

    public MaterialEventHandler(MaterialJpaRepository materialJpaRepository) {
        this.materialJpaRepository = materialJpaRepository;
        this.objectMapper = new ObjectMapper();
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void handleMaterialDevuelto(@Payload String payload, Message message) {
        log.info("Evento MaterialDevuelto recibido: {}", payload);
        try {
            JsonNode json = objectMapper.readTree(payload);

            String materialId = json.get("materialId").asText();
            boolean esUsable = json.has("esUsable") && json.get("esUsable").asBoolean(true);

            Optional<MaterialEntity> optEntity = materialJpaRepository.findById(materialId);
            if (optEntity.isEmpty()) {
                log.warn("Material no encontrado con id={}, ignorando evento", materialId);
                return;
            }

            MaterialEntity entity = optEntity.get();
            String nuevoEstado = esUsable ? "DISPONIBLE" : "EN_REPARACION";
            entity.setEstado(nuevoEstado);
            materialJpaRepository.save(entity);

            log.info("Material id={} actualizado a estado={}", materialId, nuevoEstado);

        } catch (Exception e) {
            log.error("Error procesando evento MaterialDevuelto: {}", e.getMessage(), e);
        }
    }
}
