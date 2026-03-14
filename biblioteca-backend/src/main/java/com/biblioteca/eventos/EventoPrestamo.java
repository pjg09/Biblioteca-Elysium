package com.biblioteca.eventos;

import java.time.LocalDateTime;

public class EventoPrestamo {
    private final String idPrestamo;
    private final String idUsuario;
    private final String idMaterial;
    private final LocalDateTime fecha;
    private final TipoEvento tipo;
    
    public enum TipoEvento {
        PRESTAMO_CREADO,
        PRESTAMO_VENCIDO,
        PRESTAMO_RENOVADO,
        PRESTAMO_DEVUELTO
    }
    
    public EventoPrestamo(String idPrestamo, String idUsuario, String idMaterial, LocalDateTime fecha, TipoEvento tipo) {
        this.idPrestamo = idPrestamo;
        this.idUsuario = idUsuario;
        this.idMaterial = idMaterial;
        this.fecha = fecha;
        this.tipo = tipo;
    }

    public String getIdPrestamo() {
        return idPrestamo;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public String getIdMaterial() {
        return idMaterial;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public TipoEvento getTipo() {
        return tipo;
    }
}
