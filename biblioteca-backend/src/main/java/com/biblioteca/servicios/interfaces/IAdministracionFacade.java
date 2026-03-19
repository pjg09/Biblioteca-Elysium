package com.biblioteca.servicios.interfaces;

import com.biblioteca.dominio.entidades.Material;
import com.biblioteca.dominio.entidades.Usuario;
import com.biblioteca.dominio.objetosvalor.Resultado;

public interface IAdministracionFacade {
    Resultado agregarMaterial(Material material);
    Resultado actualizarMaterial(Material material);
    Resultado agregarUsuario(Usuario usuario);
    Resultado bloquearUsuario(String idUsuario, String motivo);
    Resultado desbloquearUsuario(String idUsuario);
    Resultado pagarMulta(String idMulta);
}

