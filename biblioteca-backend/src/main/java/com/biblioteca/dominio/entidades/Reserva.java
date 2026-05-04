package com.biblioteca.dominio.entidades;

import com.biblioteca.dominio.enumeraciones.EstadoReserva;
import com.biblioteca.dominio.enumeraciones.EstadoTransaccion;
import com.biblioteca.dominio.eventos.IDomainEvent;
import com.biblioteca.dominio.eventos.ReservaCancelada;
import com.biblioteca.dominio.eventos.ReservaCreada;
import com.biblioteca.dominio.eventos.ReservaExpirada;
import com.biblioteca.dominio.eventos.ReservaNotificada;
import com.biblioteca.dominio.excepciones.OperacionNoPermitidaException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Reserva extends Transaccion {
    protected LocalDateTime fechaReserva;
    protected LocalDateTime fechaNotificacion;
    protected LocalDateTime fechaExpiracion;
    protected int posicionCola;
    private EstadoReserva estadoReserva;
    private final List<IDomainEvent> domainEvents = new ArrayList<>();

    public Reserva(String id, String idUsuario, String idMaterial) {
        super(id, idUsuario, idMaterial);
        this.fechaReserva = LocalDateTime.now();
        this.fechaExpiracion = fechaReserva.plusDays(3);
        this.estadoReserva = EstadoReserva.EN_ESPERA;
    }

    // ── Comportamiento del agregado ──────────────────────────────────────────

    /**
     * Confirma la creación asignando posición en la cola.
     * Emite ReservaCreada. Solo debe llamarse una vez al crear.
     */
    public void registrar(int posicion) {
        this.posicionCola = posicion;
        domainEvents.add(new ReservaCreada(id, idUsuario, idMaterial, posicion));
    }

    /**
     * Notifica al usuario que el material está disponible.
     * Precondición: EstadoReserva == EN_ESPERA y posicionCola == 1.
     * Postcondición: emite ReservaNotificada.
     */
    public void notificarDisponibilidad(LocalDateTime fechaNotif) throws OperacionNoPermitidaException {
        if (estadoReserva != EstadoReserva.EN_ESPERA)
            throw new OperacionNoPermitidaException("notificarDisponibilidad",
                "Solo se puede notificar una reserva en espera. Estado actual: " + estadoReserva);
        if (posicionCola != 1)
            throw new OperacionNoPermitidaException("notificarDisponibilidad",
                "Solo se puede notificar al primero en la cola. Posición actual: " + posicionCola);

        this.fechaNotificacion = fechaNotif;
        this.fechaExpiracion = fechaNotif.plusHours(24);
        this.estadoReserva = EstadoReserva.NOTIFICADA;
        domainEvents.add(new ReservaNotificada(id, idUsuario, idMaterial, fechaNotif, this.fechaExpiracion));
    }

    /**
     * Expira la reserva por falta de recogida dentro del plazo.
     * Precondición: EstadoReserva == NOTIFICADA.
     * Postcondición: emite ReservaExpirada.
     */
    public void expirar() throws OperacionNoPermitidaException {
        if (estadoReserva != EstadoReserva.NOTIFICADA)
            throw new OperacionNoPermitidaException("expirar",
                "Solo se puede expirar una reserva notificada. Estado actual: " + estadoReserva);

        this.estadoReserva = EstadoReserva.CANCELADA;
        this.setEstado(EstadoTransaccion.CANCELADA);
        domainEvents.add(new ReservaExpirada(id, idUsuario, idMaterial));
    }

    /**
     * Cancela la reserva voluntariamente.
     * Precondición: EstadoReserva es EN_ESPERA o NOTIFICADA.
     * Postcondición: emite ReservaCancelada.
     */
    public void cancelar() throws OperacionNoPermitidaException {
        if (estadoReserva != EstadoReserva.EN_ESPERA && estadoReserva != EstadoReserva.NOTIFICADA)
            throw new OperacionNoPermitidaException("cancelar",
                "Solo se puede cancelar una reserva activa. Estado actual: " + estadoReserva);

        this.estadoReserva = EstadoReserva.CANCELADA;
        this.setEstado(EstadoTransaccion.CANCELADA);
        domainEvents.add(new ReservaCancelada(id, idUsuario, idMaterial));
    }

    /**
     * Actualiza la posición en la cola de reservas (reorganización interna tras cancelaciones).
     * No emite evento — es coordinación del servicio de dominio GestorColaReservas.
     */
    public void asignarPosicionEnCola(int posicion) {
        this.posicionCola = posicion;
    }

    /**
     * Extrae y limpia los eventos pendientes de publicación.
     */
    public List<IDomainEvent> pullEvents() {
        List<IDomainEvent> pending = Collections.unmodifiableList(new ArrayList<>(domainEvents));
        domainEvents.clear();
        return pending;
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public LocalDateTime getFechaReserva()      { return fechaReserva; }
    public LocalDateTime getFechaNotificacion() { return fechaNotificacion; }
    public LocalDateTime getFechaExpiracion()   { return fechaExpiracion; }
    public int getPosicionCola()                { return posicionCola; }
    public EstadoReserva getEstadoReserva()     { return estadoReserva; }

    /** Compatibilidad con código existente que consulta EstadoTransaccion. */
    public void setFechaExpiracion(LocalDateTime fecha) {
        this.fechaExpiracion = fecha;
    }
}
