package com.biblioteca.multas.dominio;

import com.biblioteca.multas.dominio.estados.*;
import java.time.LocalDateTime;

/**
 * Agregado Multa implementando State Pattern.
 * 
 * El comportamiento de la multa varía según su estado:
 * - GENERADA: Multa recién creada, esperando resolución
 * - PAGADA: Multa pagada por el usuario (terminal)
 * - CONDONADA: Multa condonada por el sistema (terminal)
 * 
 * Ventajas sobre if/else:
 * - Cada estado es testeable independientemente
 * - Fácil agregar nuevos estados
 * - Lógica clara y sin ramas complejas
 * - Cumple con OCP (Open/Closed Principle)
 */
public class Multa {

    private String id;
    private String idPrestamo;
    private String idUsuario;
    private String tipoMulta;
    private double montoTotal;
    private IEstadoMulta estado; // Objeto de estado, no un String
    private LocalDateTime fechaGeneracion;
    private LocalDateTime fechaPago;
    private String motivo;

    public Multa() {
    }

    public Multa(String id, String idPrestamo, String idUsuario, String tipoMulta,
                 double montoTotal, String estadoString, LocalDateTime fechaGeneracion,
                 LocalDateTime fechaPago, String motivo) {
        this.id = id;
        this.idPrestamo = idPrestamo;
        this.idUsuario = idUsuario;
        this.tipoMulta = tipoMulta;
        this.montoTotal = montoTotal;
        this.estado = reconstruirEstado(estadoString); // Convertir String a estado
        this.fechaGeneracion = fechaGeneracion;
        this.fechaPago = fechaPago;
        this.motivo = motivo;
    }

    // -------------------------------------------------------------------------
    // Comportamiento de dominio con State Pattern
    // -------------------------------------------------------------------------

    /**
     * Registra el pago de la multa.
     * Delega al estado actual para validar y ejecutar la operación.
     */
    public void pagar(LocalDateTime fecha)
            throws OperacionNoPermitidaEnEstadoMultaException {
        MultaContexto contexto = new MultaContexto(this.montoTotal, this.estado);
        
        // El estado decide si se puede pagar
        this.estado.pagar(fecha, contexto);
        
        // Si no lanzó excepción, actualizar el estado de la multa
        this.fechaPago = contexto.getFechaPago();
        this.estado = contexto.getEstadoActual();
    }

    /**
     * Condona la multa.
     * Delega al estado actual para validar y ejecutar la operación.
     */
    public void condonar()
            throws OperacionNoPermitidaEnEstadoMultaException {
        MultaContexto contexto = new MultaContexto(this.montoTotal, this.estado);
        
        // El estado decide si se puede condonar
        this.estado.condonar(contexto);
        
        // Si no lanzó excepción, actualizar el estado de la multa
        this.estado = contexto.getEstadoActual();
    }

    /**
     * Indica si la multa está pendiente de resolución.
     */
    public boolean esPendiente() {
        return this.estado.esPendiente();
    }

    // -------------------------------------------------------------------------
    // Getters y Setters
    // -------------------------------------------------------------------------

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdPrestamo() {
        return idPrestamo;
    }

    public void setIdPrestamo(String idPrestamo) {
        this.idPrestamo = idPrestamo;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getTipoMulta() {
        return tipoMulta;
    }

    public void setTipoMulta(String tipoMulta) {
        this.tipoMulta = tipoMulta;
    }

    public double getMontoTotal() {
        return montoTotal;
    }

    public void setMontoTotal(double montoTotal) {
        this.montoTotal = montoTotal;
    }

    /**
     * Retorna el estado de la multa como String para compatibilidad con persistencia.
     */
    public String getEstado() {
        return estado.nombreEstado();
    }

    public IEstadoMulta getEstadoObjeto() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = reconstruirEstado(estado);
    }

    public LocalDateTime getFechaGeneracion() {
        return fechaGeneracion;
    }

    public void setFechaGeneracion(LocalDateTime fechaGeneracion) {
        this.fechaGeneracion = fechaGeneracion;
    }

    public LocalDateTime getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(LocalDateTime fechaPago) {
        this.fechaPago = fechaPago;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    // --------- Helper privado ---------

    /**
     * Convierte un String de estado a un objeto IEstadoMulta.
     * Útil para reconstrucción desde persistencia.
     */
    private static IEstadoMulta reconstruirEstado(String estadoString) {
        switch (estadoString) {
            case "GENERADA":
            case "PENDIENTE": // Para compatibilidad hacia atrás
                return new MultaGeneradaState();
            case "PAGADA":
                return new MultaPagadaState();
            case "CONDONADA":
                return new MultaCondonadaState();
            default:
                throw new IllegalArgumentException("Estado de multa desconocido: " + estadoString);
        }
    }
}
