package com.biblioteca.dominio.eventos;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

public final class PrestamoRenovado implements IDomainEvent {
    private final String eventId;
    private final Instant occurredOn;
    public final String prestamoId;
    public final String usuarioId;
    public final String materialId;
    public final LocalDateTime nuevaFechaDevolucion;
    public final int renovacionesUsadas;

    public PrestamoRenovado(String prestamoId, String usuarioId, String materialId,
                             LocalDateTime nuevaFechaDevolucion, int renovacionesUsadas) {
        this.eventId = UUID.randomUUID().toString();
        this.occurredOn = Instant.now();
        this.prestamoId = prestamoId;
        this.usuarioId = usuarioId;
        this.materialId = materialId;
        this.nuevaFechaDevolucion = nuevaFechaDevolucion;
        this.renovacionesUsadas = renovacionesUsadas;
    }

    @Override public String eventId()     { return eventId; }
    @Override public String eventType()   { return "PrestamoRenovado"; }
    @Override public Instant occurredOn() { return occurredOn; }
    @Override public String aggregateId() { return prestamoId; }
    @Override public int version()        { return 1; }
}
