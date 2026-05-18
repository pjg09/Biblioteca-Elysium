package com.biblioteca.notificaciones.aplicacion;

import com.biblioteca.notificaciones.dominio.Notificacion;
import com.biblioteca.notificaciones.infraestructura.persistencia.NotificacionRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class EventosBibliotecaHandler {

    private static final Logger log = LoggerFactory.getLogger(EventosBibliotecaHandler.class);

    private final NotificacionRepository notificacionRepository;
    private final ObjectMapper objectMapper;

    public EventosBibliotecaHandler(NotificacionRepository notificacionRepository) {
        this.notificacionRepository = notificacionRepository;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.findAndRegisterModules();
    }

    @RabbitListener(queues = "notificaciones.queue")
    public void handleEvento(@Payload String payload,
                             @Header(value = "eventType", required = false) String eventTypeHeader) {
        try {
            JsonNode node = objectMapper.readTree(payload);

            String eventType = eventTypeHeader != null ? eventTypeHeader : extraerCampo(node, "eventType");
            String usuarioId = resolverUsuarioId(node, eventType);
            String aggregateId = extraerCampo(node, "eventId");
            String mensaje = construirMensaje(node, eventType);

            log.info("Evento recibido: tipo={}, usuarioId={}", eventType, usuarioId);

            if (usuarioId != null && !usuarioId.isBlank()) {
                Notificacion notificacion = new Notificacion(
                        usuarioId,
                        eventType,
                        mensaje,
                        eventType,
                        aggregateId
                );
                notificacionRepository.save(notificacion);
                log.info("Notificacion guardada para usuario={}, tipo={}", usuarioId, eventType);
            } else {
                log.warn("No se pudo determinar usuarioId para el evento tipo={}", eventType);
            }
        } catch (Exception e) {
            log.error("Error procesando evento de biblioteca: payload={}", payload, e);
        }
    }

    private String resolverUsuarioId(JsonNode node, String eventType) {
        if (eventType == null) return null;
        return switch (eventType) {
            case "PrestamoRegistrado", "PrestamoRenovado", "RenovacionRechazada",
                 "MaterialDevuelto", "MultaGenerada", "MultaPagada", "MultaCondonada",
                 "UsuarioBloqueadoPorDeuda", "PeticionDesbloqueoUsuario",
                 "ReservaCreada", "ReservaNotificada", "ReservaExpirada", "ReservaCancelada"
                    -> extraerCampo(node, "usuarioId");
            default -> extraerCampo(node, "usuarioId");
        };
    }

    private String construirMensaje(JsonNode node, String eventType) {
        if (eventType == null) return "Evento de biblioteca recibido.";
        return switch (eventType) {
            case "PrestamoRegistrado" -> String.format(
                    "Se ha registrado un nuevo préstamo para el material %s.",
                    extraerCampo(node, "materialId"));
            case "MaterialDevuelto" -> {
                int diasRetraso = node.has("diasRetraso") ? node.get("diasRetraso").asInt(0) : 0;
                yield diasRetraso > 0
                        ? String.format("Material devuelto con %d días de retraso.", diasRetraso)
                        : "Material devuelto a tiempo. ¡Gracias!";
            }
            case "PrestamoRenovado" -> String.format(
                    "Tu préstamo ha sido renovado. Nueva fecha de devolución: %s.",
                    extraerCampo(node, "nuevaFechaDevolucion"));
            case "RenovacionRechazada" -> String.format(
                    "Tu solicitud de renovación fue rechazada. Motivo: %s.",
                    extraerCampo(node, "motivo"));
            case "MultaGenerada" -> String.format(
                    "Se ha generado una multa de tipo %s por un monto de %.2f.",
                    extraerCampo(node, "tipoMulta"),
                    node.has("montoTotal") ? node.get("montoTotal").asDouble(0.0) : 0.0);
            case "MultaPagada" -> String.format(
                    "Tu multa ha sido pagada por un monto de %.2f.",
                    node.has("montoPagado") ? node.get("montoPagado").asDouble(0.0) : 0.0);
            case "MultaCondonada" -> "Tu multa ha sido condonada.";
            case "UsuarioBloqueadoPorDeuda" -> String.format(
                    "Tu cuenta ha sido bloqueada por deuda acumulada de %.2f.",
                    node.has("montoDeudaTotal") ? node.get("montoDeudaTotal").asDouble(0.0) : 0.0);
            case "PeticionDesbloqueoUsuario" -> "Tu solicitud de desbloqueo ha sido recibida y está en proceso.";
            case "ReservaCreada" -> String.format(
                    "Reserva creada para el material %s. Posición en cola: %d.",
                    extraerCampo(node, "materialId"),
                    node.has("posicionCola") ? node.get("posicionCola").asInt(0) : 0);
            case "ReservaNotificada" -> String.format(
                    "El material que reservaste está disponible. Tienes hasta %s para recogerlo.",
                    extraerCampo(node, "fechaExpiracion"));
            case "ReservaExpirada" -> String.format(
                    "Tu reserva para el material %s ha expirado.",
                    extraerCampo(node, "materialId"));
            case "ReservaCancelada" -> String.format(
                    "Tu reserva para el material %s ha sido cancelada.",
                    extraerCampo(node, "materialId"));
            default -> "Evento de biblioteca: " + eventType;
        };
    }

    private String extraerCampo(JsonNode node, String campo) {
        if (node == null || !node.has(campo)) return "";
        return node.get(campo).asText("");
    }
}
