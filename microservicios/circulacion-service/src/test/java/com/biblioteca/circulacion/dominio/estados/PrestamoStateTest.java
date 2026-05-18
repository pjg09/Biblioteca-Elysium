package com.biblioteca.circulacion.dominio.estados;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDateTime;

import com.biblioteca.commons.excepciones.OperacionNoPermitidaEnEstadoException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para el State Pattern de Prestamo.
 * 
 * Valida que las transiciones de estado sean correctas y que
 * las operaciones ilícitas lancen excepciones apropiadas.
 */
@DisplayName("Pruebas del patrón State para Prestamo")
class PrestamoStateTest {
    
    private PrestamoContexto contexto;
    private LocalDateTime ahora;
    
    @BeforeEach
    void setup() {
        ahora = LocalDateTime.now();
        contexto = new PrestamoContexto(
                ahora.plusDays(14),
                0,
                new PrestamoActivoState()
        );
    }
    
    @DisplayName("ACTIVO: puede renovar hasta límite de renovaciones")
    @Test
    void testActivoPuedeRenovar() throws OperacionNoPermitidaEnEstadoException {
        PrestamoActivoState estado = (PrestamoActivoState) contexto.getEstadoActual();
        LocalDateTime nuevaFecha = ahora.plusDays(14);
        
        estado.renovar(nuevaFecha, 2, contexto);
        
        assertEquals(1, contexto.getRenovacionesUsadas());
        assertEquals(nuevaFecha, contexto.getFechaDevolucionEsperada());
        assertInstanceOf(PrestamoActivoState.class, contexto.getEstadoActual());
    }
    
    @DisplayName("ACTIVO: no puede renovar más allá del límite")
    @Test
    void testActivoNoRenuevaALimiteSobrepasado() throws OperacionNoPermitidaEnEstadoException {
        PrestamoActivoState estado = (PrestamoActivoState) contexto.getEstadoActual();
        
        estado.renovar(ahora.plusDays(14), 2, contexto); // Primera renovación (renovacionesUsadas = 1)
        estado.renovar(ahora.plusDays(28), 2, contexto); // Segunda renovación (renovacionesUsadas = 2)
        
        OperacionNoPermitidaEnEstadoException ex = assertThrows(
                OperacionNoPermitidaEnEstadoException.class,
                () -> estado.renovar(ahora.plusDays(42), 2, contexto) // Tercera renovación debe fallar
        );
        
        assertTrue(ex.getMessage().contains("límite de renovaciones"));
    }
    
    @DisplayName("ACTIVO: puede devolver (transición a COMPLETADO)")
    @Test
    void testActivoPuedeDevolver() throws OperacionNoPermitidaEnEstadoException {
        PrestamoActivoState estado = (PrestamoActivoState) contexto.getEstadoActual();
        
        estado.devolver(ahora, contexto);
        
        assertInstanceOf(PrestamoCompletadoState.class, contexto.getEstadoActual());
        assertEquals(ahora, contexto.getFechaDevolucionReal());
    }
    
    @DisplayName("ACTIVO: puede cancelar (transición a CANCELADO)")
    @Test
    void testActivoPuedeCancelar() throws OperacionNoPermitidaEnEstadoException {
        PrestamoActivoState estado = (PrestamoActivoState) contexto.getEstadoActual();
        
        estado.cancelar(contexto);
        
        assertInstanceOf(PrestamoCanceladoState.class, contexto.getEstadoActual());
    }
    
    @DisplayName("COMPLETADO: no puede renovar")
    @Test
    void testCompletadoNoRenueva() throws OperacionNoPermitidaEnEstadoException {
        contexto.setEstadoActual(new PrestamoCompletadoState());
        PrestamoCompletadoState estado = (PrestamoCompletadoState) contexto.getEstadoActual();
        
        OperacionNoPermitidaEnEstadoException ex = assertThrows(
                OperacionNoPermitidaEnEstadoException.class,
                () -> estado.renovar(ahora.plusDays(14), 2, contexto)
        );
        
        assertTrue(ex.getMessage().contains("COMPLETADO"));
    }
    
    @DisplayName("COMPLETADO: no puede devolver")
    @Test
    void testCompletadoNoDevuelve() throws OperacionNoPermitidaEnEstadoException {
        contexto.setEstadoActual(new PrestamoCompletadoState());
        PrestamoCompletadoState estado = (PrestamoCompletadoState) contexto.getEstadoActual();
        
        OperacionNoPermitidaEnEstadoException ex = assertThrows(
                OperacionNoPermitidaEnEstadoException.class,
                () -> estado.devolver(ahora, contexto)
        );
        
        assertTrue(ex.getMessage().contains("COMPLETADO"));
    }
    
    @DisplayName("CANCELADO: no puede renovar")
    @Test
    void testCanceladoNoRenueva() throws OperacionNoPermitidaEnEstadoException {
        contexto.setEstadoActual(new PrestamoCanceladoState());
        PrestamoCanceladoState estado = (PrestamoCanceladoState) contexto.getEstadoActual();
        
        OperacionNoPermitidaEnEstadoException ex = assertThrows(
                OperacionNoPermitidaEnEstadoException.class,
                () -> estado.renovar(ahora.plusDays(14), 2, contexto)
        );
        
        assertTrue(ex.getMessage().contains("CANCELADO"));
    }
}
