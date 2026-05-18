package com.biblioteca.commons.eventos;

import java.time.Instant;
import java.util.UUID;

public final class UsuarioBloqueadoPorDeuda implements IDomainEvent {

    private final String eventId;
    private final Instant occurredOn;
    private final String usuarioId;
    private final double montoDeudaTotal;

    public UsuarioBloqueadoPorDeuda(String usuarioId, double montoDeudaTotal) {
        this.eventId = UUID.randomUUID().toString();
        this.occurredOn = Instant.now();
        this.usuarioId = usuarioId;
        this.montoDeudaTotal = montoDeudaTotal;
    }

    @Override public String eventId() { return eventId; }
    @Override public String eventType() { return "UsuarioBloqueadoPorDeuda"; }
    @Override public Instant occurredOn() { return occurredOn; }
    @Override public String aggregateId() { return usuarioId; }
    @Override public int version() { return 1; }

    public String getUsuarioId() { return usuarioId; }
    public double getMontoDeudaTotal() { return montoDeudaTotal; }
}
