package com.biblioteca.commons.eventos;

import java.time.Instant;
import java.util.UUID;

public final class MultaGenerada implements IDomainEvent {

    private final String eventId;
    private final Instant occurredOn;
    private final String multaId;
    private final String prestamoId;
    private final String usuarioId;
    private final String tipoMulta;
    private final double montoTotal;
    private final String tipoUsuario;

    public MultaGenerada(
            String multaId,
            String prestamoId,
            String usuarioId,
            String tipoMulta,
            double montoTotal,
            String tipoUsuario) {
        this.eventId = UUID.randomUUID().toString();
        this.occurredOn = Instant.now();
        this.multaId = multaId;
        this.prestamoId = prestamoId;
        this.usuarioId = usuarioId;
        this.tipoMulta = tipoMulta;
        this.montoTotal = montoTotal;
        this.tipoUsuario = tipoUsuario;
    }

    @Override public String eventId() { return eventId; }
    @Override public String eventType() { return "MultaGenerada"; }
    @Override public Instant occurredOn() { return occurredOn; }
    @Override public String aggregateId() { return multaId; }
    @Override public int version() { return 1; }

    public String getMultaId() { return multaId; }
    public String getPrestamoId() { return prestamoId; }
    public String getUsuarioId() { return usuarioId; }
    public String getTipoMulta() { return tipoMulta; }
    public double getMontoTotal() { return montoTotal; }
    public String getTipoUsuario() { return tipoUsuario; }
}
