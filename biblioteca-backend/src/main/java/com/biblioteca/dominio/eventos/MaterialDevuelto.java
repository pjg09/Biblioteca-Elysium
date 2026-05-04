package com.biblioteca.dominio.eventos;

import com.biblioteca.dominio.objetosvalor.Evaluacion;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

public final class MaterialDevuelto implements IDomainEvent {
    private final String eventId;
    private final Instant occurredOn;
    public final String prestamoId;
    public final String materialId;
    public final String usuarioId;
    public final LocalDateTime fechaDevolucion;
    public final LocalDateTime fechaDevolucionEsperada;
    public final int diasRetraso;
    public final Evaluacion evaluacion;

    public MaterialDevuelto(String prestamoId, String materialId, String usuarioId,
                             LocalDateTime fechaDevolucion, LocalDateTime fechaDevolucionEsperada,
                             int diasRetraso, Evaluacion evaluacion) {
        this.eventId = UUID.randomUUID().toString();
        this.occurredOn = Instant.now();
        this.prestamoId = prestamoId;
        this.materialId = materialId;
        this.usuarioId = usuarioId;
        this.fechaDevolucion = fechaDevolucion;
        this.fechaDevolucionEsperada = fechaDevolucionEsperada;
        this.diasRetraso = diasRetraso;
        this.evaluacion = evaluacion;
    }

    @Override public String eventId()     { return eventId; }
    @Override public String eventType()   { return "MaterialDevuelto"; }
    @Override public Instant occurredOn() { return occurredOn; }
    @Override public String aggregateId() { return prestamoId; }
    @Override public int version()        { return 1; }
}
