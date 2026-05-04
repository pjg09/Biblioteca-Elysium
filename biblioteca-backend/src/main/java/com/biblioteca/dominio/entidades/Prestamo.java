package com.biblioteca.dominio.entidades;

import com.biblioteca.dominio.enumeraciones.EstadoTransaccion;
import com.biblioteca.dominio.estados.IEstadoPrestamo;
import com.biblioteca.dominio.estados.PrestamoActivoState;
import com.biblioteca.dominio.estados.PrestamoCompletadoState;
import com.biblioteca.dominio.eventos.IDomainEvent;
import com.biblioteca.dominio.eventos.MaterialDevuelto;
import com.biblioteca.dominio.eventos.PrestamoRenovado;
import com.biblioteca.dominio.excepciones.OperacionNoPermitidaException;
import com.biblioteca.dominio.objetosvalor.Evaluacion;
import com.biblioteca.dominio.objetosvalor.Resultado;
import com.biblioteca.dominio.objetosvalor.ResultadoValidacion;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Prestamo extends Transaccion {
    protected LocalDateTime fechaPrestamo;
    protected LocalDateTime fechaDevolucionEsperada;
    protected LocalDateTime fechaDevolucionReal;
    protected int renovacionesUsadas;
    private IEstadoPrestamo estadoActual;
    private final List<IDomainEvent> domainEvents = new ArrayList<>();

    public Prestamo(String id, String idUsuario, String idMaterial, LocalDateTime fechaDevolucionEsperada) {
        super(id, idUsuario, idMaterial);
        if (fechaDevolucionEsperada == null)
            throw new IllegalArgumentException("Fecha de devolución no puede ser nula");
        if (fechaDevolucionEsperada.isBefore(LocalDateTime.now()))
            throw new IllegalArgumentException("Fecha de devolución no puede estar en el pasado");

        this.fechaPrestamo = LocalDateTime.now();
        this.fechaDevolucionEsperada = fechaDevolucionEsperada;
        this.renovacionesUsadas = 0;
        this.estadoActual = new PrestamoActivoState();
    }

    // ── Comportamiento del agregado ──────────────────────────────────────────

    /**
     * Renueva el préstamo extendiendo la fecha de devolución.
     * Precondición: préstamo activo y renovacionesUsadas < maxRenovaciones.
     * Postcondición: emite PrestamoRenovado.
     */
    public void renovar(LocalDateTime nuevaFecha, int maxRenovaciones) throws OperacionNoPermitidaException {
        if (getEstado() != EstadoTransaccion.ACTIVA)
            throw new OperacionNoPermitidaException("renovar", "Solo se puede renovar un préstamo activo");
        if (renovacionesUsadas >= maxRenovaciones)
            throw new OperacionNoPermitidaException("renovar",
                "Límite de renovaciones alcanzado (" + maxRenovaciones + ")");
        if (nuevaFecha == null || nuevaFecha.isBefore(LocalDateTime.now()))
            throw new IllegalArgumentException("La nueva fecha debe ser futura");

        this.fechaDevolucionEsperada = nuevaFecha;
        this.renovacionesUsadas++;
        domainEvents.add(new PrestamoRenovado(id, idUsuario, idMaterial, nuevaFecha, renovacionesUsadas));
    }

    /**
     * Registra la devolución del material.
     * Precondición: préstamo activo.
     * Postcondición: emite MaterialDevuelto con la evaluación física del material.
     */
    public void devolver(LocalDateTime fechaReal, Evaluacion evaluacion) throws OperacionNoPermitidaException {
        if (getEstado() != EstadoTransaccion.ACTIVA)
            throw new OperacionNoPermitidaException("devolver", "Solo se puede devolver un préstamo activo");
        if (fechaReal == null)
            throw new IllegalArgumentException("Fecha de devolución real no puede ser nula");

        this.fechaDevolucionReal = fechaReal;
        this.setEstado(EstadoTransaccion.COMPLETADA);
        this.estadoActual = new PrestamoCompletadoState();

        int diasRetraso = (int) Math.max(0,
            ChronoUnit.DAYS.between(fechaDevolucionEsperada, fechaReal));

        domainEvents.add(new MaterialDevuelto(
            id, idMaterial, idUsuario, fechaReal, fechaDevolucionEsperada, diasRetraso, evaluacion));
    }

    /**
     * Extrae y limpia los eventos pendientes de publicación.
     * El servicio de aplicación llama este método tras persistir el agregado.
     */
    public List<IDomainEvent> pullEvents() {
        List<IDomainEvent> pending = Collections.unmodifiableList(new ArrayList<>(domainEvents));
        domainEvents.clear();
        return pending;
    }

    // ── Consultas del Estado Pattern (compatibilidad) ────────────────────────

    public ResultadoValidacion puedeRenovarse() {
        return estadoActual.puedeRenovarse(this);
    }

    public Resultado devolverLegacy() {
        return estadoActual.devolver(this);
    }

    public void cambiarEstado(IEstadoPrestamo nuevoEstado) {
        this.estadoActual = nuevoEstado;
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public LocalDateTime getFechaPrestamo()          { return fechaPrestamo; }
    public LocalDateTime getFechaDevolucionEsperada() { return fechaDevolucionEsperada; }
    public LocalDateTime getFechaDevolucionReal()     { return fechaDevolucionReal; }
    public int getRenovacionesUsadas()                { return renovacionesUsadas; }
}
