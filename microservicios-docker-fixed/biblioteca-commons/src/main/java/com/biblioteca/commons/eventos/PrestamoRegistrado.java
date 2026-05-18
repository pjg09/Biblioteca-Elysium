package com.biblioteca.commons.eventos;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

public final class PrestamoRegistrado implements IDomainEvent {

    private final String eventId;
    private final Instant occurredOn;
    private final String prestamoId;
    private final String usuarioId;
    private final String materialId;
    private final LocalDateTime fechaDevolucionEsperada;
    private final String tipoPrestamo;

    public PrestamoRegistrado(
            String prestamoId,
            String usuarioId,
            String materialId,
            LocalDateTime fechaDevolucionEsperada,
            String tipoPrestamo) {
        this.eventId = UUID.randomUUID().toString();
        this.occurredOn = Instant.now();
        this.prestamoId = prestamoId;
        this.usuarioId = usuarioId;
        this.materialId = materialId;
        this.fechaDevolucionEsperada = fechaDevolucionEsperada;
        this.tipoPrestamo = tipoPrestamo;
    }

    @Override public String eventId() { return eventId; }
    @Override public String eventType() { return "PrestamoRegistrado"; }
    @Override public Instant occurredOn() { return occurredOn; }
    @Override public String aggregateId() { return prestamoId; }
    @Override public int version() { return 1; }

    public String getPrestamoId() { return prestamoId; }
    public String getUsuarioId() { return usuarioId; }
    public String getMaterialId() { return materialId; }
    public LocalDateTime getFechaDevolucionEsperada() { return fechaDevolucionEsperada; }
    public String getTipoPrestamo() { return tipoPrestamo; }
}
