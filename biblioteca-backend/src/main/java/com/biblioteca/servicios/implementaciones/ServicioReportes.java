package com.biblioteca.servicios.implementaciones;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
import com.biblioteca.dominio.enumeraciones.TipoUsuario;
import com.biblioteca.dominio.objetosvalor.IdUsuario;
import com.biblioteca.dominio.objetosvalor.ResultadoValidacion;
import com.biblioteca.repositorios.IRepositorio;
import com.biblioteca.servicios.interfaces.IGestorBloqueoService;
import com.biblioteca.servicios.interfaces.ILimitePrestamoService;
import com.biblioteca.servicios.interfaces.IPoliticaTiempoService;
import com.biblioteca.servicios.interfaces.IPrestamoService;
import com.biblioteca.servicios.interfaces.IServicioReportes;

public class ServicioReportes implements IServicioReportes {

    private final IRepositorio<Material> repoMaterial;
    private final IRepositorio<Usuario> repoUsuario;
    private final IRepositorio<Prestamo> repoPrestamo;
    private final IRepositorio<Reserva> repoReserva;
    private final IRepositorio<Multa> repoMulta;

    private final IGestorBloqueoService gestorBloqueo;
    private final IPrestamoService prestamoService;
    private final ILimitePrestamoService limiteService;
    private final IPoliticaTiempoService politicaTiempoService;

    public ServicioReportes(
            IRepositorio<Material> repoMaterial,
            IRepositorio<Usuario> repoUsuario,
            IRepositorio<Prestamo> repoPrestamo,
            IRepositorio<Reserva> repoReserva,
            IRepositorio<Multa> repoMulta,
            IGestorBloqueoService gestorBloqueo,
            IPrestamoService prestamoService,
            ILimitePrestamoService limiteService,
            IPoliticaTiempoService politicaTiempoService) {
        
        this.repoMaterial = repoMaterial;
        this.repoUsuario = repoUsuario;
        this.repoPrestamo = repoPrestamo;
        this.repoReserva = repoReserva;
        this.repoMulta = repoMulta;
        this.gestorBloqueo = gestorBloqueo;
        this.prestamoService = prestamoService;
        this.limiteService = limiteService;
        this.politicaTiempoService = politicaTiempoService;
    }

    @Override
    public String generarEstadoUsuario(String idUsuario) {
        Usuario u = repoUsuario.obtenerPorId(idUsuario);
        if (u == null) {
            return "❌ Usuario no encontrado";
        }

        ResultadoValidacion validacion = gestorBloqueo.verificarSiDebeBloquear(idUsuario);
        int prestamosActivos = prestamoService.obtenerPrestamosActivos(new IdUsuario(idUsuario)).size();
        int limite = limiteService.obtenerLimiteMaximo(u.getTipo());

        BigDecimal multasPendientes = BigDecimal.ZERO;
        if (gestorBloqueo instanceof GestorBloqueoService) {
            multasPendientes = ((GestorBloqueoService) gestorBloqueo).obtenerTotalMultasPendientes(idUsuario);
        }

        StringBuilder sb = new StringBuilder();
        sb.append("\n👤 ESTADO DEL USUARIO:\n");
        sb.append("ID: ").append(u.getId()).append("\n");
        sb.append("Nombre: ").append(u.getNombre()).append("\n");
        sb.append("Tipo: ").append(u.getTipo()).append("\n");
        sb.append("Estado: ").append(u.getEstado()).append("\n");
        sb.append("Préstamos activos: ").append(prestamosActivos).append("/").append(limite).append("\n");
        sb.append(String.format("Multas pendientes: $%.2f%n", multasPendientes));
        sb.append("Puede realizar préstamos: ").append(validacion.esValido() ? "✅ SÍ" : "❌ NO").append("\n");

        if (!validacion.esValido() && !validacion.getErrores().isEmpty()) {
            sb.append("Motivo: ").append(validacion.getErrores().get(0)).append("\n");
        }

        return sb.toString();
    }

    @Override
    public String generarEstadisticasGenerales() {
        long totalMateriales = repoMaterial.contar();
        long totalUsuarios = repoUsuario.contar();
        long prestamosActivos = repoPrestamo.obtenerTodos().stream()
                .filter(p -> p.getEstado() == EstadoTransaccion.ACTIVA)
                .filter(p -> p.getFechaDevolucionReal() == null)
                .count();
        long reservasActivas = repoReserva.obtenerTodos().stream()
                .filter(r -> r.getEstado() == EstadoTransaccion.ACTIVA)
                .filter(r -> r.getFechaExpiracion().isAfter(LocalDateTime.now()))
                .count();
        long multasPendientes = repoMulta.obtenerTodos().stream()
                .filter(m -> m.getEstado() == EstadoMulta.PENDIENTE)
                .count();

        StringBuilder sb = new StringBuilder();
        sb.append("\n📊 ESTADÍSTICAS GENERALES:\n");
        sb.append("Total materiales: ").append(totalMateriales).append("\n");
        sb.append("Total usuarios: ").append(totalUsuarios).append("\n");
        sb.append("Préstamos activos: ").append(prestamosActivos).append("\n");
        sb.append("Reservas activas: ").append(reservasActivas).append("\n");
        sb.append("Multas pendientes: ").append(multasPendientes).append("\n");

        return sb.toString();
    }

    @Override
    public String generarLimitesUsuario() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n📋 LÍMITES DE PRÉSTAMO POR TIPO DE USUARIO:\n");
        sb.append("ESTUDIANTE: ").append(limiteService.obtenerLimiteMaximo(TipoUsuario.ESTUDIANTE)).append("\n");
        sb.append("PROFESOR: ").append(limiteService.obtenerLimiteMaximo(TipoUsuario.PROFESOR)).append("\n");
        sb.append("INVESTIGADOR: ").append(limiteService.obtenerLimiteMaximo(TipoUsuario.INVESTIGADOR)).append("\n");
        sb.append("PÚBLICO GENERAL: ").append(limiteService.obtenerLimiteMaximo(TipoUsuario.PUBLICO_GENERAL)).append("\n");
        return sb.toString();
    }

    @Override
    public String generarPoliticasTiempo() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n⏱️  DÍAS DE PRÉSTAMO POR TIPO:\n");
        sb.append("-".repeat(50)).append("\n");
        sb.append(String.format("%-15s %-12s %-12s %-12s%n", "MATERIAL", "ESTUDIANTE", "PROFESOR", "INVESTIGADOR"));
        sb.append("-".repeat(50)).append("\n");

        if (politicaTiempoService instanceof PoliticaTiempoPorTipoService) {
            PoliticaTiempoPorTipoService politica = (PoliticaTiempoPorTipoService) politicaTiempoService;
            for (TipoMaterial tm : TipoMaterial.values()) {
                sb.append(String.format("%-15s %-12d %-12d %-12d%n",
                        tm,
                        politica.calcularDiasPrestamo(tm, TipoUsuario.ESTUDIANTE),
                        politica.calcularDiasPrestamo(tm, TipoUsuario.PROFESOR),
                        politica.calcularDiasPrestamo(tm, TipoUsuario.INVESTIGADOR)));
            }
        } else {
            sb.append("Las políticas de tiempo no están gestionadas por PoliticaTiempoPorTipoService.\n");
        }

        return sb.toString();
    }

    @Override
    public String generarReporteCompleto() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n📑 REPORTE COMPLETO DEL SISTEMA\n");
        sb.append("=".repeat(60)).append("\n");

        sb.append(generarEstadisticasGenerales());

        sb.append("\n📚 MATERIALES POR ESTADO:\n");
        for (EstadoMaterial em : EstadoMaterial.values()) {
            long count = repoMaterial.obtenerTodos().stream()
                    .filter(m -> m.getEstado() == em)
                    .count();
            if (count > 0) {
                sb.append(String.format("  %s: %d%n", em, count));
            }
        }

        sb.append("\n👥 USUARIOS POR ESTADO:\n");
        for (EstadoUsuario eu : EstadoUsuario.values()) {
            long count = repoUsuario.obtenerTodos().stream()
                    .filter(u -> u.getEstado() == eu)
                    .count();
            if (count > 0) {
                sb.append(String.format("  %s: %d%n", eu, count));
            }
        }

        sb.append("\n💰 MULTAS POR ESTADO:\n");
        double totalMultas = 0;
        for (EstadoMulta em : EstadoMulta.values()) {
            double suma = repoMulta.obtenerTodos().stream()
                    .filter(m -> m.getEstado() == em)
                    .mapToDouble(Multa::calcularMontoTotal)
                    .sum();
            if (suma > 0) {
                sb.append(String.format("  %s: $%.2f%n", em, suma));
                if (em == EstadoMulta.PENDIENTE) {
                    totalMultas += suma;
                }
            }
        }
        sb.append(String.format("  TOTAL PENDIENTE: $%.2f%n", totalMultas));

        return sb.toString();
    }

}
