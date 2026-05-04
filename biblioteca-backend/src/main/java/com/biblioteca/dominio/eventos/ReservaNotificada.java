package com.biblioteca.dominio.eventos;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

public final class ReservaNotificada implements IDomainEvent {
    private final String eventId;
    private final Instant occurredOn;
    public final String reservaId;
    public final String usuarioId;
    public final String materialId;
    public final LocalDateTime fechaNotificacion;
    public final LocalDateTime fechaExpiracion;

    public ReservaNotificada(String reservaId, String usuarioId, String materialId,
                              LocalDateTime fechaNotificacion, LocalDateTime fechaExpiracion) {
        this.eventId = UUID.randomUUID().toString();
        this.occurredOn = Instant.now();
        this.reservaId = reservaId;
        this.usuarioId = usuarioId;
        this.materialId = materialId;
        this.fechaNotificacion = fechaNotificacion;
        this.fechaExpiracion = fechaExpiracion;
    }

    @Override public String eventId()     { return eventId; }
    @Override public String eventType()   { return "ReservaNotificada"; }
    @Override public Instant occurredOn() { return occurredOn; }
    @Override public String aggregateId() { return reservaId; }
    @Override public int version()        { return 1; }
}
