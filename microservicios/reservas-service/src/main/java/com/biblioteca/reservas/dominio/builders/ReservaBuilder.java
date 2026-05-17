package com.biblioteca.reservas.dominio.builders;

import com.biblioteca.commons.objetosvalor.Resultado;
import com.biblioteca.commons.patrones.IBuilder;
import com.biblioteca.reservas.dominio.Reserva;

import java.time.LocalDateTime;

/**
 * Builder para Reserva que implementa validaciones de dominio.
 * Garantiza que una Reserva se cree en un estado consistente.
 * 
 * Principios SOLID aplicados:
 * - Single Responsibility: Solo responsable de validar y construir Reserva
 * - Open/Closed: Fácil extender con nuevas validaciones sin modificar
 */
public class ReservaBuilder implements IBuilder<Reserva> {
    private String id;
    private String idUsuario;
    private String idMaterial;
    private int posicionCola = 1;
    private String estadoReserva = "EN_ESPERA";
    private LocalDateTime fechaReserva;
    private LocalDateTime fechaNotificacion;
    private LocalDateTime fechaExpiracion;
    private String sede;

    public ReservaBuilder conId(String id) {
        this.id = id;
        return this;
    }

    public ReservaBuilder conIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
        return this;
    }

    public ReservaBuilder conIdMaterial(String idMaterial) {
        this.idMaterial = idMaterial;
        return this;
    }

    public ReservaBuilder conPosicionCola(int posicionCola) {
        this.posicionCola = posicionCola;
        return this;
    }

    public ReservaBuilder conEstadoReserva(String estadoReserva) {
        this.estadoReserva = estadoReserva != null ? estadoReserva : "EN_ESPERA";
        return this;
    }

    public ReservaBuilder conFechaReserva(LocalDateTime fechaReserva) {
        this.fechaReserva = fechaReserva;
        return this;
    }

    public ReservaBuilder conSede(String sede) {
        this.sede = sede;
        return this;
    }

    @Override
    public Resultado<Reserva> construir() {
        // Validaciones
        if (id == null || id.isBlank()) {
            return Resultado.fallo("El ID de la reserva es obligatorio");
        }

        if (idUsuario == null || idUsuario.isBlank()) {
            return Resultado.fallo("El ID del usuario es obligatorio");
        }

        if (idMaterial == null || idMaterial.isBlank()) {
            return Resultado.fallo("El ID del material es obligatorio");
        }

        if (posicionCola <= 0) {
            return Resultado.fallo("La posición en la cola debe ser mayor a 0");
        }

        if (posicionCola > 1000) {
            return Resultado.fallo("La posición en la cola no puede exceder 1000");
        }

        if (!estadoValido(estadoReserva)) {
            return Resultado.fallo("Estado de reserva inválido: " + estadoReserva);
        }

        if (fechaReserva == null) {
            return Resultado.fallo("La fecha de reserva es obligatoria");
        }

        if (sede == null || sede.isBlank()) {
            return Resultado.fallo("La sede es obligatoria");
        }

        // Si todas las validaciones pasan, construir la Reserva
        Reserva reserva = new Reserva(
                id,
                idUsuario,
                idMaterial,
                posicionCola,
                estadoReserva,
                fechaReserva,
                fechaNotificacion,
                fechaExpiracion,
                sede
        );
        return Resultado.exitoso(reserva);
    }

    private boolean estadoValido(String estado) {
        return estado.equals("EN_ESPERA") ||
               estado.equals("NOTIFICADA") ||
               estado.equals("COMPLETADA") ||
               estado.equals("CANCELADA");
    }
}
