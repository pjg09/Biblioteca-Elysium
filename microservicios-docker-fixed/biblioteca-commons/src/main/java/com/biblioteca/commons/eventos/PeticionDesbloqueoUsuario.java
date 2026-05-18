package com.biblioteca.commons.eventos;

import java.time.Instant;
import java.util.UUID;

public final class PeticionDesbloqueoUsuario implements IDomainEvent {

    private final String eventId;
    private final Instant occurredOn;
    private final String usuarioId;
    private final String origen;

    public PeticionDesbloqueoUsuario(String usuarioId, String origen) {
        this.eventId = UUID.randomUUID().toString();
        this.occurredOn = Instant.now();
        this.usuarioId = usuarioId;
        this.origen = origen;
    }

    @Override public String eventId() { return eventId; }
    @Override public String eventType() { return "PeticionDesbloqueoUsuario"; }
    @Override public Instant occurredOn() { return occurredOn; }
    @Override public String aggregateId() { return usuarioId; }
    @Override public int version() { return 1; }

    public String getUsuarioId() { return usuarioId; }
    public String getOrigen() { return origen; }
}
