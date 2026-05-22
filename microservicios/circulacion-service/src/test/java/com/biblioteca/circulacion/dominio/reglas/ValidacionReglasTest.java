package com.biblioteca.circulacion.dominio.reglas;

import com.biblioteca.circulacion.dominio.objetosvalor.ContextoValidacionPrestamo;
import com.biblioteca.circulacion.dominio.servicios.ValidadorReglasService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para las Reglas de Validación (IReglaValidacion).
 * 
 * Valida que cada regla tome decisiones correctas según el contexto.
 */
@DisplayName("Pruebas de Validación de Reglas de Préstamo")
class ValidacionReglasTest {
    
    private ContextoValidacionPrestamo contexto;
    
    @BeforeEach
    void setup() {
        contexto = new ContextoValidacionPrestamo();
        contexto.setIdUsuario("USER-001");
        contexto.setIdMaterial("MAT-001");
    }
    
    @Nested
    @DisplayName("ReglaUsuarioActivo")
    class ReglaUsuarioActivoTests {
        
        @DisplayName("Pasa si usuario está activo")
        @Test
        void testPasaSiUsuarioActivo() {
            ReglaUsuarioActivo regla = new ReglaUsuarioActivo();
            contexto.setUsuarioActivo(true);
            
            assertTrue(regla.validar(contexto));
        }
        
        @DisplayName("Falla si usuario está inactivo")
        @Test
        void testFallaSiUsuarioInactivo() {
            ReglaUsuarioActivo regla = new ReglaUsuarioActivo();
            contexto.setUsuarioActivo(false);
            
            assertFalse(regla.validar(contexto));
            assertNotNull(regla.obtenerMensajeError());
            assertTrue(regla.obtenerMensajeError().contains("no está activo"));
        }
    }
    
    @Nested
    @DisplayName("ReglaMaterialDisponible")
    class ReglaMaterialDisponibleTests {
        
        @DisplayName("Pasa si material está disponible")
        @Test
        void testPasaSiMaterialDisponible() {
            ReglaMaterialDisponible regla = new ReglaMaterialDisponible();
            contexto.setMaterialDisponible(true);
            contexto.setEstadoMaterial("DISPONIBLE");
            
            assertTrue(regla.validar(contexto));
        }
        
        @DisplayName("Falla si material no está disponible")
        @Test
        void testFallaSiMaterialNoDisponible() {
            ReglaMaterialDisponible regla = new ReglaMaterialDisponible();
            contexto.setMaterialDisponible(false);
            contexto.setEstadoMaterial("PRESTADO");
            
            assertFalse(regla.validar(contexto));
            assertNotNull(regla.obtenerMensajeError());
            assertTrue(regla.obtenerMensajeError().contains("PRESTADO"));
        }
    }
    
    @Nested
    @DisplayName("ReglaLimitePrestamos")
    class ReglaLimitePermitidosTests {
        
        @DisplayName("Pasa si préstamos activos < límite")
        @Test
        void testPasaSiDentroDelLimite() {
            ReglaLimitePrestamos regla = new ReglaLimitePrestamos();
            contexto.setPrestamosActivos(2);
            contexto.setLimiteMaximoPrestamos(3);
            
            assertTrue(regla.validar(contexto));
        }
        
        @DisplayName("Falla si préstamos activos >= límite")
        @Test
        void testFallaSiAlcanzaLimite() {
            ReglaLimitePrestamos regla = new ReglaLimitePrestamos();
            contexto.setPrestamosActivos(3);
            contexto.setLimiteMaximoPrestamos(3);
            
            assertFalse(regla.validar(contexto));
            assertNotNull(regla.obtenerMensajeError());
            assertTrue(regla.obtenerMensajeError().contains("alcanzado"));
        }
    }
    
    @Nested
    @DisplayName("ReglaUsuarioNoMoroso")
    class ReglaUsuarioNoMorosoTests {
        
        @DisplayName("Pasa si deuda = 0")
        @Test
        void testPasaSiSinDeuda() {
            ReglaUsuarioNoMoroso regla = new ReglaUsuarioNoMoroso();
            contexto.setDeudaPendiente(0.0);
            
            assertTrue(regla.validar(contexto));
        }
        
        @DisplayName("Falla si deuda > 0")
        @Test
        void testFallaSiTieneDeuda() {
            ReglaUsuarioNoMoroso regla = new ReglaUsuarioNoMoroso();
            contexto.setDeudaPendiente(5000.0);
            
            assertFalse(regla.validar(contexto));
            assertNotNull(regla.obtenerMensajeError());
            assertTrue(regla.obtenerMensajeError().contains("5000"));
        }
    }
    
    @Nested
    @DisplayName("ValidadorReglasService")
    class ValidadorReglasServiceTests {
        
        @DisplayName("Todas las reglas pasan: validación exitosa")
        @Test
        void testTodasReglasValidas() {
            ValidadorReglasService validador = new ValidadorReglasService();
            contexto.setUsuarioActivo(true);
            contexto.setMaterialDisponible(true);
            contexto.setPrestamosActivos(2);
            contexto.setLimiteMaximoPrestamos(3);
            contexto.setDeudaPendiente(0.0);
            
            validador.agregar(new ReglaUsuarioActivo())
                     .agregar(new ReglaMaterialDisponible())
                     .agregar(new ReglaLimitePrestamos())
                     .agregar(new ReglaUsuarioNoMoroso());
            
            assertTrue(validador.validar(contexto));
        }
        
        @DisplayName("Primer regla falla: detiene validación")
        @Test
        void testPrimeraReglaFalla() {
            ValidadorReglasService validador = new ValidadorReglasService();
            contexto.setUsuarioActivo(false); // Esta falla
            contexto.setMaterialDisponible(true);
            contexto.setPrestamosActivos(2);
            contexto.setLimiteMaximoPrestamos(3);
            contexto.setDeudaPendiente(0.0);
            
            validador.agregar(new ReglaUsuarioActivo())
                     .agregar(new ReglaMaterialDisponible())
                     .agregar(new ReglaLimitePrestamos())
                     .agregar(new ReglaUsuarioNoMoroso());
            
            assertFalse(validador.validar(contexto));
            String mensajeError = validador.obtenerMensajeError(contexto);
            assertTrue(mensajeError.contains("no está activo"));
        }
        
        @DisplayName("Regla intermedia falla: detiene en esa regla")
        @Test
        void testReglaIntermediaFalla() {
            ValidadorReglasService validador = new ValidadorReglasService();
            contexto.setUsuarioActivo(true);
            contexto.setMaterialDisponible(false); // Esta falla
            contexto.setPrestamosActivos(2);
            contexto.setLimiteMaximoPrestamos(3);
            contexto.setDeudaPendiente(0.0);
            
            validador.agregar(new ReglaUsuarioActivo())
                     .agregar(new ReglaMaterialDisponible())
                     .agregar(new ReglaLimitePrestamos())
                     .agregar(new ReglaUsuarioNoMoroso());
            
            assertFalse(validador.validar(contexto));
            String mensajeError = validador.obtenerMensajeError(contexto);
            assertTrue(mensajeError.contains("no está disponible"));
        }
    }
}
