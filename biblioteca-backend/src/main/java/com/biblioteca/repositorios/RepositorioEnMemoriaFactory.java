package com.biblioteca.repositorios;

import com.biblioteca.dominio.entidades.Material;
import com.biblioteca.dominio.entidades.Multa;
import com.biblioteca.dominio.entidades.Prestamo;
import com.biblioteca.dominio.entidades.Reserva;
import com.biblioteca.dominio.entidades.Usuario;

/**
 * Implementación de la fábrica para repositorios en memoria (Volátiles).
 */
public class RepositorioEnMemoriaFactory implements IRepositorioFactory {
    
    @Override
    public IRepositorio<Material> crearRepositorioMaterial() {
        return new RepositorioMaterialEnMemoria();
    }
    
    @Override
    public IRepositorio<Usuario> crearRepositorioUsuario() {
        return new RepositorioUsuarioEnMemoria();
    }
    
    @Override
    public IRepositorio<Prestamo> crearRepositorioPrestamo() {
        return new RepositorioPrestamoEnMemoria();
    }
    
    @Override
    public IRepositorio<Reserva> crearRepositorioReserva() {
        return new RepositorioReservaEnMemoria();
    }
    
    @Override
    public IRepositorio<Multa> crearRepositorioMulta() {
        return new RepositorioMultaEnMemoria();
    }
}
