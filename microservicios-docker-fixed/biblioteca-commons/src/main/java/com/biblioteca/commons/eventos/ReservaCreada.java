package com.biblioteca.commons.eventos;

import java.time.Instant;
import java.util.UUID;

public final class ReservaCreada implements IDomainEvent {

    private final String eventId;
    private final Instant occurredOn;
    private final String reservaId;
    private final String usuarioId;
    private final String materialId;
    private final int posicionCola;

    public ReservaCreada(String reservaId, String usuarioId, String materialId, int posicionCola) {
        this.eventId = UUID.randomUUID().toString();
        this.occurredOn = Instant.now();
        this.reservaId = reservaId;
        this.usuarioId = usuarioId;
        this.materialId = materialId;
        this.posicionCola = posicionCola;
    }

    @Override public String eventId() { return eventId; }
    @Override public String eventType() { return "ReservaCreada"; }
    @Override public Instant occurredOn() { return occurredOn; }
    @Override public String aggregateId() { return reservaId; }
    @Override public int version() { return 1; }

    public String getReservaId() { return reservaId; }
    public String getUsuarioId() { return usuarioId; }
    public String getMaterialId() { return materialId; }
    public int getPosicionCola() { return posicionCola; }
}
