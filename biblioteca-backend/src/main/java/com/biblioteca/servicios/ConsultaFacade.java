package com.biblioteca.servicios;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.biblioteca.dominio.entidades.Material;
import com.biblioteca.dominio.entidades.Multa;
import com.biblioteca.dominio.entidades.Prestamo;
import com.biblioteca.dominio.entidades.Reserva;
import com.biblioteca.dominio.entidades.Usuario;
import com.biblioteca.dominio.enumeraciones.EstadoMaterial;
import com.biblioteca.dominio.enumeraciones.EstadoMulta;
import com.biblioteca.dominio.enumeraciones.EstadoTransaccion;
import com.biblioteca.dominio.enumeraciones.EstadoUsuario;
import com.biblioteca.dominio.enumeraciones.TipoMaterial;
import com.biblioteca.dominio.objetosvalor.IdMaterial;
import com.biblioteca.dominio.objetosvalor.IdUsuario;
import com.biblioteca.repositorios.IRepositorio;
import com.biblioteca.servicios.interfaces.IConsultaFacade;
import com.biblioteca.servicios.interfaces.IDisponibilidadService;
import com.biblioteca.servicios.interfaces.IServicioReportes;

public class ConsultaFacade implements IConsultaFacade {

    private final IRepositorio<Material> repoMaterial;
    private final IRepositorio<Usuario> repoUsuario;
    private final IRepositorio<Prestamo> repoPrestamo;
    private final IRepositorio<Reserva> repoReserva;
    private final IRepositorio<Multa> repoMulta;
    private final IDisponibilidadService disponibilidadService;
    private final IServicioReportes servicioReportes;

    public ConsultaFacade(
            IRepositorio<Material> repoMaterial,
            IRepositorio<Usuario> repoUsuario,
            IRepositorio<Prestamo> repoPrestamo,
            IRepositorio<Reserva> repoReserva,
            IRepositorio<Multa> repoMulta,
            IDisponibilidadService disponibilidadService,
            IServicioReportes servicioReportes) {
        this.repoMaterial = repoMaterial;
        this.repoUsuario = repoUsuario;
        this.repoPrestamo = repoPrestamo;
        this.repoReserva = repoReserva;
        this.repoMulta = repoMulta;
        this.disponibilidadService = disponibilidadService;
        this.servicioReportes = servicioReportes;
    }

    @Override
    public List<Material> listarMateriales() {
        return repoMaterial.obtenerTodos();
    }

    @Override
    public List<Material> buscarMateriales(String criterio) {
        String busqueda = criterio.toLowerCase();
        return repoMaterial.obtenerTodos().stream()
                .filter(m ->
                        m.getId().getValor().toLowerCase().contains(busqueda) ||
                        m.getTitulo().toLowerCase().contains(busqueda) ||
                        m.getAutor().toLowerCase().contains(busqueda))
                .collect(Collectors.toList());
    }

    @Override
    public Material obtenerMaterialPorId(String id) {
        return repoMaterial.obtenerPorId(id);
    }

    @Override
    public List<Material> obtenerMaterialesDisponibles() {
        return repoMaterial.obtenerTodos().stream()
                .filter(m -> m.getEstado() == EstadoMaterial.DISPONIBLE)
                .collect(Collectors.toList());
    }

    @Override
    public List<Material> obtenerMaterialesPrestados() {
        return repoMaterial.obtenerTodos().stream()
                .filter(m -> m.getEstado() == EstadoMaterial.PRESTADO)
                .collect(Collectors.toList());
    }

    @Override
    public boolean verificarDisponibilidad(String idMaterial) {
        return disponibilidadService.verificarDisponibilidad(idMaterial);
    }

    @Override
    public EstadoMaterial obtenerEstadoActual(String idMaterial) {
        return disponibilidadService.obtenerEstadoActual(idMaterial);
    }

    @Override
    public boolean materialEsPrestable(String idMaterial, TipoMaterial tipo) {
        return disponibilidadService.materialEsPrestable(idMaterial, tipo);
    }

    @Override
    public List<Usuario> listarUsuarios() {
        return repoUsuario.obtenerTodos();
    }

    @Override
    public List<Usuario> buscarUsuarios(String criterio) {
        String busqueda = criterio.toLowerCase();
        return repoUsuario.obtenerTodos().stream()
                .filter(u ->
                        u.getId().getValor().toLowerCase().contains(busqueda) ||
                        u.getNombre().toLowerCase().contains(busqueda) ||
                        u.getEmail().toLowerCase().contains(busqueda))
                .collect(Collectors.toList());
    }

    @Override
    public Usuario obtenerUsuarioPorId(String id) {
        return repoUsuario.obtenerPorId(id);
    }

    @Override
    public List<Usuario> obtenerUsuariosActivos() {
        return repoUsuario.obtenerTodos().stream()
                .filter(u -> u.getEstado() == EstadoUsuario.ACTIVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<Usuario> obtenerUsuariosBloqueados() {
        return repoUsuario.obtenerTodos().stream()
                .filter(u -> u.getEstado() == EstadoUsuario.BLOQUEADO_MULTA ||
                             u.getEstado() == EstadoUsuario.BLOQUEADO_PERDIDA)
                .collect(Collectors.toList());
    }

    @Override
    public List<Prestamo> listarPrestamosActivos() {
        return repoPrestamo.obtenerTodos().stream()
                .filter(p -> p.getEstado() == EstadoTransaccion.ACTIVA)
                .collect(Collectors.toList());
    }

    @Override
    public List<Prestamo> obtenerPrestamosVencidos() {
        LocalDateTime ahora = LocalDateTime.now();
        return repoPrestamo.obtenerTodos().stream()
                .filter(p -> p.getEstado() == EstadoTransaccion.ACTIVA)
                .filter(p -> p.getFechaDevolucionReal() == null)
                .filter(p -> p.getFechaDevolucionEsperada().isBefore(ahora))
                .collect(Collectors.toList());
    }

    @Override
    public List<Prestamo> obtenerPrestamosPorUsuario(IdUsuario idUsuario) {
        return repoPrestamo.obtenerTodos().stream()
                .filter(p -> p.getIdUsuario().equals(idUsuario))
                .filter(p -> p.getEstado() == EstadoTransaccion.ACTIVA)
                .collect(Collectors.toList());
    }

    @Override
    public Prestamo obtenerPrestamoPorId(String id) {
        return repoPrestamo.obtenerPorId(id);
    }

    @Override
    public List<Reserva> listarReservasActivas() {
        LocalDateTime ahora = LocalDateTime.now();
        return repoReserva.obtenerTodos().stream()
                .filter(r -> r.getEstado() == EstadoTransaccion.ACTIVA)
                .filter(r -> r.getFechaExpiracion().isAfter(ahora))
                .collect(Collectors.toList());
    }

    @Override
    public List<Reserva> obtenerReservasPorUsuario(IdUsuario idUsuario) {
        return repoReserva.obtenerTodos().stream()
                .filter(r -> r.getEstado() == EstadoTransaccion.ACTIVA)
                .filter(r -> r.getIdUsuario().equals(idUsuario))
                .collect(Collectors.toList());
    }

    @Override
    public List<Reserva> obtenerReservasPorMaterial(IdMaterial idMaterial) {
        return repoReserva.obtenerTodos().stream()
                .filter(r -> r.getEstado() == EstadoTransaccion.ACTIVA)
                .filter(r -> r.getIdMaterial().equals(idMaterial))
                .collect(Collectors.toList());
    }

    @Override
    public List<Multa> listarMultasPendientes() {
        return repoMulta.obtenerTodos().stream()
                .filter(m -> m.getEstado() == EstadoMulta.PENDIENTE)
                .collect(Collectors.toList());
    }

    @Override
    public List<Multa> obtenerMultasPorUsuario(String idUsuario) {
        return repoMulta.obtenerTodos().stream()
                .filter(m -> m.getIdUsuario().equals(idUsuario))
                .collect(Collectors.toList());
    }

    @Override
    public List<Prestamo> verHistorialDevoluciones() {
        return repoPrestamo.obtenerTodos().stream()
                .filter(p -> p.getFechaDevolucionReal() != null)
                .collect(Collectors.toList());
    }

    @Override
    public String generarEstadisticasGenerales() {
        return servicioReportes.generarEstadisticasGenerales();
    }

    @Override
    public String generarEstadoUsuario(String idUsuario) {
        return servicioReportes.generarEstadoUsuario(idUsuario);
    }

    @Override
    public String generarLimitesUsuario() {
        return servicioReportes.generarLimitesUsuario();
    }

    @Override
    public String generarPoliticasTiempo() {
        return servicioReportes.generarPoliticasTiempo();
    }

    @Override
    public String generarReporteCompleto() {
        return servicioReportes.generarReporteCompleto();
    }
}

