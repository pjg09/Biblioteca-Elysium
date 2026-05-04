package com.biblioteca.commons.eventos;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

public final class MultaPagada implements IDomainEvent {

    private final String eventId;
    private final Instant occurredOn;
    private final String multaId;
    private final String usuarioId;
    private final double montoPagado;
    private final LocalDateTime fechaPago;

    public MultaPagada(String multaId, String usuarioId, double montoPagado, LocalDateTime fechaPago) {
        this.eventId = UUID.randomUUID().toString();
        this.occurredOn = Instant.now();
        this.multaId = multaId;
        this.usuarioId = usuarioId;
        this.montoPagado = montoPagado;
        this.fechaPago = fechaPago;
    }

    @Override public String eventId() { return eventId; }
    @Override public String eventType() { return "MultaPagada"; }
    @Override public Instant occurredOn() { return occurredOn; }
    @Override public String aggregateId() { return multaId; }
    @Override public int version() { return 1; }

    public String getMultaId() { return multaId; }
    public String getUsuarioId() { return usuarioId; }
    public double getMontoPagado() { return montoPagado; }
    public LocalDateTime getFechaPago() { return fechaPago; }
}
