package com.biblioteca.dominio;
import com.biblioteca.dominio.excepciones.BibliotecaException;

import com.biblioteca.dominio.entidades.Multa;
import com.biblioteca.dominio.entidades.MultaPorRetraso;
import com.biblioteca.dominio.enumeraciones.EstadoMulta;
import com.biblioteca.dominio.eventos.IDomainEvent;
import com.biblioteca.dominio.eventos.MultaCondonada;
import com.biblioteca.dominio.eventos.MultaPagada;
import com.biblioteca.dominio.excepciones.OperacionNoPermitidaException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MultaTest {

    private Multa multa;

    @BeforeEach
    void setUp() {
        multa = new MultaPorRetraso("PRE-001", "USR-001", 10, 1000.0);
    }

    // ── pagar() ──────────────────────────────────────────────────────────────

    @Test
    void pagar_exitoso_emiteMultaPagada() throws BibliotecaException {
        multa.pagar(LocalDateTime.now());

        List<IDomainEvent> eventos = multa.pullEvents();
        assertEquals(1, eventos.size());
        assertInstanceOf(MultaPagada.class, eventos.get(0));

        MultaPagada evento = (MultaPagada) eventos.get(0);
        assertEquals("USR-001", evento.usuarioId);
        assertTrue(evento.montoPagado > 0);
    }

    @Test
    void pagar_cambiaEstadoAPagada() throws BibliotecaException {
        multa.pagar(LocalDateTime.now());
        multa.pullEvents();

        assertEquals(EstadoMulta.PAGADA, multa.getEstado());
    }

    @Test
    void pagar_lanzaExcepcion_siMultaYaPagada() throws BibliotecaException {
        multa.pagar(LocalDateTime.now());
        multa.pullEvents();

        assertThrows(OperacionNoPermitidaException.class,
            () -> multa.pagar(LocalDateTime.now()));
    }

    @Test
    void pagar_lanzaExcepcion_siMultaCondonada() throws BibliotecaException {
        multa.condonar();
        multa.pullEvents();

        assertThrows(OperacionNoPermitidaException.class,
            () -> multa.pagar(LocalDateTime.now()));
    }

    // ── condonar() ───────────────────────────────────────────────────────────

    @Test
    void condonar_exitoso_emiteMultaCondonada() throws BibliotecaException {
        multa.condonar();

        List<IDomainEvent> eventos = multa.pullEvents();
        assertEquals(1, eventos.size());
        assertInstanceOf(MultaCondonada.class, eventos.get(0));
    }

    @Test
    void condonar_cambiaEstadoACondonada() throws BibliotecaException {
        multa.condonar();
        multa.pullEvents();

        assertEquals(EstadoMulta.CONDONADA, multa.getEstado());
    }

    @Test
    void condonar_lanzaExcepcion_siMultaYaPagada() throws BibliotecaException {
        multa.pagar(LocalDateTime.now());
        multa.pullEvents();

        assertThrows(OperacionNoPermitidaException.class, () -> multa.condonar());
    }
}
