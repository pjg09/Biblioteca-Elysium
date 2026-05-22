package com.biblioteca.reservas.dominio.agregados;

import com.biblioteca.commons.excepciones.OperacionNoPermitidaEnEstadoReservaException;
import com.biblioteca.reservas.dominio.estados.*;
import java.time.LocalDateTime;

public class Reserva {

    private String id;
    private String idUsuario;
    private String idMaterial;
    private int posicionCola;
    private IEstadoReserva estado;
    private LocalDateTime fechaReserva;
    private LocalDateTime fechaNotificacion;
    private LocalDateTime fechaExpiracion;
    private String sede;

    public Reserva() {}

    public Reserva(String id, String idUsuario, String idMaterial, int posicionCola,
                   String estadoString, LocalDateTime fechaReserva,
                   LocalDateTime fechaNotificacion, LocalDateTime fechaExpiracion,
                   String sede) {
        this.id = id;
        this.idUsuario = idUsuario;
        this.idMaterial = idMaterial;
        this.posicionCola = posicionCola;
        this.estado = reconstruirEstado(estadoString);
        this.fechaReserva = fechaReserva;
        this.fechaNotificacion = fechaNotificacion;
        this.fechaExpiracion = fechaExpiracion;
        this.sede = sede;
    }

    public void notificarDisponibilidad(LocalDateTime ahora)
            throws OperacionNoPermitidaEnEstadoReservaException {
        ReservaContexto contexto = new ReservaContexto(this.posicionCola, this.estado);
        this.estado.notificar(ahora, contexto);
        this.posicionCola = contexto.getPosicionCola();
        this.fechaNotificacion = contexto.getFechaNotificacion();
        this.fechaExpiracion = contexto.getFechaExpiracion();
        this.estado = contexto.getEstadoActual();
    }

    public void expirar()
            throws OperacionNoPermitidaEnEstadoReservaException {
        ReservaContexto contexto = new ReservaContexto(this.posicionCola, this.estado);
        this.estado.expirar(contexto);
        this.estado = contexto.getEstadoActual();
    }

    public void cancelar()
            throws OperacionNoPermitidaEnEstadoReservaException {
        ReservaContexto contexto = new ReservaContexto(this.posicionCola, this.estado);
        this.estado.cancelar(contexto);
        this.estado = contexto.getEstadoActual();
    }

    public void completar()
            throws OperacionNoPermitidaEnEstadoReservaException {
        ReservaContexto contexto = new ReservaContexto(this.posicionCola, this.estado);
        this.estado.completar(contexto);
        this.estado = contexto.getEstadoActual();
    }

    public boolean isActiva() {
        return this.estado.esActiva();
    }

    public String getId() {
        return id;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public String getIdMaterial() {
        return idMaterial;
    }

    public int getPosicionCola() {
        return posicionCola;
    }

    public String getEstadoReserva() {
        return estado.nombreEstado();
    }

    public IEstadoReserva getEstadoObjeto() {
        return estado;
    }

    public LocalDateTime getFechaReserva() {
        return fechaReserva;
    }

    public LocalDateTime getFechaNotificacion() {
        return fechaNotificacion;
    }

    public LocalDateTime getFechaExpiracion() {
        return fechaExpiracion;
    }

    public String getSede() {
        return sede;
    }

    public void setPosicionCola(int posicionCola) {
        this.posicionCola = posicionCola;
    }

    private static IEstadoReserva reconstruirEstado(String estadoString) {
        switch (estadoString) {
            case "EN_ESPERA":
                return new ReservaEnEsperaState();
            case "NOTIFICADA":
                return new ReservaNotificadaState();
            case "COMPLETADA":
                return new ReservaCompletadaState();
            case "CANCELADA":
                return new ReservaCanceladaState();
            case "EXPIRADA":
                return new ReservaExpiradaState();
            default:
                throw new IllegalArgumentException("Estado de reserva desconocido: " + estadoString);
        }
    }
}
