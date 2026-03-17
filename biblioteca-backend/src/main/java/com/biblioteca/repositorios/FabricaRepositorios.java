package com.biblioteca.repositorios;

import com.biblioteca.dominio.entidades.Material;
import com.biblioteca.dominio.entidades.Multa;
import com.biblioteca.dominio.entidades.Prestamo;
import com.biblioteca.dominio.entidades.Reserva;
import com.biblioteca.dominio.entidades.Usuario;

/**
 * Fabrica de Repositorios (Patrón Factory)
 * Permite cambiar la implementación de almacenamiento sin modificar la lógica de negocio.
 */
public class FabricaRepositorios {
    
    // Podría configurarse desde properties o variables de entorno
    private static final String TIPO_REPOSITORIO = "MEMORIA";
    
    public static IRepositorio<Material> crearRepositorioMaterial() {
        if ("MEMORIA".equals(TIPO_REPOSITORIO)) return new RepositorioMaterialEnMemoria();
        throw new UnsupportedOperationException("Tipo no soportado");
    }
    
    public static IRepositorio<Usuario> crearRepositorioUsuario() {
        if ("MEMORIA".equals(TIPO_REPOSITORIO)) return new RepositorioUsuarioEnMemoria();
        throw new UnsupportedOperationException("Tipo no soportado");
    }
    
    public static IRepositorio<Prestamo> crearRepositorioPrestamo() {
        if ("MEMORIA".equals(TIPO_REPOSITORIO)) return new RepositorioPrestamoEnMemoria();
        throw new UnsupportedOperationException("Tipo no soportado");
    }
    
    public static IRepositorio<Reserva> crearRepositorioReserva() {
        if ("MEMORIA".equals(TIPO_REPOSITORIO)) return new RepositorioReservaEnMemoria();
        throw new UnsupportedOperationException("Tipo no soportado");
    }
    
    public static IRepositorio<Multa> crearRepositorioMulta() {
        if ("MEMORIA".equals(TIPO_REPOSITORIO)) return new RepositorioMultaEnMemoria();
        throw new UnsupportedOperationException("Tipo no soportado");
    }
}
