package com.biblioteca.servicios.implementaciones;

import java.util.HashMap;
import java.util.Map;

import com.biblioteca.dominio.entidades.Prestamo;
import com.biblioteca.dominio.entidades.Usuario;
import com.biblioteca.dominio.enumeraciones.EstadoTransaccion;
import com.biblioteca.dominio.enumeraciones.TipoUsuario;
import com.biblioteca.dominio.objetosvalor.ResultadoValidacion;
import com.biblioteca.repositorios.IRepositorio;
import com.biblioteca.servicios.interfaces.ILimitePrestamoService;

public class LimitePorTipoUsuarioService implements ILimitePrestamoService {
    
    private final IRepositorio<Usuario> repoUsuario;
    private final IRepositorio<Prestamo> repoPrestamo;
    private final Map<TipoUsuario, Integer> limitesPorTipo;
    
    public LimitePorTipoUsuarioService(
            IRepositorio<Usuario> repoUsuario,
            IRepositorio<Prestamo> repoPrestamo) {
        
        this.repoUsuario = repoUsuario;
        this.repoPrestamo = repoPrestamo;
        this.limitesPorTipo = new HashMap<>();
        inicializarLimites();
    }
    
    private void inicializarLimites() {
        limitesPorTipo.put(TipoUsuario.ESTUDIANTE, 3);
        limitesPorTipo.put(TipoUsuario.PROFESOR, 5);
        limitesPorTipo.put(TipoUsuario.INVESTIGADOR, 7);
        limitesPorTipo.put(TipoUsuario.PUBLICO_GENERAL, 2);
    }
    
    @Override
    public ResultadoValidacion validarLimite(String idUsuario, TipoUsuario tipoUsuario) {
        
        if (idUsuario == null || idUsuario.trim().isEmpty()) {
            return ResultadoValidacion.Invalido("ID de usuario no puede estar vacío");
        }
        
        Usuario usuario = repoUsuario.obtenerPorId(idUsuario);
        if (usuario == null) {
            return ResultadoValidacion.Invalido("Usuario no encontrado");
        }
        
        int limiteMaximo = obtenerLimiteMaximo(tipoUsuario);
        int prestamosActuales = cantidadActualPrestada(idUsuario);
        
        if (prestamosActuales >= limiteMaximo) {
            return ResultadoValidacion.Invalido(
                "Límite de préstamos excedido. Límite: " + limiteMaximo + 
                ", Actual: " + prestamosActuales
            );
        }
        
        return ResultadoValidacion.Valido();
    }
    
    @Override
    public int obtenerLimiteMaximo(TipoUsuario tipoUsuario) {
        return limitesPorTipo.getOrDefault(tipoUsuario, 0);
    }
    
    @Override
    public int cantidadActualPrestada(String idUsuario) {
        if (idUsuario == null) return 0;
        
        return (int) repoPrestamo.obtenerTodos().stream()
            .filter(p -> p.getIdUsuario().equals(idUsuario))
            .filter(p -> p.getEstado() == EstadoTransaccion.ACTIVA)
            .filter(p -> p.getFechaDevolucionReal() == null)
            .count();
    }
    
    public boolean actualizarLimite(TipoUsuario tipoUsuario, int nuevoLimite) {
        if (tipoUsuario == null || nuevoLimite < 0) return false;
        limitesPorTipo.put(tipoUsuario, nuevoLimite);
        return true;
    }
    
    public int cupoRestante(String idUsuario) {
        Usuario usuario = repoUsuario.obtenerPorId(idUsuario);
        if (usuario == null) return 0;
        
        int limite = obtenerLimiteMaximo(usuario.getTipo());
        int actual = cantidadActualPrestada(idUsuario);
        return Math.max(0, limite - actual);
    }
}