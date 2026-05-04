package com.biblioteca.dominio.eventos;

import com.biblioteca.dominio.enumeraciones.TipoMulta;
import com.biblioteca.dominio.enumeraciones.TipoUsuario;

import java.time.Instant;
import java.util.UUID;

public final class MultaGenerada implements IDomainEvent {
    private final String eventId;
    private final Instant occurredOn;
    public final String multaId;
    public final String prestamoId;
    public final String usuarioId;
    public final TipoMulta tipoMulta;
    public final double montoTotal;
    public final TipoUsuario tipoUsuario;

    public MultaGenerada(String multaId, String prestamoId, String usuarioId,
                          TipoMulta tipoMulta, double montoTotal, TipoUsuario tipoUsuario) {
        this.eventId = UUID.randomUUID().toString();
        this.occurredOn = Instant.now();
        this.multaId = multaId;
        this.prestamoId = prestamoId;
        this.usuarioId = usuarioId;
        this.tipoMulta = tipoMulta;
        this.montoTotal = montoTotal;
        this.tipoUsuario = tipoUsuario;
    }

    @Override public String eventId()     { return eventId; }
    @Override public String eventType()   { return "MultaGenerada"; }
    @Override public Instant occurredOn() { return occurredOn; }
    @Override public String aggregateId() { return multaId; }
    @Override public int version()        { return 1; }
}
