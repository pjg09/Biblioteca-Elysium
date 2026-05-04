package com.biblioteca.dominio.eventos;

import java.time.Instant;
import java.util.UUID;

public final class MultaCondonada implements IDomainEvent {
    private final String eventId;
    private final Instant occurredOn;
    public final String multaId;
    public final String usuarioId;

    public MultaCondonada(String multaId, String usuarioId) {
        this.eventId = UUID.randomUUID().toString();
        this.occurredOn = Instant.now();
        this.multaId = multaId;
        this.usuarioId = usuarioId;
    }

    @Override public String eventId()     { return eventId; }
    @Override public String eventType()   { return "MultaCondonada"; }
    @Override public Instant occurredOn() { return occurredOn; }
    @Override public String aggregateId() { return multaId; }
    @Override public int version()        { return 1; }
}
