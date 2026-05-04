package com.biblioteca.servicios.implementaciones;

import com.biblioteca.dominio.entidades.Material;
import com.biblioteca.dominio.entidades.Multa;
import com.biblioteca.dominio.entidades.Prestamo;
import com.biblioteca.dominio.entidades.Usuario;
import com.biblioteca.dominio.enumeraciones.EstadoMulta;
import com.biblioteca.dominio.enumeraciones.TipoMulta;
import com.biblioteca.dominio.eventos.IDomainEvent;
import com.biblioteca.dominio.eventos.MaterialDevuelto;
import com.biblioteca.dominio.excepciones.OperacionNoPermitidaException;
import com.biblioteca.dominio.objetosvalor.ContextoMulta;
import com.biblioteca.dominio.objetosvalor.Evaluacion;
import com.biblioteca.dominio.objetosvalor.Resultado;
import com.biblioteca.repositorios.IRepositorio;
import com.biblioteca.servicios.interfaces.IDevolucionService;
import com.biblioteca.servicios.interfaces.IGestorBloqueoService;
import com.biblioteca.servicios.interfaces.IGestorMultasService;
import com.biblioteca.servicios.interfaces.INotificacionService;
import com.biblioteca.servicios.interfaces.IReservaService;

import java.time.LocalDateTime;
import java.util.List;

public class DevolucionService implements IDevolucionService {

    private final IGestorMultasService gestorMultas;
    private final IRepositorio<Prestamo> repoPrestamo;
    private final IRepositorio<Material> repoMaterial;
    private final IRepositorio<Usuario> repoUsuario;
    private final IRepositorio<Multa> repoMulta;
    private final IReservaService reservas;
    private final INotificacionService notificador;
    private final IGestorBloqueoService gestorBloqueo;

    public DevolucionService(
            IGestorMultasService gestorMultas,
            IRepositorio<Prestamo> repoPrestamo,
            IRepositorio<Material> repoMaterial,
            IRepositorio<Usuario> repoUsuario,
            IRepositorio<Multa> repoMulta,
            IReservaService reservas,
            INotificacionService notificador,
            IGestorBloqueoService gestorBloqueo) {
        this.gestorMultas  = gestorMultas;
        this.repoPrestamo  = repoPrestamo;
        this.repoMaterial  = repoMaterial;
        this.repoUsuario   = repoUsuario;
        this.repoMulta     = repoMulta;
        this.reservas      = reservas;
        this.notificador   = notificador;
        this.gestorBloqueo = gestorBloqueo;
    }

    @Override
    public Resultado registrarDevolucion(String idPrestamo, Evaluacion evaluacion) {
        try {
            Prestamo prestamo = repoPrestamo.obtenerPorId(idPrestamo);
            if (prestamo == null)
                return Resultado.Fallido("Préstamo no encontrado");
            if (prestamo.getFechaDevolucionReal() != null)
                return Resultado.Fallido("Este préstamo ya fue devuelto");

            Material material = repoMaterial.obtenerPorId(prestamo.getIdMaterial());
            Usuario usuario   = repoUsuario.obtenerPorId(prestamo.getIdUsuario());
            if (material == null)
                return Resultado.Fallido("Material no encontrado");
            if (usuario == null)
                return Resultado.Fallido("Usuario no encontrado");

            LocalDateTime fechaDevolucion = LocalDateTime.now();

            // ── El agregado encapsula el cambio de estado y emite el evento ──
            try {
                prestamo.devolver(fechaDevolucion, evaluacion);
            } catch (OperacionNoPermitidaException e) {
                return Resultado.Fallido("No se puede registrar la devolución: " + e.getMessage());
            }
            repoPrestamo.actualizar(prestamo);

            // ── Extraer eventos y publicarlos (en monolito: log; en microservicios: RabbitMQ) ──
            List<IDomainEvent> eventos = prestamo.pullEvents();
            publicarEventos(eventos);

            // Obtener datos del evento para construir contextos de multa
            MaterialDevuelto eventoDevolucion = eventos.stream()
                .filter(e -> e instanceof MaterialDevuelto)
                .map(e -> (MaterialDevuelto) e)
                .findFirst().orElse(null);

            int diasRetraso = eventoDevolucion != null ? eventoDevolucion.diasRetraso : 0;

            // ── Procesar multas usando datos ya resueltos (sin acceso a repos desde calculadores) ──
            StringBuilder mensajeMultas = new StringBuilder();
            double totalMultas = 0;

            if (diasRetraso > 0) {
                ContextoMulta contextoRetraso = new ContextoMulta.Builder()
                    .conPrestamo(idPrestamo)
                    .conUsuario(usuario.getId())
                    .conMaterial(material.getId())
                    .conFechaActual(fechaDevolucion)
                    .deTipo(TipoMulta.POR_RETRASO)
                    .conDiasRetraso(diasRetraso)
                    .conTipoUsuario(usuario.getTipo())
                    .conValorMaterial(material.getPrecio())
                    .build();

                Multa multaRetraso = gestorMultas.calcularMulta(contextoRetraso);
                if (multaRetraso != null) {
                    repoMulta.agregar(multaRetraso);
                    totalMultas += multaRetraso.calcularMontoTotal();
                    mensajeMultas.append("• Multa por retraso: $").append(multaRetraso.calcularMontoTotal()).append("\n");
                    publicarEventos(multaRetraso.pullEvents());
                }
            }

            if (evaluacion != null && !evaluacion.esUsable() && evaluacion.tieneDanos()) {
                ContextoMulta contextoDano = new ContextoMulta.Builder()
                    .conPrestamo(idPrestamo)
                    .conUsuario(usuario.getId())
                    .conMaterial(material.getId())
                    .conEvaluacion(evaluacion)
                    .conFechaActual(fechaDevolucion)
                    .deTipo(TipoMulta.POR_DANO)
                    .conTipoUsuario(usuario.getTipo())
                    .conValorMaterial(material.getPrecio())
                    .build();

                Multa multaDano = gestorMultas.calcularMulta(contextoDano);
                if (multaDano != null) {
                    repoMulta.agregar(multaDano);
                    totalMultas += multaDano.calcularMontoTotal();
                    mensajeMultas.append("• Multa por daños: $").append(multaDano.calcularMontoTotal()).append("\n");
                    publicarEventos(multaDano.pullEvents());
                }
            }

            // ── Actualizar estado del material ──
            if (evaluacion == null || evaluacion.esUsable()) {
                material.marcarComoDisponible();
            } else {
                material.marcarComoEnReparacion("Daños detectados en inspección");
            }
            repoMaterial.actualizar(material);

            // ── Evaluar bloqueo por deuda acumulada ──
            if (totalMultas > 0) {
                double totalPendiente = repoMulta.obtenerTodos().stream()
                    .filter(m -> m.getIdUsuario().equals(usuario.getId()))
                    .filter(m -> m.getEstado() == EstadoMulta.PENDIENTE)
                    .mapToDouble(Multa::calcularMontoTotal)
                    .sum();
                if (totalPendiente >= 50000) {
                    gestorBloqueo.bloquearUsuario(usuario.getId(),
                        "Bloqueado por multas pendientes: $" + totalPendiente);
                }
            }

            // ── Notificar ──
            String mensajeUsuario = construirMensaje(material.getTitulo(), fechaDevolucion,
                mensajeMultas.toString(), totalMultas);
            notificador.enviarNotificacion(usuario.getId(), mensajeUsuario);

            final double totalFinal = totalMultas;
            final String detalleFinal = mensajeMultas.toString();
            final boolean usableFinal = evaluacion == null || evaluacion.esUsable();
            Object data = new Object() {
                public final Prestamo prestamoDevuelto = prestamo;
                public final double totalMultas_ = totalFinal;
                public final String detalleMultas = detalleFinal;
                public final boolean materialUsable = usableFinal;
            };

            String msg = totalMultas > 0
                ? "Devolución registrada con multas. Total: $" + totalMultas
                : "Devolución registrada exitosamente. Material en buen estado.";
            return Resultado.Exitoso(msg, data);

        } catch (Exception e) {
            return Resultado.Fallido("Error al registrar devolución: " + e.getMessage());
        }
    }

    private void publicarEventos(List<IDomainEvent> eventos) {
        // En el monolito: log. En microservicios: publicar a RabbitMQ vía IPublicadorEventos.
        eventos.forEach(e ->
            System.out.println("[DOMAIN EVENT] " + e.eventType()
                + " | aggregate=" + e.aggregateId()
                + " | id=" + e.eventId()));
    }

    private String construirMensaje(String titulo, LocalDateTime fecha, String detalle, double total) {
        StringBuilder sb = new StringBuilder();
        sb.append("Devolución registrada\nMaterial: ").append(titulo)
          .append("\nFecha: ").append(fecha.toLocalDate()).append("\n");
        if (total > 0) {
            sb.append("\nMULTAS GENERADAS:\n").append(detalle)
              .append("TOTAL: $").append(total).append("\n")
              .append("Debe pagar las multas para evitar bloqueos.");
        } else {
            sb.append("\nMaterial devuelto en buen estado. ¡Gracias!");
        }
        return sb.toString();
    }

    public long contarDevolucionesPorUsuario(String idUsuario) {
        return repoPrestamo.obtenerTodos().stream()
            .filter(p -> p.getIdUsuario().equals(idUsuario))
            .filter(p -> p.getFechaDevolucionReal() != null)
            .count();
    }
}
