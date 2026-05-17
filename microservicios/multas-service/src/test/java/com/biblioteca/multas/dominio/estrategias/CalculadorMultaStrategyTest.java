package com.biblioteca.multas.dominio.estrategias;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para el Strategy Pattern de CalculadorMulta.
 * 
 * Valida que cada estrategia calcule correctamente las multas
 * según el tipo de usuario.
 */
@DisplayName("Pruebas del patrón Strategy para Cálculo de Multas")
class CalculadorMultaStrategyTest {
    
    private static final double EPSILON = 0.01;
    private static final double VALOR_MATERIAL = 10000.0;
    
    @Nested
    @DisplayName("CalculadorMultaEstudiante")
    class EstudianteTests {
        
        private final CalculadorMultaEstudiante calculador = new CalculadorMultaEstudiante();
        
        @DisplayName("Calcula tarifa de retraso: 1000 por día")
        @Test
        void testTarifaRetrasoEstudiante() {
            double resultado = calculador.calcularMontoRetraso(5);
            assertEquals(5000.0, resultado, EPSILON);
        }
        
        @DisplayName("Calcula multa por pérdida: valor + 20%")
        @Test
        void testMontoPerdidaEstudiante() {
            double resultado = calculador.calcularMontoPerdida(VALOR_MATERIAL);
            assertEquals(12000.0, resultado, EPSILON);
        }
        
        @ParameterizedTest
        @ValueSource(strings = {"LEVE", "MODERADO", "GRAVE", "IRREPARABLE"})
        @DisplayName("Calcula multa por daño según gravedad")
        void testMontoDanoEstudiante(String gravedad) {
            double resultado = calculador.calcularMontoDano(VALOR_MATERIAL, gravedad);
            
            switch (gravedad) {
                case "LEVE" -> assertEquals(2000.0, resultado, EPSILON);
                case "MODERADO" -> assertEquals(4000.0, resultado, EPSILON);
                case "GRAVE" -> assertEquals(7000.0, resultado, EPSILON);
                case "IRREPARABLE" -> assertEquals(10000.0, resultado, EPSILON);
            }
        }
    }
    
    @Nested
    @DisplayName("CalculadorMultaProfesor")
    class ProfesorTests {
        
        private final CalculadorMultaProfesor calculador = new CalculadorMultaProfesor();
        
        @DisplayName("Calcula tarifa de retraso: 500 por día (50% descuento)")
        @Test
        void testTarifaRetrasoProfesor() {
            double resultado = calculador.calcularMontoRetraso(5);
            assertEquals(2500.0, resultado, EPSILON);
        }
        
        @DisplayName("Calcula multa por pérdida: valor + 10% (menor recargo)")
        @Test
        void testMontoPerdidaProfesor() {
            double resultado = calculador.calcularMontoPerdida(VALOR_MATERIAL);
            assertEquals(11000.0, resultado, EPSILON);
        }
    }
    
    @Nested
    @DisplayName("CalculadorMultaInvestigador")
    class InvestigadorTests {
        
        private final CalculadorMultaInvestigador calculador = new CalculadorMultaInvestigador();
        
        @DisplayName("No cobra multa por retraso (privilegio académico)")
        @Test
        void testTarifaRetrasoInvestigador() {
            double resultado = calculador.calcularMontoRetraso(100);
            assertEquals(0.0, resultado, EPSILON);
        }
        
        @DisplayName("Calcula multa por pérdida: solo valor sin recargo")
        @Test
        void testMontoPerdidaInvestigador() {
            double resultado = calculador.calcularMontoPerdida(VALOR_MATERIAL);
            assertEquals(VALOR_MATERIAL, resultado, EPSILON);
        }
    }
    
    @Nested
    @DisplayName("CalculadorMultaPublico")
    class PublicoTests {
        
        private final CalculadorMultaPublico calculador = new CalculadorMultaPublico();
        
        @DisplayName("Calcula tarifa de retraso: 1500 por día (tarifa premium)")
        @Test
        void testTarifaRetrasoPublico() {
            double resultado = calculador.calcularMontoRetraso(5);
            assertEquals(7500.0, resultado, EPSILON);
        }
        
        @DisplayName("Calcula multa por pérdida: valor + 30% (mayor recargo)")
        @Test
        void testMontoPerdidaPublico() {
            double resultado = calculador.calcularMontoPerdida(VALOR_MATERIAL);
            assertEquals(13000.0, resultado, EPSILON);
        }
    }
    
    @Nested
    @DisplayName("CalculadorMultaContext")
    class ContextTests {
        
        @DisplayName("Selecciona estrategia correcta por tipo de usuario")
        @Test
        void testContextSeleccionaEstrategia() {
            CalculadorMultaContext context = new CalculadorMultaContext();
            
            assertTrue(context.establecerEstrategia("ESTUDIANTE"));
            assertInstanceOf(CalculadorMultaEstudiante.class, context.getCalculador());
            
            assertTrue(context.establecerEstrategia("PROFESOR"));
            assertInstanceOf(CalculadorMultaProfesor.class, context.getCalculador());
            
            assertTrue(context.establecerEstrategia("INVESTIGADOR"));
            assertInstanceOf(CalculadorMultaInvestigador.class, context.getCalculador());
            
            assertTrue(context.establecerEstrategia("PUBLICO_GENERAL"));
            assertInstanceOf(CalculadorMultaPublico.class, context.getCalculador());
        }
        
        @DisplayName("Lanza excepción si estrategia no inicializada")
        @Test
        void testContextSinEstrategiaLanzaExcepcion() {
            CalculadorMultaContext context = new CalculadorMultaContext();
            
            assertThrows(IllegalStateException.class,
                    () -> context.calcularMontoRetraso(5));
        }
        
        @DisplayName("Defecto: ESTUDIANTE para tipo nulo")
        @Test
        void testContextDefaultEstudiante() {
            CalculadorMultaContext context = new CalculadorMultaContext();
            
            assertTrue(context.establecerEstrategia(null));
            assertInstanceOf(CalculadorMultaEstudiante.class, context.getCalculador());
        }
    }
}
