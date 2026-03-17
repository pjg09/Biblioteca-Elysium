package com.biblioteca.repositorios;

import com.biblioteca.dominio.entidades.Material;
import com.biblioteca.dominio.entidades.Multa;
import com.biblioteca.dominio.entidades.Prestamo;
import com.biblioteca.dominio.entidades.Reserva;
import com.biblioteca.dominio.entidades.Usuario;

/**
 * Fábrica abstracta para crear repositorios.
 * Permite cambiar la implementación de almacenamiento (ej. en memoria, base de datos)
 * sin afectar la lógica de negocio.
 */
public interface IRepositorioFactory {
    IRepositorio<Material> crearRepositorioMaterial();
    IRepositorio<Usuario> crearRepositorioUsuario();
    IRepositorio<Prestamo> crearRepositorioPrestamo();
    IRepositorio<Reserva> crearRepositorioReserva();
    IRepositorio<Multa> crearRepositorioMulta();
}
