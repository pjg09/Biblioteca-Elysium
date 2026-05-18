package com.biblioteca.reservas.dominio.estados;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDateTime;

import com.biblioteca.commons.excepciones.OperacionNoPermitidaEnEstadoReservaException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para el State Pattern de Reserva.
 * 
 * Valida que las transiciones de estado sean correctas y que
 * las operaciones ilícitas lancen excepciones apropiadas.
 */
@DisplayName("Pruebas del patrón State para Reserva")
class ReservaStateTest {
    
    private ReservaContexto contexto;
    private LocalDateTime ahora;
    
    @BeforeEach
    void setup() {
        ahora = LocalDateTime.now();
        contexto = new ReservaContexto(1, new ReservaEnEsperaState());
    }
    
    @DisplayName("EN_ESPERA: posición 1 puede notificar")
    @Test
    void testEnEsperaPosicion1PuedeNotificar() throws OperacionNoPermitidaEnEstadoReservaException {
        ReservaEnEsperaState estado = (ReservaEnEsperaState) contexto.getEstadoActual();
        contexto.setPosicionCola(1);
        
        estado.notificar(ahora, contexto);
        
        assertInstanceOf(ReservaNotificadaState.class, contexto.getEstadoActual());
        assertEquals(ahora, contexto.getFechaNotificacion());
        assertEquals(ahora.plusHours(24), contexto.getFechaExpiracion());
    }
    
    @DisplayName("EN_ESPERA: posición > 1 no puede notificar")
    @Test
    void testEnEsperaPosicionMayorNoNotifica() throws OperacionNoPermitidaEnEstadoReservaException {
        ReservaEnEsperaState estado = (ReservaEnEsperaState) contexto.getEstadoActual();
        contexto.setPosicionCola(2);
        
        OperacionNoPermitidaEnEstadoReservaException ex = assertThrows(
                OperacionNoPermitidaEnEstadoReservaException.class,
                () -> estado.notificar(ahora, contexto)
        );
        
        assertTrue(ex.getMessage().contains("posición 1"));
    }
    
    @DisplayName("EN_ESPERA: puede cancelar (transición a CANCELADA)")
    @Test
    void testEnEsperaPuedeCancelar() throws OperacionNoPermitidaEnEstadoReservaException {
        ReservaEnEsperaState estado = (ReservaEnEsperaState) contexto.getEstadoActual();
        
        estado.cancelar(contexto);
        
        assertInstanceOf(ReservaCanceladaState.class, contexto.getEstadoActual());
    }
    
    @DisplayName("NOTIFICADA: puede completar (transición a COMPLETADA)")
    @Test
    void testNotificadaPuedeCompletar() throws OperacionNoPermitidaEnEstadoReservaException {
        contexto.setEstadoActual(new ReservaNotificadaState());
        ReservaNotificadaState estado = (ReservaNotificadaState) contexto.getEstadoActual();
        
        estado.completar(contexto);
        
        assertInstanceOf(ReservaCompletadaState.class, contexto.getEstadoActual());
    }
    
    @DisplayName("NOTIFICADA: puede expirar (transición a EXPIRADA)")
    @Test
    void testNotificadaPuedeExpirar() throws OperacionNoPermitidaEnEstadoReservaException {
        contexto.setEstadoActual(new ReservaNotificadaState());
        ReservaNotificadaState estado = (ReservaNotificadaState) contexto.getEstadoActual();
        
        estado.expirar(contexto);
        
        assertInstanceOf(ReservaExpiradaState.class, contexto.getEstadoActual());
    }
    
    @DisplayName("NOTIFICADA: puede cancelar (transición a CANCELADA)")
    @Test
    void testNotificadaPuedeCancelar() throws OperacionNoPermitidaEnEstadoReservaException {
        contexto.setEstadoActual(new ReservaNotificadaState());
        ReservaNotificadaState estado = (ReservaNotificadaState) contexto.getEstadoActual();
        
        estado.cancelar(contexto);
        
        assertInstanceOf(ReservaCanceladaState.class, contexto.getEstadoActual());
    }
    
    @DisplayName("COMPLETADA: no puede expirar")
    @Test
    void testCompletadaNoExpira() throws OperacionNoPermitidaEnEstadoReservaException {
        contexto.setEstadoActual(new ReservaCompletadaState());
        ReservaCompletadaState estado = (ReservaCompletadaState) contexto.getEstadoActual();
        
        OperacionNoPermitidaEnEstadoReservaException ex = assertThrows(
                OperacionNoPermitidaEnEstadoReservaException.class,
                () -> estado.expirar(contexto)
        );
        
        assertTrue(ex.getMessage().contains("COMPLETADA"));
    }
    
    @DisplayName("CANCELADA: terminal, no puede hacer nada")
    @Test
    void testCanceladaTerminal() throws OperacionNoPermitidaEnEstadoReservaException {
        contexto.setEstadoActual(new ReservaCanceladaState());
        ReservaCanceladaState estado = (ReservaCanceladaState) contexto.getEstadoActual();
        
        assertThrows(
                OperacionNoPermitidaEnEstadoReservaException.class,
                () -> estado.notificar(ahora, contexto)
        );
        
        assertThrows(
                OperacionNoPermitidaEnEstadoReservaException.class,
                () -> estado.completar(contexto)
        );
    }
    
    @DisplayName("EXPIRADA: terminal, no puede hacer nada")
    @Test
    void testExpiradaTerminal() throws OperacionNoPermitidaEnEstadoReservaException {
        contexto.setEstadoActual(new ReservaExpiradaState());
        ReservaExpiradaState estado = (ReservaExpiradaState) contexto.getEstadoActual();
        
        assertThrows(
                OperacionNoPermitidaEnEstadoReservaException.class,
                () -> estado.completar(contexto)
        );
        
        assertThrows(
                OperacionNoPermitidaEnEstadoReservaException.class,
                () -> estado.cancelar(contexto)
        );
    }
}
