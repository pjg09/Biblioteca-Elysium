package com.biblioteca.dominio.entidades;

import com.biblioteca.dominio.enumeraciones.EstadoMulta;
import com.biblioteca.dominio.eventos.IDomainEvent;
import com.biblioteca.dominio.eventos.MultaCondonada;
import com.biblioteca.dominio.eventos.MultaPagada;
import com.biblioteca.dominio.excepciones.OperacionNoPermitidaException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public abstract class Multa {
    protected String id;
    protected String idPrestamo;
    protected String idUsuario;
    protected LocalDateTime fechaGeneracion;
    protected LocalDateTime fechaPago;
    protected EstadoMulta estado;
    protected String motivo;
    private final List<IDomainEvent> domainEvents = new ArrayList<>();

    public Multa(String idPrestamo, String idUsuario, String motivo) {
        this.id = "MUL-" + UUID.randomUUID().toString().substring(0, 8);
        this.idPrestamo = idPrestamo;
        this.idUsuario = idUsuario;
        this.fechaGeneracion = LocalDateTime.now();
        this.estado = EstadoMulta.PENDIENTE;
        this.motivo = motivo;
    }

    // ── Comportamiento del agregado ──────────────────────────────────────────

    /**
     * Registra el pago de la multa.
     * Precondición: EstadoMulta == PENDIENTE.
     * Postcondición: emite MultaPagada.
     */
    public void pagar(LocalDateTime fechaPago) throws OperacionNoPermitidaException {
        if (estado != EstadoMulta.PENDIENTE)
            throw new OperacionNoPermitidaException("pagar",
                "Solo se puede pagar una multa pendiente. Estado actual: " + estado);
        if (fechaPago == null)
            throw new IllegalArgumentException("Fecha de pago no puede ser nula");

        this.fechaPago = fechaPago;
        this.estado = EstadoMulta.PAGADA;
        domainEvents.add(new MultaPagada(id, idUsuario, calcularMontoTotal(), fechaPago));
    }

    /**
     * Condona la multa (exoneración administrativa).
     * Precondición: EstadoMulta == PENDIENTE.
     * Postcondición: emite MultaCondonada.
     */
    public void condonar() throws OperacionNoPermitidaException {
        if (estado != EstadoMulta.PENDIENTE)
            throw new OperacionNoPermitidaException("condonar",
                "Solo se puede condonar una multa pendiente. Estado actual: " + estado);

        this.estado = EstadoMulta.CONDONADA;
        domainEvents.add(new MultaCondonada(id, idUsuario));
    }

    /**
     * Extrae y limpia los eventos pendientes de publicación.
     */
    public List<IDomainEvent> pullEvents() {
        List<IDomainEvent> pending = Collections.unmodifiableList(new ArrayList<>(domainEvents));
        domainEvents.clear();
        return pending;
    }

    // ── Consultas ────────────────────────────────────────────────────────────

    public abstract double calcularMontoTotal();

    public boolean esPendiente() { return estado == EstadoMulta.PENDIENTE; }

    // ── Getters ──────────────────────────────────────────────────────────────

    public String getId()                       { return id; }
    public String getIdPrestamo()               { return idPrestamo; }
    public String getIdUsuario()                { return idUsuario; }
    public LocalDateTime getFechaGeneracion()   { return fechaGeneracion; }
    public LocalDateTime getFechaPago()         { return fechaPago; }
    public EstadoMulta getEstado()              { return estado; }
    public String getMotivo()                   { return motivo; }
}
