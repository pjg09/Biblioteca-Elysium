package com.biblioteca.multas.dominio;

import com.biblioteca.commons.excepciones.OperacionNoPermitidaEnEstadoMultaException;
import com.biblioteca.multas.dominio.estados.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Agregado raíz de Multa.
 * Protege invariantes: solo puede transicionar entre estados válidos mediante State Pattern.
 * 
 * Estados: GENERADA → PAGADA o CONDONADA (terminales)
 */
public class Multa {

    private String id;
    private String idPrestamo;
    private String idUsuario;
    private String tipoMulta;
    private double montoTotal;
    private String estado;  // GENERADA, PAGADA, CONDONADA
    private LocalDateTime fechaGeneracion;
    private LocalDateTime fechaPago;
    private String motivo;
    
    private IEstadoMulta estadoActual;
    private List<Object> domainEvents;

    /**
     * Constructor privado - usar factory methods.
     */
    private Multa() {
        this.domainEvents = new ArrayList<>();
    }

    /**
     * Constructor para reconstruir desde persistencia (usado por MultaEntity.toDomain()).
     */
    public Multa(String id, String idPrestamo, String idUsuario, String tipoMulta,
                 double montoTotal, String estado, LocalDateTime fechaGeneracion,
                 LocalDateTime fechaPago, String motivo) {
        this.id = id;
        this.idPrestamo = idPrestamo;
        this.idUsuario = idUsuario;
        this.tipoMulta = tipoMulta;
        this.montoTotal = montoTotal;
        this.estado = estado;
        this.fechaGeneracion = fechaGeneracion;
        this.fechaPago = fechaPago;
        this.motivo = motivo;
        this.domainEvents = new ArrayList<>();
        this.estadoActual = reconstruirEstado(estado);
    }

    /**
     * Factory method para crear una nueva multa.
     */
    public static Multa crear(String id, String idPrestamo, String idUsuario,
                             String tipoMulta, double montoTotal) {
        Multa multa = new Multa();
        multa.id = id;
        multa.idPrestamo = idPrestamo;
        multa.idUsuario = idUsuario;
        multa.tipoMulta = tipoMulta;
        multa.montoTotal = montoTotal;
        multa.estado = "GENERADA";
        multa.estadoActual = new MultaGeneradaState();
        multa.fechaGeneracion = LocalDateTime.now();
        multa.domainEvents = new ArrayList<>();
        return multa;
    }

    /**
     * Paga la multa (transición GENERADA → PAGADA).
     * El estado decide si la transición es válida.
     */
    public void pagar(LocalDateTime fechaPago) throws OperacionNoPermitidaEnEstadoMultaException {
        if (estadoActual == null) {
            throw new OperacionNoPermitidaEnEstadoMultaException(
                "pagar",
                "Estado actual no inicializado"
            );
        }

        MultaContexto contexto = new MultaContexto(this.montoTotal, this.estadoActual);
        estadoActual.pagar(fechaPago, contexto);
        
        // Si llegamos aquí, la transición fue válida
        this.estado = "PAGADA";
        this.fechaPago = fechaPago;
        this.estadoActual = new MultaPagadaState();
    }

    /**
     * Condona la multa (transición GENERADA → CONDONADA).
     * El estado decide si la transición es válida.
     */
    public void condonar() throws OperacionNoPermitidaEnEstadoMultaException {
        if (estadoActual == null) {
            throw new OperacionNoPermitidaEnEstadoMultaException(
                "condonar",
                "Estado actual no inicializado"
            );
        }

        MultaContexto contexto = new MultaContexto(this.montoTotal, this.estadoActual);
        estadoActual.condonar(contexto);
        
        // Si llegamos aquí, la transición fue válida
        this.estado = "CONDONADA";
        this.estadoActual = new MultaCondonadaState();
    }

    /**
     * Reconstruye el objeto de estado (IEstadoMulta) desde el string de estado.
     */
    private IEstadoMulta reconstruirEstado(String estadoString) {
        switch (estadoString) {
            case "GENERADA":
                return new MultaGeneradaState();
            case "PAGADA":
                return new MultaPagadaState();
            case "CONDONADA":
                return new MultaCondonadaState();
            default:
                throw new IllegalArgumentException("Estado de multa desconocido: " + estadoString);
        }
    }

    // Getters (requeridos por MultaEntity.fromDomain())

    public String getId() {
        return id;
    }

    public String getIdPrestamo() {
        return idPrestamo;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public String getTipoMulta() {
        return tipoMulta;
    }

    public double getMontoTotal() {
        return montoTotal;
    }

    public String getEstado() {
        return estado;
    }

    public LocalDateTime getFechaGeneracion() {
        return fechaGeneracion;
    }

    public LocalDateTime getFechaPago() {
        return fechaPago;
    }

    public String getMotivo() {
        return motivo;
    }

    /**
     * Extrae y limpia los eventos de dominio emitidos durante operaciones.
     */
    public List<Object> pullEvents() {
        List<Object> events = new ArrayList<>(domainEvents);
        domainEvents.clear();
        return events;
    }
}
