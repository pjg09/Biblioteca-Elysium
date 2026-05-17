package com.biblioteca.circulacion.aplicacion.facades;

import com.biblioteca.circulacion.aplicacion.CirculacionService;
import com.biblioteca.circulacion.aplicacion.dto.DevolverMaterialRequest;
import com.biblioteca.circulacion.aplicacion.dto.RegistrarPrestamoRequest;
import com.biblioteca.circulacion.aplicacion.dto.ResultadoOperacion;
import org.springframework.stereotype.Component;

/**
 * Implementación de ICirculacionFacade.
 * 
 * Delega todas las operaciones al CirculacionService subyacente.
 * En una arquitectura más compleja, este Facade podría:
 * - Coordinar múltiples servicios
 * - Aplicar lógica transaccional a nivel de Facade
 * - Publicar eventos de Saga para operaciones distribuidas
 * 
 * Por ahora, proporciona un punto de entrada único y testeable.
 */
@Component
public class CirculacionFacade implements ICirculacionFacade {
    
    private final CirculacionService circulacionService;
    
    public CirculacionFacade(CirculacionService circulacionService) {
        this.circulacionService = circulacionService;
    }
    
    @Override
    public ResultadoOperacion registrarPrestamo(RegistrarPrestamoRequest request) {
        return circulacionService.registrarPrestamo(request);
    }
    
    @Override
    public ResultadoOperacion registrarDevolucion(String idPrestamo, DevolverMaterialRequest request) {
        return circulacionService.registrarDevolucion(idPrestamo, request);
    }
    
    @Override
    public ResultadoOperacion renovarPrestamo(String idPrestamo) {
        return circulacionService.renovarPrestamo(idPrestamo);
    }
}
