package com.biblioteca.dominio;

import com.biblioteca.dominio.entidades.Multa;
import com.biblioteca.dominio.enumeraciones.TipoMulta;
import com.biblioteca.dominio.enumeraciones.TipoUsuario;
import com.biblioteca.dominio.objetosvalor.ContextoMulta;
import com.biblioteca.servicios.calculadores.CalculadorMultaPorRetraso;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CalculadorMultaPorRetrasoTest {

    private CalculadorMultaPorRetraso calculador;

    @BeforeEach
    void setUp() {
        calculador = new CalculadorMultaPorRetraso();
    }

    @Test
    void calcular_retornaNull_siDiasRetrasoEsCero() {
        ContextoMulta contexto = new ContextoMulta.Builder()
            .conPrestamo("PRE-001").conUsuario("USR-001").conMaterial("MAT-001")
            .deTipo(TipoMulta.POR_RETRASO)
            .conDiasRetraso(0)
            .conTipoUsuario(TipoUsuario.ESTUDIANTE)
            .build();

        assertNull(calculador.calcular(contexto));
    }

    @Test
    void calcular_aplicaMultiplicadorEstudiante() {
        ContextoMulta contexto = new ContextoMulta.Builder()
            .conPrestamo("PRE-001").conUsuario("USR-001").conMaterial("MAT-001")
            .deTipo(TipoMulta.POR_RETRASO)
            .conDiasRetraso(5)
            .conTipoUsuario(TipoUsuario.ESTUDIANTE)
            .build();

        Multa multa = calculador.calcular(contexto);
        assertNotNull(multa);
        // Tarifa base LIBRO_NORMAL = 1000 * multiplicador ESTUDIANTE (1.0) = 1000 por día
        assertEquals(5000.0, multa.calcularMontoTotal(), 0.01);
    }

    @Test
    void calcular_aplicaDescuentoProfesor() {
        ContextoMulta contexto = new ContextoMulta.Builder()
            .conPrestamo("PRE-001").conUsuario("USR-001").conMaterial("MAT-001")
            .deTipo(TipoMulta.POR_RETRASO)
            .conDiasRetraso(4)
            .conTipoUsuario(TipoUsuario.PROFESOR) // multiplicador 0.5
            .build();

        Multa multa = calculador.calcular(contexto);
        assertNotNull(multa);
        assertEquals(2000.0, multa.calcularMontoTotal(), 0.01); // 4 * 1000 * 0.5
    }

    @Test
    void calcular_sinMultaParaInvestigador() {
        ContextoMulta contexto = new ContextoMulta.Builder()
            .conPrestamo("PRE-001").conUsuario("USR-001").conMaterial("MAT-001")
            .deTipo(TipoMulta.POR_RETRASO)
            .conDiasRetraso(10)
            .conTipoUsuario(TipoUsuario.INVESTIGADOR) // multiplicador 0.0
            .build();

        Multa multa = calculador.calcular(contexto);
        assertNotNull(multa);
        assertEquals(0.0, multa.calcularMontoTotal(), 0.01);
    }

    @Test
    void calcular_aplicaRecargoPublicoGeneral() {
        ContextoMulta contexto = new ContextoMulta.Builder()
            .conPrestamo("PRE-001").conUsuario("USR-001").conMaterial("MAT-001")
            .deTipo(TipoMulta.POR_RETRASO)
            .conDiasRetraso(2)
            .conTipoUsuario(TipoUsuario.PUBLICO_GENERAL) // multiplicador 1.5
            .build();

        Multa multa = calculador.calcular(contexto);
        assertNotNull(multa);
        assertEquals(3000.0, multa.calcularMontoTotal(), 0.01); // 2 * 1000 * 1.5
    }

    @Test
    void puedeCalcular_soloParaTipoRetraso() {
        ContextoMulta contextoRetraso = new ContextoMulta.Builder()
            .deTipo(TipoMulta.POR_RETRASO).build();
        ContextoMulta contextoDano = new ContextoMulta.Builder()
            .deTipo(TipoMulta.POR_DANO).build();

        assertTrue(calculador.puedeCalcular(contextoRetraso));
        assertFalse(calculador.puedeCalcular(contextoDano));
    }
}
