package com.biblioteca.servicios.interfaces;

import java.util.List;

import com.biblioteca.dominio.entidades.Material;
import com.biblioteca.dominio.entidades.Multa;
import com.biblioteca.dominio.entidades.Prestamo;
import com.biblioteca.dominio.entidades.Reserva;
import com.biblioteca.dominio.entidades.Usuario;
import com.biblioteca.dominio.enumeraciones.EstadoMaterial;
import com.biblioteca.dominio.objetosvalor.IdMaterial;
import com.biblioteca.dominio.objetosvalor.IdUsuario;

public interface IConsultaFacade {

    // === Materiales ===
    List<Material> listarMateriales();
    List<Material> buscarMateriales(String criterio);
    Material obtenerMaterialPorId(String id);
    List<Material> obtenerMaterialesDisponibles();
    List<Material> obtenerMaterialesPrestados();
    boolean verificarDisponibilidad(String idMaterial);
    EstadoMaterial obtenerEstadoActual(String idMaterial);
    boolean materialEsPrestable(String idMaterial, com.biblioteca.dominio.enumeraciones.TipoMaterial tipo);

    // === Usuarios ===
    List<Usuario> listarUsuarios();
    List<Usuario> buscarUsuarios(String criterio);
    Usuario obtenerUsuarioPorId(String id);
    List<Usuario> obtenerUsuariosActivos();
    List<Usuario> obtenerUsuariosBloqueados();

    // === Préstamos ===
    List<Prestamo> listarPrestamosActivos();
    List<Prestamo> obtenerPrestamosVencidos();
    List<Prestamo> obtenerPrestamosPorUsuario(IdUsuario idUsuario);
    Prestamo obtenerPrestamoPorId(String id);

    // === Reservas ===
    List<Reserva> listarReservasActivas();
    List<Reserva> obtenerReservasPorUsuario(IdUsuario idUsuario);
    List<Reserva> obtenerReservasPorMaterial(IdMaterial idMaterial);

    // === Multas ===
    List<Multa> listarMultasPendientes();
    List<Multa> obtenerMultasPorUsuario(String idUsuario);

    // === Historial ===
    List<Prestamo> verHistorialDevoluciones();

    // === Reportes ===
    String generarEstadisticasGenerales();
    String generarEstadoUsuario(String idUsuario);
    String generarLimitesUsuario();
    String generarPoliticasTiempo();
    String generarReporteCompleto();
}
