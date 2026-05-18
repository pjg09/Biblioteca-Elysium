package com.biblioteca.commons.eventos;

import java.time.Instant;
import java.util.UUID;

public final class MultaCondonada implements IDomainEvent {

    private final String eventId;
    private final Instant occurredOn;
    private final String multaId;
    private final String usuarioId;

    public MultaCondonada(String multaId, String usuarioId) {
        this.eventId = UUID.randomUUID().toString();
        this.occurredOn = Instant.now();
        this.multaId = multaId;
        this.usuarioId = usuarioId;
    }

    @Override public String eventId() { return eventId; }
    @Override public String eventType() { return "MultaCondonada"; }
    @Override public Instant occurredOn() { return occurredOn; }
    @Override public String aggregateId() { return multaId; }
    @Override public int version() { return 1; }

    public String getMultaId() { return multaId; }
    public String getUsuarioId() { return usuarioId; }
}
