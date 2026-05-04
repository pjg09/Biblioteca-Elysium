package com.biblioteca.dominio.eventos;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

public final class MultaPagada implements IDomainEvent {
    private final String eventId;
    private final Instant occurredOn;
    public final String multaId;
    public final String usuarioId;
    public final double montoPagado;
    public final LocalDateTime fechaPago;

    public MultaPagada(String multaId, String usuarioId, double montoPagado, LocalDateTime fechaPago) {
        this.eventId = UUID.randomUUID().toString();
        this.occurredOn = Instant.now();
        this.multaId = multaId;
        this.usuarioId = usuarioId;
        this.montoPagado = montoPagado;
        this.fechaPago = fechaPago;
    }

    @Override public String eventId()     { return eventId; }
    @Override public String eventType()   { return "MultaPagada"; }
    @Override public Instant occurredOn() { return occurredOn; }
    @Override public String aggregateId() { return multaId; }
    @Override public int version()        { return 1; }
}
