package com.biblioteca.dominio;

import com.biblioteca.dominio.entidades.Reserva;
import com.biblioteca.dominio.entidades.ReservaNormal;
import com.biblioteca.dominio.enumeraciones.EstadoReserva;
import com.biblioteca.dominio.enumeraciones.EstadoTransaccion;
import com.biblioteca.dominio.eventos.IDomainEvent;
import com.biblioteca.dominio.eventos.ReservaCancelada;
import com.biblioteca.dominio.eventos.ReservaCreada;
import com.biblioteca.dominio.eventos.ReservaExpirada;
import com.biblioteca.dominio.eventos.ReservaNotificada;
import com.biblioteca.dominio.excepciones.BibliotecaException;
import com.biblioteca.dominio.excepciones.OperacionNoPermitidaException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReservaTest {

    private Reserva reserva;

    @BeforeEach
    void setUp() {
        reserva = new ReservaNormal("RES-001", "USR-001", "MAT-001", "Sala de lectura");
    }

    // ── registrar() ──────────────────────────────────────────────────────────

    @Test
    void registrar_emiteReservaCreada() throws BibliotecaException {
        reserva.registrar(1);
        List<IDomainEvent> eventos = reserva.pullEvents();
        assertEquals(1, eventos.size());
        assertInstanceOf(ReservaCreada.class, eventos.get(0));

        ReservaCreada evento = (ReservaCreada) eventos.get(0);
        assertEquals(1, evento.posicionCola);
        assertEquals("RES-001", evento.reservaId);
    }

    // ── notificarDisponibilidad() ─────────────────────────────────────────────

    @Test
    void notificarDisponibilidad_exitoso_emiteReservaNotificada() throws BibliotecaException {
        reserva.registrar(1);
        reserva.pullEvents();

        reserva.notificarDisponibilidad(LocalDateTime.now());

        List<IDomainEvent> eventos = reserva.pullEvents();
        assertEquals(1, eventos.size());
        assertInstanceOf(ReservaNotificada.class, eventos.get(0));
    }

    @Test
    void notificarDisponibilidad_cambiasEstadoANotificada() throws BibliotecaException {
        reserva.registrar(1);
        reserva.pullEvents();

        reserva.notificarDisponibilidad(LocalDateTime.now());
        reserva.pullEvents();

        assertEquals(EstadoReserva.NOTIFICADA, reserva.getEstadoReserva());
    }

    @Test
    void notificarDisponibilidad_lanzaExcepcion_siPosicionNoEsUno() throws BibliotecaException {
        reserva.registrar(2); // posición 2 en cola
        reserva.pullEvents();

        assertThrows(OperacionNoPermitidaException.class,
            () -> reserva.notificarDisponibilidad(LocalDateTime.now()));
    }

    @Test
    void notificarDisponibilidad_lanzaExcepcion_siEstadoNoBEsEnEspera() throws BibliotecaException {
        reserva.registrar(1);
        reserva.pullEvents();
        reserva.cancelar();
        reserva.pullEvents();

        assertThrows(OperacionNoPermitidaException.class,
            () -> reserva.notificarDisponibilidad(LocalDateTime.now()));
    }

    // ── expirar() ─────────────────────────────────────────────────────────────

    @Test
    void expirar_exitoso_emiteReservaExpirada() throws BibliotecaException {
        reserva.registrar(1);
        reserva.pullEvents();
        reserva.notificarDisponibilidad(LocalDateTime.now());
        reserva.pullEvents();

        reserva.expirar();
        List<IDomainEvent> eventos = reserva.pullEvents();
        assertEquals(1, eventos.size());
        assertInstanceOf(ReservaExpirada.class, eventos.get(0));
    }

    @Test
    void expirar_lanzaExcepcion_siEstadoNoEsNotificada() throws BibliotecaException {
        reserva.registrar(1);
        reserva.pullEvents();
        // Estado sigue siendo EN_ESPERA, no NOTIFICADA

        assertThrows(OperacionNoPermitidaException.class, () -> reserva.expirar());
    }

    // ── cancelar() ────────────────────────────────────────────────────────────

    @Test
    void cancelar_exitoso_emiteReservaCancelada() throws BibliotecaException {
        reserva.registrar(1);
        reserva.pullEvents();

        reserva.cancelar();
        List<IDomainEvent> eventos = reserva.pullEvents();
        assertEquals(1, eventos.size());
        assertInstanceOf(ReservaCancelada.class, eventos.get(0));
    }

    @Test
    void cancelar_cambiaEstadoTransaccionACancelada() throws BibliotecaException {
        reserva.registrar(1);
        reserva.pullEvents();

        reserva.cancelar();
        reserva.pullEvents();

        assertEquals(EstadoTransaccion.CANCELADA, reserva.getEstado());
        assertEquals(EstadoReserva.CANCELADA, reserva.getEstadoReserva());
    }

    @Test
    void cancelar_lanzaExcepcion_siReservaYaCancelada() throws BibliotecaException {
        reserva.registrar(1);
        reserva.pullEvents();
        reserva.cancelar();
        reserva.pullEvents();

        assertThrows(OperacionNoPermitidaException.class, () -> reserva.cancelar());
    }
}
