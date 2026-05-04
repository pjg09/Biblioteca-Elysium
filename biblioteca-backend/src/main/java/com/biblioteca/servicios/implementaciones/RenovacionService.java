package com.biblioteca.servicios.implementaciones;

import com.biblioteca.dominio.entidades.Material;
import com.biblioteca.dominio.entidades.Prestamo;
import com.biblioteca.dominio.entidades.Reserva;
import com.biblioteca.dominio.entidades.Usuario;
import com.biblioteca.dominio.enumeraciones.EstadoTransaccion;
import com.biblioteca.dominio.enumeraciones.TipoUsuario;
import com.biblioteca.dominio.eventos.IDomainEvent;
import com.biblioteca.dominio.eventos.RenovacionRechazada;
import com.biblioteca.dominio.excepciones.OperacionNoPermitidaException;
import com.biblioteca.dominio.objetosvalor.Resultado;
import com.biblioteca.dominio.objetosvalor.ResultadoValidacion;
import com.biblioteca.repositorios.IRepositorio;
import com.biblioteca.servicios.interfaces.IPoliticaTiempoService;
import com.biblioteca.servicios.interfaces.IRenovacionService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RenovacionService implements IRenovacionService {

    private final IRepositorio<Prestamo> repositorioPrestamo;
    private final IRepositorio<Reserva> repositorioReserva;
    private final IRepositorio<Material> repositorioMaterial;
    private final IRepositorio<Usuario> repositorioUsuario;
    private final IPoliticaTiempoService politicaTiempoService;
    private final Map<TipoUsuario, Integer> maximoRenovaciones;

    public RenovacionService(
            IRepositorio<Prestamo> repositorioPrestamo,
            IRepositorio<Reserva> repositorioReserva,
            IRepositorio<Material> repositorioMaterial,
            IRepositorio<Usuario> repositorioUsuario,
            IPoliticaTiempoService politicaTiempoService) {
        if (repositorioPrestamo == null || repositorioReserva == null
                || repositorioMaterial == null || repositorioUsuario == null
                || politicaTiempoService == null)
            throw new IllegalArgumentException("Ningún repositorio o servicio puede ser nulo");

        this.repositorioPrestamo  = repositorioPrestamo;
        this.repositorioReserva   = repositorioReserva;
        this.repositorioMaterial  = repositorioMaterial;
        this.repositorioUsuario   = repositorioUsuario;
        this.politicaTiempoService = politicaTiempoService;
        this.maximoRenovaciones   = inicializarMaximoRenovaciones();
    }

    private Map<TipoUsuario, Integer> inicializarMaximoRenovaciones() {
        Map<TipoUsuario, Integer> maximos = new HashMap<>();
        maximos.put(TipoUsuario.ESTUDIANTE,      2);
        maximos.put(TipoUsuario.PROFESOR,        3);
        maximos.put(TipoUsuario.INVESTIGADOR,    4);
        maximos.put(TipoUsuario.PUBLICO_GENERAL, 1);
        return maximos;
    }

    @Override
    public ResultadoValidacion validarRenovacion(String idPrestamo) {
        List<String> errores = new ArrayList<>();

        Prestamo prestamo = repositorioPrestamo.obtenerPorId(idPrestamo);
        if (prestamo == null) {
            errores.add("El préstamo con ID '" + idPrestamo + "' no existe");
            return ResultadoValidacion.Invalido(errores);
        }
        if (prestamo.getEstado() != EstadoTransaccion.ACTIVA) {
            errores.add("El préstamo no está activo. Estado actual: " + prestamo.getEstado());
            return ResultadoValidacion.Invalido(errores);
        }

        Usuario usuario = repositorioUsuario.obtenerPorId(prestamo.getIdUsuario());
        if (usuario == null) {
            errores.add("Usuario asociado al préstamo no encontrado");
            return ResultadoValidacion.Invalido(errores);
        }

        int maximoPermitido = maximoRenovaciones.getOrDefault(usuario.getTipo(), 2);
        if (prestamo.getRenovacionesUsadas() >= maximoPermitido) {
            errores.add("Ha excedido el número máximo de renovaciones permitidas");
            errores.add("Tipo de usuario: " + usuario.getTipo());
            errores.add("Máximo permitido: " + maximoPermitido);
            errores.add("Renovaciones ya usadas: " + prestamo.getRenovacionesUsadas());
            return ResultadoValidacion.Invalido(errores);
        }

        boolean hayReservas = repositorioReserva.obtenerTodos().stream()
            .anyMatch(r -> r.getIdMaterial().equals(prestamo.getIdMaterial())
                       && r.getEstado() == EstadoTransaccion.ACTIVA);
        if (hayReservas) {
            errores.add("No se puede renovar: hay reservas pendientes sobre este material");
            return ResultadoValidacion.Invalido(errores);
        }

        LocalDateTime ahora = LocalDateTime.now();
        if (prestamo.getFechaDevolucionEsperada() != null
                && ahora.isAfter(prestamo.getFechaDevolucionEsperada().plusDays(7))) {
            errores.add("El préstamo está vencido hace más de 7 días. Debe devolver el material primero");
            return ResultadoValidacion.Invalido(errores);
        }

        return ResultadoValidacion.Valido();
    }

    @Override
    public Resultado renovarPrestamo(String idPrestamo) {
        try {
            ResultadoValidacion validacion = validarRenovacion(idPrestamo);
            if (!validacion.esValido())
                return Resultado.Fallido("No se puede renovar el préstamo: "
                    + String.join(", ", validacion.getErrores()));

            Prestamo prestamo = repositorioPrestamo.obtenerPorId(idPrestamo);
            if (prestamo == null)
                return Resultado.Fallido("Préstamo no encontrado");

            Material material = repositorioMaterial.obtenerPorId(prestamo.getIdMaterial());
            Usuario usuario   = repositorioUsuario.obtenerPorId(prestamo.getIdUsuario());
            if (material == null || usuario == null)
                return Resultado.Fallido("No se encontraron los datos del material o usuario");

            LocalDateTime nuevaFecha = politicaTiempoService.obtenerFechaDevolucion(
                LocalDateTime.now(), material.getTipo(), usuario.getTipo());

            int maxRenovaciones = maximoRenovaciones.getOrDefault(usuario.getTipo(), 2);

            // ── El agregado encapsula la invariante y emite el evento ──
            try {
                prestamo.renovar(nuevaFecha, maxRenovaciones);
            } catch (OperacionNoPermitidaException e) {
                // Registrar como evento de dominio y retornar fallo
                RenovacionRechazada rechazo = new RenovacionRechazada(
                    idPrestamo, prestamo.getIdUsuario(), e.getMessage());
                publicarEventos(List.of(rechazo));
                return Resultado.Fallido("Renovación rechazada: " + e.getMessage());
            }

            Resultado resultadoActualizacion = repositorioPrestamo.actualizar(prestamo);
            if (!resultadoActualizacion.getExito())
                return Resultado.Fallido("Error al actualizar el préstamo: "
                    + resultadoActualizacion.getMensaje());

            publicarEventos(prestamo.pullEvents());

            int restantes = maxRenovaciones - prestamo.getRenovacionesUsadas();
            String mensaje = String.format(
                "Préstamo renovado exitosamente. Nueva fecha de devolución: %s. Renovaciones restantes: %d",
                nuevaFecha.toLocalDate(), restantes);
            return Resultado.Exitoso(mensaje, prestamo);

        } catch (Exception e) {
            return Resultado.Fallido("Error inesperado al renovar préstamo: " + e.getMessage());
        }
    }

    @Override
    public int obtenerRenovacionesDisponibles(String idPrestamo, TipoUsuario tipoUsuario) {
        Prestamo prestamo = repositorioPrestamo.obtenerPorId(idPrestamo);
        if (prestamo == null) return 0;
        int max = maximoRenovaciones.getOrDefault(tipoUsuario, 2);
        return Math.max(0, max - prestamo.getRenovacionesUsadas());
    }

    public void configurarLimiteRenovaciones(TipoUsuario tipoUsuario, int limite) {
        if (limite < 0)
            throw new IllegalArgumentException("El límite de renovaciones no puede ser negativo");
        maximoRenovaciones.put(tipoUsuario, limite);
    }

    public int obtenerMaximoRenovaciones(TipoUsuario tipoUsuario) {
        return maximoRenovaciones.getOrDefault(tipoUsuario, 2);
    }

    private void publicarEventos(List<IDomainEvent> eventos) {
        eventos.forEach(e ->
            System.out.println("[DOMAIN EVENT] " + e.eventType()
                + " | aggregate=" + e.aggregateId()
                + " | id=" + e.eventId()));
    }
}
