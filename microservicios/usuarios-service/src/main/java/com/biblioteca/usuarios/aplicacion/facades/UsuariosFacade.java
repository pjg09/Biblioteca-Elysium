package com.biblioteca.usuarios.aplicacion.facades;

import com.biblioteca.usuarios.aplicacion.UsuarioService;
import com.biblioteca.usuarios.aplicacion.dto.CrearUsuarioRequest;
import com.biblioteca.usuarios.aplicacion.dto.EstadoUsuarioDTO;
import com.biblioteca.usuarios.aplicacion.dto.LimitePrestamoDTO;
import com.biblioteca.usuarios.infraestructura.persistencia.UsuarioEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Implementación de IUsuariosFacade.
 * 
 * Delega todas las operaciones al UsuarioService subyacente.
 */
@Component
public class UsuariosFacade implements IUsuariosFacade {
    
    private final UsuarioService usuarioService;
    
    public UsuariosFacade(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }
    
    @Override
    public Optional<UsuarioEntity> obtenerPorId(String id) {
        return usuarioService.obtenerPorId(id);
    }
    
    @Override
    public EstadoUsuarioDTO consultarEstado(String id) {
        return usuarioService.consultarEstado(id);
    }
    
    @Override
    public LimitePrestamoDTO consultarLimite(String id) {
        return usuarioService.consultarLimite(id);
    }
    
    @Override
    public UsuarioEntity registrarUsuario(CrearUsuarioRequest request) {
        return usuarioService.registrarUsuario(request);
    }
    
    @Override
    public List<UsuarioEntity> listarTodos() {
        return usuarioService.listarTodos();
    }
}
