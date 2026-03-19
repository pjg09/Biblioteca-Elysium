package com.biblioteca.servicios;

import com.biblioteca.dominio.entidades.Material;
import com.biblioteca.dominio.entidades.Usuario;
import com.biblioteca.dominio.objetosvalor.Resultado;
import com.biblioteca.repositorios.IRepositorio;
import com.biblioteca.servicios.interfaces.IAdministracionFacade;
import com.biblioteca.servicios.interfaces.IGestorBloqueoService;
import com.biblioteca.servicios.interfaces.IGestorMultasService;

public class AdministracionFacade implements IAdministracionFacade {

    private final IRepositorio<Material> repoMaterial;
    private final IRepositorio<Usuario> repoUsuario;
    private final IGestorBloqueoService gestorBloqueo;
    private final IGestorMultasService gestorMultas;

    public AdministracionFacade(
            IRepositorio<Material> repoMaterial,
            IRepositorio<Usuario> repoUsuario,
            IGestorBloqueoService gestorBloqueo,
            IGestorMultasService gestorMultas) {
        this.repoMaterial = repoMaterial;
        this.repoUsuario = repoUsuario;
        this.gestorBloqueo = gestorBloqueo;
        this.gestorMultas = gestorMultas;
    }

    @Override
    public Resultado agregarMaterial(Material material) {
        return repoMaterial.agregar(material);
    }

    @Override
    public Resultado actualizarMaterial(Material material) {
        return repoMaterial.actualizar(material);
    }

    @Override
    public Resultado agregarUsuario(Usuario usuario) {
        return repoUsuario.agregar(usuario);
    }

    @Override
    public Resultado bloquearUsuario(String idUsuario, String motivo) {
        return gestorBloqueo.bloquearUsuario(idUsuario, motivo);
    }

    @Override
    public Resultado desbloquearUsuario(String idUsuario) {
        return gestorBloqueo.desbloquearUsuario(idUsuario);
    }

    @Override
    public Resultado pagarMulta(String idMulta) {
        return gestorMultas.pagarMulta(idMulta);
    }
}
