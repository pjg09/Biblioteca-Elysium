package com.biblioteca.reportes.aplicacion;

import com.biblioteca.reportes.dominio.RegistroEstadistica;
import com.biblioteca.reportes.infraestructura.persistencia.EstadisticaRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class EventosEstadisticaHandler {

    private static final Logger log = LoggerFactory.getLogger(EventosEstadisticaHandler.class);

    private final EstadisticaRepository estadisticaRepository;
    private final ObjectMapper objectMapper;

    public EventosEstadisticaHandler(EstadisticaRepository estadisticaRepository) {
        this.estadisticaRepository = estadisticaRepository;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.findAndRegisterModules();
    }

    @RabbitListener(queues = "reportes.queue")
    public void handleEvento(@Payload String payload,
                             @Header(value = "eventType", required = false) String eventTypeHeader) {
        try {
            JsonNode node = objectMapper.readTree(payload);
            String eventType = eventTypeHeader != null ? eventTypeHeader : extraerCampo(node, "eventType");

            log.info("Evento recibido para estadísticas: tipo={}", eventType);

            RegistroEstadistica registro = construirRegistro(node, eventType);
            if (registro != null) {
                estadisticaRepository.save(registro);
                log.info("Estadística registrada: tipo={}, descripcion={}", registro.getTipo(), registro.getDescripcion());
            }
        } catch (Exception e) {
            log.error("Error procesando evento para estadísticas: payload={}", payload, e);
        }
    }

    private RegistroEstadistica construirRegistro(JsonNode node, String eventType) {
        if (eventType == null) return null;
        return switch (eventType) {
            case "PrestamoRegistrado" -> new RegistroEstadistica(
                    "PRESTAMO",
                    "Nuevo préstamo registrado para usuario " + extraerCampo(node, "usuarioId")
                            + " con material " + extraerCampo(node, "materialId"),
                    1.0);
            case "MaterialDevuelto" -> {
                int diasRetraso = node.has("diasRetraso") ? node.get("diasRetraso").asInt(0) : 0;
                yield new RegistroEstadistica(
                        "DEVOLUCION",
                        "Material devuelto, días de retraso: " + diasRetraso,
                        diasRetraso);
            }
            case "MultaGenerada" -> {
                double monto = node.has("montoTotal") ? node.get("montoTotal").asDouble(0.0) : 0.0;
                yield new RegistroEstadistica(
                        "MULTA_GENERADA",
                        "Multa generada de tipo " + extraerCampo(node, "tipoMulta")
                                + " para usuario " + extraerCampo(node, "usuarioId"),
                        monto);
            }
            case "MultaPagada" -> {
                double montoPagado = node.has("montoPagado") ? node.get("montoPagado").asDouble(0.0) : 0.0;
                yield new RegistroEstadistica(
                        "MULTA_PAGADA",
                        "Multa pagada por usuario " + extraerCampo(node, "usuarioId"),
                        montoPagado);
            }
            default -> {
                log.debug("Evento no relevante para estadísticas: {}", eventType);
                yield null;
            }
        };
    }

    private String extraerCampo(JsonNode node, String campo) {
        if (node == null || !node.has(campo)) return "";
        return node.get(campo).asText("");
    }
}
