package com.biblioteca.dominio.excepciones;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Excepción lanzada cuando se intenta operar con un usuario que está bloqueado.
 * 
 * Contiene información detallada sobre:
 * - El ID del usuario bloqueado
 * - Los motivos del bloqueo
 * - El monto de la multa pendiente
 * 
 * Código de error: USR-001
 */
public class UsuarioBloqueadoException extends BibliotecaException {
    
    private final String idUsuario;
    private final List<String> motivosBloqueo;
    private final BigDecimal multaPendiente;
    
    /**
     * Constructor completo de la excepción de usuario bloqueado.
     * 
     * @param idUsuario ID del usuario bloqueado
     * @param motivosBloqueo Lista de razones por las que fue bloqueado
     * @param multaPendiente Monto total de multa pendiente
     */
    public UsuarioBloqueadoException(String idUsuario, List<String> motivosBloqueo, BigDecimal multaPendiente) {
        super(construirMensaje(idUsuario, motivosBloqueo, multaPendiente), "USR-001");
        this.idUsuario = idUsuario;
        this.motivosBloqueo = motivosBloqueo != null ? motivosBloqueo : new ArrayList<>();
        this.multaPendiente = multaPendiente != null ? multaPendiente : BigDecimal.ZERO;
    }
    
    /**
     * Constructor sin multa pendiente conocida.
     * 
     * @param idUsuario ID del usuario bloqueado
     * @param motivosBloqueo Lista de razones por las que fue bloqueado
     */
    public UsuarioBloqueadoException(String idUsuario, List<String> motivosBloqueo) {
        this(idUsuario, motivosBloqueo, BigDecimal.ZERO);
    }
    
    /**
     * Constructor con un único motivo de bloqueo.
     * 
     * @param idUsuario ID del usuario bloqueado
     * @param motivo Razón del bloqueo
     * @param multaPendiente Monto total de multa pendiente
     */
    public UsuarioBloqueadoException(String idUsuario, String motivo, BigDecimal multaPendiente) {
        this(idUsuario, List.of(motivo), multaPendiente);
    }
    
    /**
     * Constructor con un único motivo y sin multa.
     * 
     * @param idUsuario ID del usuario bloqueado
     * @param motivo Razón del bloqueo
     */
    public UsuarioBloqueadoException(String idUsuario, String motivo) {
        this(idUsuario, List.of(motivo), BigDecimal.ZERO);
    }
    
    /**
     * Obtiene el ID del usuario bloqueado.
     * 
     * @return ID del usuario
     */
    public String getIdUsuario() {
        return idUsuario;
    }
    
    /**
     * Obtiene la lista de motivos por los que el usuario fue bloqueado.
     * 
     * @return Lista inmutable de motivos
     */
    public List<String> getMotivos() {
        return new ArrayList<>(motivosBloqueo);
    }
    
    /**
     * Obtiene el monto total de multa pendiente del usuario.
     * 
     * @return Monto de la multa en BigDecimal
     */
    public BigDecimal getMultaPendiente() {
        return multaPendiente;
    }
    
    /**
     * Construye el mensaje descriptivo de la excepción.
     * 
     * @param idUsuario ID del usuario
     * @param motivos Lista de motivos
     * @param multa Monto de la multa
     * @return Mensaje descriptivo
     */
    private static String construirMensaje(String idUsuario, List<String> motivos, BigDecimal multa) {
        StringBuilder mensaje = new StringBuilder();
        mensaje.append("Usuario '").append(idUsuario).append("' está bloqueado.");
        
        if (motivos != null && !motivos.isEmpty()) {
            mensaje.append(" Motivos: ").append(String.join(", ", motivos)).append(".");
        }
        
        if (multa != null && multa.compareTo(BigDecimal.ZERO) > 0) {
            mensaje.append(" Multa pendiente: $").append(multa);
        }
        
        return mensaje.toString();
    }
}