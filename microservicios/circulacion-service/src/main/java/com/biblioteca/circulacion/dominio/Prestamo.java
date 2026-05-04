package com.biblioteca.circulacion.dominio;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Prestamo {

    private String id;
    private String idUsuario;
    private String idMaterial;
    private LocalDateTime fechaPrestamo;
    private LocalDateTime fechaDevolucionEsperada;
    private LocalDateTime fechaDevolucionReal;
    private int renovacionesUsadas;
    private String estado; // ACTIVO / COMPLETADO / CANCELADO
    private String tipoPrestamo; // NORMAL / INTERBIBLIOTECARIO
    private String sede;

    private Prestamo() {}

    /** Reconstruye un agregado desde persistencia sin disparar lógica de negocio. */
    public static Prestamo reconstruir(String id, String idUsuario, String idMaterial,
            LocalDateTime fechaPrestamo, LocalDateTime fechaDevolucionEsperada,
            LocalDateTime fechaDevolucionReal, int renovacionesUsadas,
            String estado, String tipoPrestamo, String sede) {
        Prestamo p = new Prestamo();
        p.id = id; p.idUsuario = idUsuario; p.idMaterial = idMaterial;
        p.fechaPrestamo = fechaPrestamo; p.fechaDevolucionEsperada = fechaDevolucionEsperada;
        p.fechaDevolucionReal = fechaDevolucionReal; p.renovacionesUsadas = renovacionesUsadas;
        p.estado = estado; p.tipoPrestamo = tipoPrestamo; p.sede = sede;
        return p;
    }

    public static Prestamo crear(String id,
                                 String idUsuario,
                                 String idMaterial,
                                 LocalDateTime fechaDevolucion,
                                 String tipo,
                                 String sede) {
        Prestamo p = new Prestamo();
        p.id = id;
        p.idUsuario = idUsuario;
        p.idMaterial = idMaterial;
        p.fechaPrestamo = LocalDateTime.now();
        p.fechaDevolucionEsperada = fechaDevolucion;
        p.fechaDevolucionReal = null;
        p.renovacionesUsadas = 0;
        p.estado = "ACTIVO";
        p.tipoPrestamo = tipo;
        p.sede = sede;
        return p;
    }

    public void renovar(LocalDateTime nuevaFecha, int maxRenovaciones) {
        if (!"ACTIVO".equals(estado)) {
            throw new IllegalStateException("Solo se pueden renovar prestamos en estado ACTIVO. Estado actual: " + estado);
        }
        if (renovacionesUsadas >= maxRenovaciones) {
            throw new IllegalStateException(
                    "Se ha alcanzado el limite de renovaciones (" + maxRenovaciones + ") para este prestamo.");
        }
        this.fechaDevolucionEsperada = nuevaFecha;
        this.renovacionesUsadas++;
    }

    public void devolver(LocalDateTime fecha) {
        if (!"ACTIVO".equals(estado)) {
            throw new IllegalStateException("Solo se pueden devolver prestamos en estado ACTIVO. Estado actual: " + estado);
        }
        this.fechaDevolucionReal = fecha;
        this.estado = "COMPLETADO";
    }

    public long calcularDiasRetraso() {
        LocalDateTime referencia = fechaDevolucionReal != null ? fechaDevolucionReal : LocalDateTime.now();
        long dias = ChronoUnit.DAYS.between(fechaDevolucionEsperada, referencia);
        return Math.max(0, dias);
    }

    public boolean isActivo() {
        return "ACTIVO".equals(estado);
    }

    // --- Getters ---

    public String getId() { return id; }
    public String getIdUsuario() { return idUsuario; }
    public String getIdMaterial() { return idMaterial; }
    public LocalDateTime getFechaPrestamo() { return fechaPrestamo; }
    public LocalDateTime getFechaDevolucionEsperada() { return fechaDevolucionEsperada; }
    public LocalDateTime getFechaDevolucionReal() { return fechaDevolucionReal; }
    public int getRenovacionesUsadas() { return renovacionesUsadas; }
    public String getEstado() { return estado; }
    public String getTipoPrestamo() { return tipoPrestamo; }
    public String getSede() { return sede; }

    // --- Setters (needed for reconstruction from persistence) ---

    public void setId(String id) { this.id = id; }
    public void setIdUsuario(String idUsuario) { this.idUsuario = idUsuario; }
    public void setIdMaterial(String idMaterial) { this.idMaterial = idMaterial; }
    public void setFechaPrestamo(LocalDateTime fechaPrestamo) { this.fechaPrestamo = fechaPrestamo; }
    public void setFechaDevolucionEsperada(LocalDateTime fechaDevolucionEsperada) { this.fechaDevolucionEsperada = fechaDevolucionEsperada; }
    public void setFechaDevolucionReal(LocalDateTime fechaDevolucionReal) { this.fechaDevolucionReal = fechaDevolucionReal; }
    public void setRenovacionesUsadas(int renovacionesUsadas) { this.renovacionesUsadas = renovacionesUsadas; }
    public void setEstado(String estado) { this.estado = estado; }
    public void setTipoPrestamo(String tipoPrestamo) { this.tipoPrestamo = tipoPrestamo; }
    public void setSede(String sede) { this.sede = sede; }
}
