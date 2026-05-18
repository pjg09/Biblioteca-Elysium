package com.biblioteca.commons.eventos;

import java.time.Instant;
import java.util.UUID;

public final class RenovacionRechazada implements IDomainEvent {

    private final String eventId;
    private final Instant occurredOn;
    private final String prestamoId;
    private final String usuarioId;
    private final String motivo;

    public RenovacionRechazada(String prestamoId, String usuarioId, String motivo) {
        this.eventId = UUID.randomUUID().toString();
        this.occurredOn = Instant.now();
        this.prestamoId = prestamoId;
        this.usuarioId = usuarioId;
        this.motivo = motivo;
    }

    @Override public String eventId() { return eventId; }
    @Override public String eventType() { return "RenovacionRechazada"; }
    @Override public Instant occurredOn() { return occurredOn; }
    @Override public String aggregateId() { return prestamoId; }
    @Override public int version() { return 1; }

    public String getPrestamoId() { return prestamoId; }
    public String getUsuarioId() { return usuarioId; }
    public String getMotivo() { return motivo; }
}
