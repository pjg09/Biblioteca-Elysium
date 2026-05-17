package com.biblioteca.multas.dominio.estados;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para el State Pattern de Multa.
 * 
 * Valida que las transiciones de estado sean correctas y que
 * las operaciones ilícitas lancen excepciones apropiadas.
 */
@DisplayName("Pruebas del patrón State para Multa")
class MultaStateTest {
    
    private MultaContexto contexto;
    private LocalDateTime ahora;
    
    @BeforeEach
    void setup() {
        ahora = LocalDateTime.now();
        contexto = new MultaContexto(5000.0, new MultaGeneradaState());
    }
    
    @DisplayName("GENERADA: puede pagar (transición a PAGADA)")
    @Test
    void testGeneradaPuedePagar() throws OperacionNoPermitidaEnEstadoMultaException {
        MultaGeneradaState estado = (MultaGeneradaState) contexto.getEstadoActual();
        
        estado.pagar(ahora, contexto);
        
        assertInstanceOf(MultaPagadaState.class, contexto.getEstadoActual());
        assertEquals(ahora, contexto.getFechaPago());
    }
    
    @DisplayName("GENERADA: puede condonar (transición a CONDONADA)")
    @Test
    void testGeneradaPuedeCondonar() throws OperacionNoPermitidaEnEstadoMultaException {
        MultaGeneradaState estado = (MultaGeneradaState) contexto.getEstadoActual();
        
        estado.condonar(contexto);
        
        assertInstanceOf(MultaCondonadaState.class, contexto.getEstadoActual());
    }
    
    @DisplayName("PAGADA: no puede pagar nuevamente")
    @Test
    void testPagadaNoRepagaMulta() throws OperacionNoPermitidaEnEstadoMultaException {
        contexto.setEstadoActual(new MultaPagadaState());
        MultaPagadaState estado = (MultaPagadaState) contexto.getEstadoActual();
        
        OperacionNoPermitidaEnEstadoMultaException ex = assertThrows(
                OperacionNoPermitidaEnEstadoMultaException.class,
                () -> estado.pagar(ahora, contexto)
        );
        
        assertTrue(ex.getMessage().contains("PAGADA"));
        assertTrue(ex.getMessage().contains("ya pagada"));
    }
    
    @DisplayName("PAGADA: no puede condonar")
    @Test
    void testPagadaNoCondona() throws OperacionNoPermitidaEnEstadoMultaException {
        contexto.setEstadoActual(new MultaPagadaState());
        MultaPagadaState estado = (MultaPagadaState) contexto.getEstadoActual();
        
        OperacionNoPermitidaEnEstadoMultaException ex = assertThrows(
                OperacionNoPermitidaEnEstadoMultaException.class,
                () -> estado.condonar(contexto)
        );
        
        assertTrue(ex.getMessage().contains("PAGADA"));
    }
    
    @DisplayName("CONDONADA: terminal, no puede pagar")
    @Test
    void testCondonadaTerminal() throws OperacionNoPermitidaEnEstadoMultaException {
        contexto.setEstadoActual(new MultaCondonadaState());
        MultaCondonadaState estado = (MultaCondonadaState) contexto.getEstadoActual();
        
        OperacionNoPermitidaEnEstadoMultaException ex = assertThrows(
                OperacionNoPermitidaEnEstadoMultaException.class,
                () -> estado.pagar(ahora, contexto)
        );
        
        assertTrue(ex.getMessage().contains("CONDONADA"));
    }
    
    @DisplayName("Estados terminales no permiten transiciones")
    @Test
    void testEstadosTerminalesInmutables() throws OperacionNoPermitidaEnEstadoMultaException {
        // PAGADA es terminal
        contexto.setEstadoActual(new MultaPagadaState());
        MultaPagadaState pagada = (MultaPagadaState) contexto.getEstadoActual();
        
        assertThrows(OperacionNoPermitidaEnEstadoMultaException.class,
                () -> pagada.condonar(contexto));
        
        // CONDONADA es terminal
        contexto.setEstadoActual(new MultaCondonadaState());
        MultaCondonadaState condonada = (MultaCondonadaState) contexto.getEstadoActual();
        
        assertThrows(OperacionNoPermitidaEnEstadoMultaException.class,
                () -> condonada.pagar(ahora, contexto));
    }
}
