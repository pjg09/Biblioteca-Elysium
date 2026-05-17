package com.biblioteca.cobros.aplicacion.facades;

import com.biblioteca.cobros.aplicacion.CobroService;
import com.biblioteca.cobros.aplicacion.dto.RegistrarPagoRequest;
import com.biblioteca.cobros.infraestructura.persistencia.RegistroPagoEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Implementación de ICobrosFacade.
 * 
 * Delega operaciones al CobroService subyacente.
 */
@Component
public class CobrosFacade implements ICobrosFacade {
    
    private final CobroService cobroService;
    
    public CobrosFacade(CobroService cobroService) {
        this.cobroService = cobroService;
    }
    
    @Override
    public RegistroPagoEntity registrarPago(RegistrarPagoRequest request) {
        return cobroService.registrarPago(request);
    }
    
    @Override
    public Optional<RegistroPagoEntity> obtenerPorId(String id) {
        return cobroService.obtenerPorId(id);
    }
    
    @Override
    public List<RegistroPagoEntity> obtenerPorUsuario(String usuarioId) {
        return cobroService.obtenerPorUsuario(usuarioId);
    }
}
