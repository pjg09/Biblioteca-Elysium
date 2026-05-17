package com.biblioteca.multas.aplicacion.facades;

import com.biblioteca.multas.aplicacion.MultaService;
import com.biblioteca.multas.aplicacion.dto.DeudaPendienteDTO;
import com.biblioteca.multas.dominio.estados.OperacionNoPermitidaEnEstadoMultaException;
import com.biblioteca.multas.infraestructura.persistencia.MultaEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Implementación de IMult asFacade.
 * 
 * Delega operaciones al MultaService y coordina cambios de estado.
 */
@Component
public class MultasFacade implements IMult asFacade {
    
    private final MultaService multaService;
    
    public MultasFacade(MultaService multaService) {
        this.multaService = multaService;
    }
    
    @Override
    public Optional<MultaEntity> obtenerPorId(String id) {
        return multaService.obtenerPorId(id);
    }
    
    @Override
    public List<MultaEntity> obtenerPorUsuario(String idUsuario) {
        return multaService.obtenerPorUsuario(idUsuario);
    }
    
    @Override
    public DeudaPendienteDTO consultarDeudaPendiente(String idUsuario) {
        return multaService.consultarDeudaPendiente(idUsuario);
    }
    
    @Override
    public List<MultaEntity> obtenerPorPrestamo(String idPrestamo) {
        return multaService.obtenerPorPrestamo(idPrestamo);
    }
    
    @Override
    public List<MultaEntity> obtenerPorUsuarioYEstado(String idUsuario, String estado) {
        return multaService.obtenerPorUsuarioYEstado(idUsuario, estado);
    }
    
    @Override
    public MultaEntity pagarMulta(String id) throws OperacionNoPermitidaEnEstadoMultaException {
        return multaService.pagarMulta(id);
    }
    
    @Override
    public MultaEntity condonarMulta(String id) throws OperacionNoPermitidaEnEstadoMultaException {
        return multaService.condonarMulta(id);
    }
}
