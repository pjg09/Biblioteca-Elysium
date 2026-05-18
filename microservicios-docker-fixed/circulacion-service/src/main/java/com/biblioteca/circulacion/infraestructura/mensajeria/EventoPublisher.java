package com.biblioteca.circulacion.infraestructura.mensajeria;

import com.biblioteca.circulacion.infraestructura.persistencia.PrestamoEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class EventoPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public EventoPublisher(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    public void publicarPrestamoRegistrado(PrestamoEntity p, String tipoUsuario) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("eventId", UUID.randomUUID().toString());
        payload.put("eventType", "PrestamoRegistrado");
        payload.put("occurredOn", Instant.now().toString());
        payload.put("prestamoId", p.getId());
        payload.put("usuarioId", p.getIdUsuario());
        payload.put("materialId", p.getIdMaterial());
        payload.put("fechaDevolucionEsperada", p.getFechaDevolucionEsperada().toString());
        payload.put("tipoPrestamo", p.getTipoPrestamo());
        payload.put("tipoUsuario", tipoUsuario);
        payload.put("sede", p.getSede());
        enviar("prestamo.registrado", payload);
    }

    public void publicarMaterialDevuelto(PrestamoEntity p, boolean esUsable, String tipoUsuario) {
        long diasRetraso = p.getFechaDevolucionReal() != null
                ? Math.max(0, java.time.temporal.ChronoUnit.DAYS.between(
                        p.getFechaDevolucionEsperada(), p.getFechaDevolucionReal()))
                : 0;

        Map<String, Object> payload = new HashMap<>();
        payload.put("eventId", UUID.randomUUID().toString());
        payload.put("eventType", "MaterialDevuelto");
        payload.put("occurredOn", Instant.now().toString());
        payload.put("prestamoId", p.getId());
        payload.put("usuarioId", p.getIdUsuario());
        payload.put("materialId", p.getIdMaterial());
        payload.put("fechaDevolucion",
                p.getFechaDevolucionReal() != null ? p.getFechaDevolucionReal().toString() : null);
        payload.put("fechaDevolucionEsperada", p.getFechaDevolucionEsperada().toString());
        payload.put("diasRetraso", diasRetraso);
        payload.put("esUsable", esUsable);
        payload.put("tipoUsuario", tipoUsuario);
        enviar("material.devuelto", payload);
    }

    public void publicarPrestamoRenovado(PrestamoEntity p) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("eventId", UUID.randomUUID().toString());
        payload.put("eventType", "PrestamoRenovado");
        payload.put("occurredOn", Instant.now().toString());
        payload.put("prestamoId", p.getId());
        payload.put("usuarioId", p.getIdUsuario());
        payload.put("materialId", p.getIdMaterial());
        payload.put("nuevaFechaDevolucion", p.getFechaDevolucionEsperada().toString());
        payload.put("renovacionesUsadas", p.getRenovacionesUsadas());
        enviar("prestamo.renovado", payload);
    }

    public void publicarRenovacionRechazada(String prestamoId, String usuarioId, String motivo) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("eventId", UUID.randomUUID().toString());
        payload.put("eventType", "RenovacionRechazada");
        payload.put("occurredOn", Instant.now().toString());
        payload.put("prestamoId", prestamoId);
        payload.put("usuarioId", usuarioId);
        payload.put("motivo", motivo);
        enviar("renovacion.rechazada", payload);
    }

    private void enviar(String routingKey, Map<String, Object> payload) {
        try {
            String json = objectMapper.writeValueAsString(payload);
            MessageProperties props = new MessageProperties();
            props.setContentType("application/json");
            Message message = new Message(json.getBytes(java.nio.charset.StandardCharsets.UTF_8), props);
            rabbitTemplate.send(RabbitMQConfig.EXCHANGE_NAME, routingKey, message);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializando evento para routing key: " + routingKey, e);
        }
    }
}
