package com.biblioteca.servicios.implementaciones.reglas;

import com.biblioteca.dominio.entidades.Usuario;
import com.biblioteca.dominio.enumeraciones.EstadoUsuario;
import com.biblioteca.dominio.objetosvalor.ResultadoValidacion;
import com.biblioteca.servicios.implementaciones.ValidadorReglasService.ContextoValidacion;
import com.biblioteca.servicios.interfaces.IReglaValidacion;

public class ReglaUsuarioActivo implements IReglaValidacion {
    
    @Override
    public ResultadoValidacion validar(ContextoValidacion contexto) {
        if (!contexto.tieneUsuario()) {
            return ResultadoValidacion.Invalido("Usuario no encontrado en el contexto");
        }
        
        Usuario usuario = contexto.getUsuario();
        
        if (usuario.getEstado() != EstadoUsuario.ACTIVO) {
            return ResultadoValidacion.Invalido(
                "Usuario no está activo. Estado actual: " + usuario.getEstado()
            );
        }
        
        return ResultadoValidacion.Valido();
    }
    
    @Override
    public int obtenerPrioridad() {
        return 1; // Máxima prioridad
    }
}