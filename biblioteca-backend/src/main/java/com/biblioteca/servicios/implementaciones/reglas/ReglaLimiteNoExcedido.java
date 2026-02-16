package com.biblioteca.servicios.implementaciones.reglas;

import com.biblioteca.dominio.entidades.Usuario;
import com.biblioteca.dominio.objetosvalor.ResultadoValidacion;
import com.biblioteca.servicios.implementaciones.ValidadorReglasService.ContextoValidacion;
import com.biblioteca.servicios.interfaces.ILimitePrestamoService;
import com.biblioteca.servicios.interfaces.IReglaValidacion;

public class ReglaLimiteNoExcedido implements IReglaValidacion {
    
    private ILimitePrestamoService limiteService;
    
    public ReglaLimiteNoExcedido(ILimitePrestamoService limiteService) {
        this.limiteService = limiteService;
    }
    
    @Override
    public ResultadoValidacion validar(ContextoValidacion contexto) {
        if (!contexto.tieneUsuario()) {
            return ResultadoValidacion.Invalido("Usuario no encontrado en el contexto");
        }
        
        Usuario usuario = contexto.getUsuario();
        
        return limiteService.validarLimite(usuario.getId(), usuario.getTipo());
    }
    
    @Override
    public int obtenerPrioridad() {
        return 4;
    }
}