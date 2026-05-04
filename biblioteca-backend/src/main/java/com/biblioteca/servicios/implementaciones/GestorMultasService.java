package com.biblioteca.servicios.implementaciones;

import com.biblioteca.dominio.entidades.Multa;
import com.biblioteca.dominio.entidades.Usuario;
import com.biblioteca.dominio.enumeraciones.EstadoMulta;
import com.biblioteca.dominio.enumeraciones.EstadoUsuario;
import com.biblioteca.dominio.eventos.IDomainEvent;
import com.biblioteca.dominio.objetosvalor.ContextoMulta;
import com.biblioteca.dominio.objetosvalor.Resultado;
import com.biblioteca.repositorios.IRepositorio;
import com.biblioteca.servicios.interfaces.ICalculadorMulta;
import com.biblioteca.servicios.interfaces.IGestorBloqueoService;
import com.biblioteca.servicios.interfaces.IGestorMultasService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class GestorMultasService implements IGestorMultasService {
    private final List<ICalculadorMulta> calculadores;
    private final IRepositorio<Multa> repoMulta;
    private final IRepositorio<Usuario> repoUsuario;
    private final IGestorBloqueoService gestorBloqueo;

    public GestorMultasService(
            IRepositorio<Multa> repoMulta,
            IRepositorio<Usuario> repoUsuario,
            IGestorBloqueoService gestorBloqueo) {
        this.calculadores  = new ArrayList<>();
        this.repoMulta     = repoMulta;
        this.repoUsuario   = repoUsuario;
        this.gestorBloqueo = gestorBloqueo;
    }

    @Override
    public void registrarCalculador(ICalculadorMulta calculador) {
        calculadores.add(calculador);
    }

    @Override
    public Multa calcularMulta(ContextoMulta contexto) {
        for (ICalculadorMulta calculador : calculadores) {
            if (calculador.puedeCalcular(contexto))
                return calculador.calcular(contexto);
        }
        throw new IllegalArgumentException(
            "No hay calculador disponible para el tipo de multa: " + contexto.getTipoMulta());
    }

    @Override
    public Resultado pagarMulta(String idMulta) {
        Multa multa = repoMulta.obtenerPorId(idMulta);
        if (multa == null)
            return Resultado.Fallido("Multa no encontrada");

        // ── El agregado encapsula la invariante y emite el evento ──
        try {
            multa.pagar(LocalDateTime.now());
        } catch (com.biblioteca.dominio.excepciones.OperacionNoPermitidaException e) {
            return Resultado.Fallido(e.getMessage());
        }
        repoMulta.actualizar(multa);

        List<IDomainEvent> eventos = multa.pullEvents();
        publicarEventos(eventos);

        // Evaluar desbloqueo si el usuario no tiene más deuda
        Usuario usuario = repoUsuario.obtenerPorId(multa.getIdUsuario());
        if (usuario != null && usuario.getEstado() == EstadoUsuario.BLOQUEADO_MULTA) {
            double totalPendiente = repoMulta.obtenerTodos().stream()
                .filter(m -> m.getIdUsuario().equals(usuario.getId()))
                .filter(m -> m.getEstado() == EstadoMulta.PENDIENTE)
                .mapToDouble(Multa::calcularMontoTotal)
                .sum();

            if (totalPendiente == 0) {
                gestorBloqueo.desbloquearUsuario(usuario.getId());
                return Resultado.Exitoso("Pago registrado. Usuario desbloqueado automáticamente", multa);
            }
        }

        return Resultado.Exitoso("Pago registrado exitosamente", multa);
    }

    private void publicarEventos(List<IDomainEvent> eventos) {
        eventos.forEach(e ->
            System.out.println("[DOMAIN EVENT] " + e.eventType()
                + " | aggregate=" + e.aggregateId()
                + " | id=" + e.eventId()));
    }
}
