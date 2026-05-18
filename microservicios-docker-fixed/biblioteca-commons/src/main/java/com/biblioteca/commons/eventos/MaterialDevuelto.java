package com.biblioteca.commons.eventos;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

public final class MaterialDevuelto implements IDomainEvent {

    private final String eventId;
    private final Instant occurredOn;
    private final String prestamoId;
    private final String materialId;
    private final String usuarioId;
    private final LocalDateTime fechaDevolucion;
    private final LocalDateTime fechaDevolucionEsperada;
    private final int diasRetraso;
    private final boolean esUsable;

    public MaterialDevuelto(
            String prestamoId,
            String materialId,
            String usuarioId,
            LocalDateTime fechaDevolucion,
            LocalDateTime fechaDevolucionEsperada,
            int diasRetraso,
            boolean esUsable) {
        this.eventId = UUID.randomUUID().toString();
        this.occurredOn = Instant.now();
        this.prestamoId = prestamoId;
        this.materialId = materialId;
        this.usuarioId = usuarioId;
        this.fechaDevolucion = fechaDevolucion;
        this.fechaDevolucionEsperada = fechaDevolucionEsperada;
        this.diasRetraso = diasRetraso;
        this.esUsable = esUsable;
    }

    @Override public String eventId() { return eventId; }
    @Override public String eventType() { return "MaterialDevuelto"; }
    @Override public Instant occurredOn() { return occurredOn; }
    @Override public String aggregateId() { return prestamoId; }
    @Override public int version() { return 1; }

    public String getPrestamoId() { return prestamoId; }
    public String getMaterialId() { return materialId; }
    public String getUsuarioId() { return usuarioId; }
    public LocalDateTime getFechaDevolucion() { return fechaDevolucion; }
    public LocalDateTime getFechaDevolucionEsperada() { return fechaDevolucionEsperada; }
    public int getDiasRetraso() { return diasRetraso; }
    public boolean isEsUsable() { return esUsable; }
}
