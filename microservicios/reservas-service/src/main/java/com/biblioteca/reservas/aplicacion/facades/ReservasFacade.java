package com.biblioteca.reservas.aplicacion.facades;

import com.biblioteca.reservas.aplicacion.ReservaService;
import com.biblioteca.reservas.aplicacion.dto.CrearReservaRequest;
import com.biblioteca.reservas.infraestructura.persistencia.ReservaEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Implementación de IReservasFacade.
 * 
 * Delega todas las operaciones al ReservaService subyacente.
 */
@Component
public class ReservasFacade implements IReservasFacade {
    
    private final ReservaService reservaService;
    
    public ReservasFacade(ReservaService reservaService) {
        this.reservaService = reservaService;
    }
    
    @Override
    public ReservaEntity crearReserva(CrearReservaRequest request) {
        return reservaService.crearReserva(request);
    }
    
    @Override
    public ReservaEntity cancelarReserva(String id) throws com.biblioteca.commons.excepciones.OperacionNoPermitidaEnEstadoReservaException {
        return reservaService.cancelarReserva(id);
    }
    
    @Override
    public Optional<ReservaEntity> obtenerPorId(String id) {
        return reservaService.obtenerPorId(id);
    }
    
    @Override
    public List<ReservaEntity> listarPorMaterial(String idMaterial) {
        return reservaService.listarPorMaterial(idMaterial);
    }
    
    @Override
    public List<ReservaEntity> listarPorUsuario(String idUsuario) {
        return reservaService.listarPorUsuario(idUsuario);
    }
    
    @Override
    public void reorganizarCola(String idMaterial) {
        reservaService.reorganizarCola(idMaterial);
    }
}
