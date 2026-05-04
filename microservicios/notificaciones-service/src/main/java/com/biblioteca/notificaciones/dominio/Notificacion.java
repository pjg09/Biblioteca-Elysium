package com.biblioteca.notificaciones.dominio;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "notificaciones")
public class Notificacion {

    @Id
    private String id;
    private String usuarioId;
    private String tipo;
    private String mensaje;
    private String eventType;
    private String aggregateId;
    private boolean leida;
    private LocalDateTime fechaCreacion;

    public Notificacion() {
        this.leida = false;
        this.fechaCreacion = LocalDateTime.now();
    }

    public Notificacion(String usuarioId, String tipo, String mensaje, String eventType, String aggregateId) {
        this.usuarioId = usuarioId;
        this.tipo = tipo;
        this.mensaje = mensaje;
        this.eventType = eventType;
        this.aggregateId = aggregateId;
        this.leida = false;
        this.fechaCreacion = LocalDateTime.now();
    }

    public void marcarLeida() {
        this.leida = true;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUsuarioId() { return usuarioId; }
    public void setUsuarioId(String usuarioId) { this.usuarioId = usuarioId; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    public String getAggregateId() { return aggregateId; }
    public void setAggregateId(String aggregateId) { this.aggregateId = aggregateId; }

    public boolean isLeida() { return leida; }
    public void setLeida(boolean leida) { this.leida = leida; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}
