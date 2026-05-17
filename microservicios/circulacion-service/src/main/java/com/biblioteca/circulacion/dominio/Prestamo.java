package com.biblioteca.circulacion.dominio;

import com.biblioteca.circulacion.dominio.estados.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Agregado Prestamo implementando State Pattern.
 * 
 * El comportamiento del préstamo varía según su estado:
 * - ACTIVO: puede renovarse y devolverse
 * - COMPLETADO: es terminal, no permite más operaciones
 * - CANCELADO: es terminal, no permite más operaciones
 * 
 * Ventajas sobre if/else:
 * - Cada estado es testeable independientemente
 * - Fácil agregar nuevos estados
 * - Lógica clara y sin ramas complejas
 * - Cumple con OCP (Open/Closed Principle)
 */
public class Prestamo {

    private String id;
    private String idUsuario;
    private String idMaterial;
    private LocalDateTime fechaPrestamo;
    private LocalDateTime fechaDevolucionEsperada;
    private LocalDateTime fechaDevolucionReal;
    private int renovacionesUsadas;
    private IEstadoPrestamo estado; // Objeto de estado, no un String
    private String tipoPrestamo; // NORMAL / INTERBIBLIOTECARIO
    private String sede;

    private Prestamo() {}

    /**
     * Factory method: reconstruye un agregado desde persistencia sin disparar lógica de negocio.
     * Usado cuando se carga un Prestamo de la base de datos.
     */
    public static Prestamo reconstruir(String id, String idUsuario, String idMaterial,
            LocalDateTime fechaPrestamo, LocalDateTime fechaDevolucionEsperada,
            LocalDateTime fechaDevolucionReal, int renovacionesUsadas,
            String estadoString, String tipoPrestamo, String sede) {
        Prestamo p = new Prestamo();
        p.id = id;
        p.idUsuario = idUsuario;
        p.idMaterial = idMaterial;
        p.fechaPrestamo = fechaPrestamo;
        p.fechaDevolucionEsperada = fechaDevolucionEsperada;
        p.fechaDevolucionReal = fechaDevolucionReal;
        p.renovacionesUsadas = renovacionesUsadas;
        p.estado = reconstruirEstado(estadoString); // Convertir String a estado
        p.tipoPrestamo = tipoPrestamo;
        p.sede = sede;
        return p;
    }

    /**
     * Factory method: crea un nuevo Prestamo en estado ACTIVO.
     */
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
        p.estado = new PrestamoActivoState(); // Comienza en estado ACTIVO
        p.tipoPrestamo = tipo;
        p.sede = sede;
        return p;
    }

    /**
     * Intenta renovar el préstamo.
     * Delega al estado actual para validar y ejecutar la operación.
     */
    public void renovar(LocalDateTime nuevaFecha, int maxRenovaciones)
            throws OperacionNoPermitidaEnEstadoException {
        PrestamoContexto contexto = new PrestamoContexto(
                this.fechaDevolucionEsperada,
                this.renovacionesUsadas,
                this.estado
        );
        
        // El estado decide si se puede renovar
        this.estado.renovar(nuevaFecha, maxRenovaciones, contexto);
        
        // Si no lanzó excepción, actualizar el estado del préstamo
        this.fechaDevolucionEsperada = contexto.getFechaDevolucionEsperada();
        this.renovacionesUsadas = contexto.getRenovacionesUsadas();
        this.estado = contexto.getEstadoActual();
    }

    /**
     * Devuelve el préstamo.
     * Delega al estado actual para validar y ejecutar la operación.
     */
    public void devolver(LocalDateTime fecha)
            throws OperacionNoPermitidaEnEstadoException {
        PrestamoContexto contexto = new PrestamoContexto(
                this.fechaDevolucionEsperada,
                this.renovacionesUsadas,
                this.estado
        );
        
        // El estado decide si se puede devolver
        this.estado.devolver(fecha, contexto);
        
        // Si no lanzó excepción, actualizar el estado del préstamo
        this.fechaDevolucionReal = contexto.getFechaDevolucionReal();
        this.estado = contexto.getEstadoActual();
    }

    /**
     * Cancela el préstamo.
     * Delega al estado actual para validar y ejecutar la operación.
     */
    public void cancelar()
            throws OperacionNoPermitidaEnEstadoException {
        PrestamoContexto contexto = new PrestamoContexto(
                this.fechaDevolucionEsperada,
                this.renovacionesUsadas,
                this.estado
        );
        
        // El estado decide si se puede cancelar
        this.estado.cancelar(contexto);
        
        // Si no lanzó excepción, actualizar el estado del préstamo
        this.estado = contexto.getEstadoActual();
    }

    public long calcularDiasRetraso() {
        LocalDateTime referencia = fechaDevolucionReal != null ? fechaDevolucionReal : LocalDateTime.now();
        long dias = ChronoUnit.DAYS.between(fechaDevolucionEsperada, referencia);
        return Math.max(0, dias);
    }

    public boolean isActivo() {
        return estado.nombreEstado().equals("ACTIVO");
    }

    // --------- Getters ---------

    public String getId() { return id; }
    public String getIdUsuario() { return idUsuario; }
    public String getIdMaterial() { return idMaterial; }
    public LocalDateTime getFechaPrestamo() { return fechaPrestamo; }
    public LocalDateTime getFechaDevolucionEsperada() { return fechaDevolucionEsperada; }
    public LocalDateTime getFechaDevolucionReal() { return fechaDevolucionReal; }
    public int getRenovacionesUsadas() { return renovacionesUsadas; }
    
    /**
     * Retorna el estado del préstamo como String para compatibilidad con persistencia.
     */
    public String getEstado() {
        return estado.nombreEstado();
    }
    
    public IEstadoPrestamo getEstadoObjeto() {
        return estado;
    }
    
    public String getTipoPrestamo() { return tipoPrestamo; }
    public String getSede() { return sede; }

    // --------- Setters (para reconstrucción desde persistencia) ---------

    public void setId(String id) { this.id = id; }
    public void setIdUsuario(String idUsuario) { this.idUsuario = idUsuario; }
    public void setIdMaterial(String idMaterial) { this.idMaterial = idMaterial; }
    public void setFechaPrestamo(LocalDateTime fechaPrestamo) { this.fechaPrestamo = fechaPrestamo; }
    public void setFechaDevolucionEsperada(LocalDateTime fechaDevolucionEsperada) { this.fechaDevolucionEsperada = fechaDevolucionEsperada; }
    public void setFechaDevolucionReal(LocalDateTime fechaDevolucionReal) { this.fechaDevolucionReal = fechaDevolucionReal; }
    public void setRenovacionesUsadas(int renovacionesUsadas) { this.renovacionesUsadas = renovacionesUsadas; }
    
    public void setEstado(String estadoString) {
        this.estado = reconstruirEstado(estadoString);
    }
    
    public void setTipoPrestamo(String tipoPrestamo) { this.tipoPrestamo = tipoPrestamo; }
    public void setSede(String sede) { this.sede = sede; }

    // --------- Helpers privados ---------

    /**
     * Convierte un String de estado a un objeto IEstadoPrestamo.
     * Útil para reconstrucción desde persistencia.
     */
    private static IEstadoPrestamo reconstruirEstado(String estadoString) {
        switch (estadoString) {
            case "ACTIVO":
                return new PrestamoActivoState();
            case "COMPLETADO":
                return new PrestamoCompletadoState();
            case "CANCELADO":
                return new PrestamoCanceladoState();
            default:
                throw new IllegalArgumentException("Estado de préstamo desconocido: " + estadoString);
        }
    }
}
