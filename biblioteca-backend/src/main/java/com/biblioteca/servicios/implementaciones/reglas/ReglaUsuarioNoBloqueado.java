package com.biblioteca.servicios.implementaciones.reglas;

import com.biblioteca.dominio.objetosvalor.ResultadoValidacion;
import com.biblioteca.servicios.implementaciones.ValidadorReglasService.ContextoValidacion;
import com.biblioteca.servicios.interfaces.IGestorBloqueoService;
import com.biblioteca.servicios.interfaces.IReglaValidacion;

public class ReglaUsuarioNoBloqueado implements IReglaValidacion {
    
    private IGestorBloqueoService bloqueoService;
    
    public ReglaUsuarioNoBloqueado(IGestorBloqueoService bloqueoService) {
        this.bloqueoService = bloqueoService;
    }
    
    @Override
    public ResultadoValidacion validar(ContextoValidacion contexto) {
        if (!contexto.tieneUsuario()) {
            return ResultadoValidacion.Invalido("Usuario no encontrado en el contexto");
        }
        
        return bloqueoService.verificarSiDebeBloquear(contexto.getIdUsuario());
    }
    
    @Override
    public int obtenerPrioridad() {
        return 2; // Después de verificar que existe
    }
}