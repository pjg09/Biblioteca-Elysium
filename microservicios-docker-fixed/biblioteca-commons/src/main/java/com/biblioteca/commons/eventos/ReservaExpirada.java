package com.biblioteca.commons.eventos;

import java.time.Instant;
import java.util.UUID;

public final class ReservaExpirada implements IDomainEvent {

    private final String eventId;
    private final Instant occurredOn;
    private final String reservaId;
    private final String usuarioId;
    private final String materialId;

    public ReservaExpirada(String reservaId, String usuarioId, String materialId) {
        this.eventId = UUID.randomUUID().toString();
        this.occurredOn = Instant.now();
        this.reservaId = reservaId;
        this.usuarioId = usuarioId;
        this.materialId = materialId;
    }

    @Override public String eventId() { return eventId; }
    @Override public String eventType() { return "ReservaExpirada"; }
    @Override public Instant occurredOn() { return occurredOn; }
    @Override public String aggregateId() { return reservaId; }
    @Override public int version() { return 1; }

    public String getReservaId() { return reservaId; }
    public String getUsuarioId() { return usuarioId; }
    public String getMaterialId() { return materialId; }
}
