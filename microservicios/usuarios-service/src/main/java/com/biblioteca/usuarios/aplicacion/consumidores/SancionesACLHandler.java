package com.biblioteca.usuarios.aplicacion.consumidores;

import com.biblioteca.usuarios.dominio.agregados.Usuario;
import com.biblioteca.usuarios.infraestructura.mensajeria.RabbitMQConfig;
import com.biblioteca.usuarios.infraestructura.persistencia.UsuarioEntity;
import com.biblioteca.usuarios.infraestructura.persistencia.UsuarioJpaRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class SancionesACLHandler {

    private static final Logger log = LoggerFactory.getLogger(SancionesACLHandler.class);

    private final UsuarioJpaRepository usuarioJpaRepository;
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public SancionesACLHandler(UsuarioJpaRepository usuarioJpaRepository,
                                RabbitTemplate rabbitTemplate) {
        this.usuarioJpaRepository = usuarioJpaRepository;
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = new ObjectMapper();
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    @Transactional
    public void handleSancionEvent(
            @Payload String payload,
            Message message,
            @Header(AmqpHeaders.RECEIVED_ROUTING_KEY) String routingKey) {

        log.info("Evento recibido con routing key '{}': {}", routingKey, payload);

        try {
            if (RabbitMQConfig.ROUTING_KEY_BLOQUEO_PETICION.equals(routingKey)) {
                handlePeticionBloqueo(payload);
            } else if (RabbitMQConfig.ROUTING_KEY_DESBLOQUEO_PETICION.equals(routingKey)) {
                handlePeticionDesbloqueo(payload);
            } else {
                log.warn("Routing key desconocida '{}', evento ignorado", routingKey);
            }
        } catch (Exception e) {
            log.error("Error procesando evento con routing key '{}': {}", routingKey, e.getMessage(), e);
        }
    }

    private void handlePeticionBloqueo(String payload) throws Exception {
        JsonNode json = objectMapper.readTree(payload);

        String usuarioId = json.get("usuarioId").asText();
        double montoDeudaTotal = json.has("montoDeudaTotal")
                ? json.get("montoDeudaTotal").asDouble(0.0)
                : 0.0;

        Optional<UsuarioEntity> optEntity = usuarioJpaRepository.findById(usuarioId);
        if (optEntity.isEmpty()) {
            log.warn("ACL: usuario id={} no encontrado, petición de bloqueo ignorada", usuarioId);
            return;
        }

        UsuarioEntity entity = optEntity.get();
        Usuario usuario = entity.toDomain();
        usuario.bloquearPorDeuda("Deuda $" + montoDeudaTotal);

        entity.setEstadoUsuario(usuario.getEstadoUsuario());
        entity.setMotivoBloqueo("Deuda $" + montoDeudaTotal);
        usuarioJpaRepository.save(entity);

        log.info("ACL: usuario id={} bloqueado por deuda de ${}", usuarioId, montoDeudaTotal);

        ObjectNode evento = objectMapper.createObjectNode();
        evento.put("usuarioId", usuarioId);
        evento.put("montoDeudaTotal", montoDeudaTotal);
        evento.put("estadoUsuario", usuario.getEstadoUsuario());
        evento.put("motivo", "Deuda $" + montoDeudaTotal);

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.ROUTING_KEY_USUARIO_BLOQUEADO,
                objectMapper.writeValueAsString(evento));

        log.info("ACL: evento UsuarioBloqueadoPorDeuda publicado para usuarioId={}", usuarioId);
    }

    private void handlePeticionDesbloqueo(String payload) throws Exception {
        JsonNode json = objectMapper.readTree(payload);
        String usuarioId = json.get("usuarioId").asText();

        Optional<UsuarioEntity> optEntity = usuarioJpaRepository.findById(usuarioId);
        if (optEntity.isEmpty()) {
            log.warn("ACL: usuario id={} no encontrado, petición de desbloqueo ignorada", usuarioId);
            return;
        }

        UsuarioEntity entity = optEntity.get();
        Usuario usuario = entity.toDomain();
        usuario.desbloquear();

        entity.setEstadoUsuario(usuario.getEstadoUsuario());
        entity.setMotivoBloqueo(null);
        usuarioJpaRepository.save(entity);

        log.info("ACL: usuario id={} desbloqueado correctamente", usuarioId);
    }
}
