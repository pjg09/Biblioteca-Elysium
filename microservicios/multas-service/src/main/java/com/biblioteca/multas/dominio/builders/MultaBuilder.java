package com.biblioteca.multas.dominio.builders;

import com.biblioteca.multas.dominio.agregados.Multa;
import com.biblioteca.commons.objetosvalor.Resultado;
import com.biblioteca.commons.patrones.IBuilder;

/**
 * Builder pattern para construcción y validación de Multa.
 * 
 * Implementa validaciones de dominio:
 * - ID no vacío
 * - ID Préstamo no vacío
 * - ID Usuario no vacío
 * - Tipo de Multa válido (RETRASO, PERDIDA, DAÑO)
 * - Monto total >= 0
 * - Estado válido (GENERADA, PAGADA, CONDONADA)
 * 
 * Retorna Resultado<Multa> con error si alguna validación falla.
 */
public class MultaBuilder implements IBuilder<Multa> {
    
    private String id;
    private String idPrestamo;
    private String idUsuario;
    private String tipoMulta;
    private double montoTotal;
    private String estado;
    private String motivo;
    
    public MultaBuilder conId(String id) {
        this.id = id;
        return this;
    }
    
    public MultaBuilder conIdPrestamo(String idPrestamo) {
        this.idPrestamo = idPrestamo;
        return this;
    }
    
    public MultaBuilder conIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
        return this;
    }
    
    public MultaBuilder conTipoMulta(String tipoMulta) {
        this.tipoMulta = tipoMulta;
        return this;
    }
    
    public MultaBuilder conMontoTotal(double montoTotal) {
        this.montoTotal = montoTotal;
        return this;
    }
    
    public MultaBuilder conEstado(String estado) {
        this.estado = estado;
        return this;
    }
    
    public MultaBuilder conMotivo(String motivo) {
        this.motivo = motivo;
        return this;
    }
    
    @Override
    public Resultado<Multa> construir() {
        // Validaciones
        if (id == null || id.trim().isEmpty()) {
            return Resultado.fallo("ID de multa no puede ser vacío");
        }
        
        if (idPrestamo == null || idPrestamo.trim().isEmpty()) {
            return Resultado.fallo("ID de préstamo no puede ser vacío");
        }
        
        if (idUsuario == null || idUsuario.trim().isEmpty()) {
            return Resultado.fallo("ID de usuario no puede ser vacío");
        }
        
        if (tipoMulta == null || !esValidoTipoMulta(tipoMulta)) {
            return Resultado.fallo("Tipo de multa inválido. Debe ser: RETRASO, PERDIDA o DAÑO");
        }
        
        if (montoTotal < 0) {
            return Resultado.fallo("Monto total no puede ser negativo: " + montoTotal);
        }
        
        if (estado == null || !esValidoEstado(estado)) {
            return Resultado.fallo("Estado de multa inválido. Debe ser: GENERADA, PAGADA o CONDONADA");
        }
        
        // Construir la multa
        Multa multa = new Multa(
                id,
                idPrestamo,
                idUsuario,
                tipoMulta,
                montoTotal,
                estado,
                null, // fechaGeneracion se establece en la aplicación
                null, // fechaPago null inicialmente
                motivo
        );
        
        return Resultado.exitoso(multa);
    }
    
    private boolean esValidoTipoMulta(String tipo) {
        return tipo.toUpperCase().matches("^(RETRASO|PERDIDA|DAÑO)$");
    }
    
    private boolean esValidoEstado(String estado) {
        return estado.toUpperCase().matches("^(GENERADA|PAGADA|CONDONADA)$");
    }
}
