package com.biblioteca.dominio;

import com.biblioteca.dominio.entidades.Prestamo;
import com.biblioteca.dominio.entidades.PrestamoNormal;
import com.biblioteca.dominio.enumeraciones.EstadoTransaccion;
import com.biblioteca.dominio.eventos.IDomainEvent;
import com.biblioteca.dominio.eventos.MaterialDevuelto;
import com.biblioteca.dominio.eventos.PrestamoRenovado;
import com.biblioteca.dominio.excepciones.BibliotecaException;
import com.biblioteca.dominio.excepciones.OperacionNoPermitidaException;
import com.biblioteca.dominio.objetosvalor.Evaluacion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PrestamoTest {

    private Prestamo prestamo;

    @BeforeEach
    void setUp() {
        prestamo = new PrestamoNormal(
            "PRE-001", "USR-001", "MAT-001",
            LocalDateTime.now().plusDays(14),
            "Estante A1"
        );
    }

    // ── renovar() ────────────────────────────────────────────────────────────

    @Test
    void renovar_exitoso_emitePrestamoRenovado() throws BibliotecaException {
        LocalDateTime nuevaFecha = LocalDateTime.now().plusDays(14);
        prestamo.renovar(nuevaFecha, 2);

        List<IDomainEvent> eventos = prestamo.pullEvents();
        assertEquals(1, eventos.size());
        assertInstanceOf(PrestamoRenovado.class, eventos.get(0));

        PrestamoRenovado evento = (PrestamoRenovado) eventos.get(0);
        assertEquals("PRE-001", evento.prestamoId);
        assertEquals(1, evento.renovacionesUsadas);
    }

    @Test
    void renovar_incrementaContadorRenovaciones() throws BibliotecaException {
        prestamo.renovar(LocalDateTime.now().plusDays(14), 2);
        assertEquals(1, prestamo.getRenovacionesUsadas());
    }

    @Test
    void renovar_lanzaExcepcion_cuandoLimiteAlcanzado() throws BibliotecaException {
        prestamo.renovar(LocalDateTime.now().plusDays(14), 2);
        prestamo.pullEvents(); // vaciar

        LocalDateTime segundaFecha = LocalDateTime.now().plusDays(14);
        prestamo.renovar(segundaFecha, 2);
        prestamo.pullEvents();

        assertThrows(OperacionNoPermitidaException.class,
            () -> prestamo.renovar(LocalDateTime.now().plusDays(14), 2));
    }

    @Test
    void renovar_lanzaExcepcion_cuandoPrestamoNoActivo() throws BibliotecaException {
        Evaluacion evaluacion = new Evaluacion(true, new ArrayList<>());
        prestamo.devolver(LocalDateTime.now(), evaluacion);
        prestamo.pullEvents();

        assertThrows(OperacionNoPermitidaException.class,
            () -> prestamo.renovar(LocalDateTime.now().plusDays(14), 2));
    }

    // ── devolver() ───────────────────────────────────────────────────────────

    @Test
    void devolver_exitoso_emiteMaterialDevuelto() throws BibliotecaException {
        Evaluacion evaluacion = new Evaluacion(true, new ArrayList<>());
        prestamo.devolver(LocalDateTime.now(), evaluacion);

        List<IDomainEvent> eventos = prestamo.pullEvents();
        assertEquals(1, eventos.size());
        assertInstanceOf(MaterialDevuelto.class, eventos.get(0));

        MaterialDevuelto evento = (MaterialDevuelto) eventos.get(0);
        assertEquals("PRE-001", evento.prestamoId);
        assertEquals("MAT-001", evento.materialId);
    }

    @Test
    void devolver_calculaDiasRetrasoCorrectamente() throws Exception { // reflexión + checked
        // Forzar fecha de devolución esperada en el pasado via reflexión
        java.lang.reflect.Field fd = Prestamo.class.getDeclaredField("fechaDevolucionEsperada");
        fd.setAccessible(true);
        fd.set(prestamo, LocalDateTime.now().minusDays(5));

        Evaluacion evaluacion = new Evaluacion(true, new ArrayList<>());
        prestamo.devolver(LocalDateTime.now(), evaluacion);

        MaterialDevuelto evento = prestamo.pullEvents().stream()
            .filter(e -> e instanceof MaterialDevuelto)
            .map(e -> (MaterialDevuelto) e)
            .findFirst().orElseThrow();

        assertEquals(5, evento.diasRetraso);
    }

    @Test
    void devolver_cambiaEstadoACompletada() throws BibliotecaException {
        Evaluacion evaluacion = new Evaluacion(true, new ArrayList<>());
        prestamo.devolver(LocalDateTime.now(), evaluacion);
        prestamo.pullEvents();

        assertEquals(EstadoTransaccion.COMPLETADA, prestamo.getEstado());
    }

    @Test
    void devolver_lanzaExcepcion_cuandoPrestamoYaDevuelto() throws BibliotecaException {
        Evaluacion evaluacion = new Evaluacion(true, new ArrayList<>());
        prestamo.devolver(LocalDateTime.now(), evaluacion);
        prestamo.pullEvents();

        assertThrows(OperacionNoPermitidaException.class,
            () -> prestamo.devolver(LocalDateTime.now(), evaluacion));
    }

    // ── pullEvents() ─────────────────────────────────────────────────────────

    @Test
    void pullEvents_limpiaBuzonTrasExtraer() throws BibliotecaException {
        prestamo.renovar(LocalDateTime.now().plusDays(14), 2);
        prestamo.pullEvents(); // primer pull
        List<IDomainEvent> segundosPull = prestamo.pullEvents();
        assertTrue(segundosPull.isEmpty());
    }
}
