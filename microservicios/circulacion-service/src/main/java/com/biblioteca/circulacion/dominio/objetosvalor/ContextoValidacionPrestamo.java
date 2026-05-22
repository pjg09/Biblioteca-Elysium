package com.biblioteca.circulacion.dominio.objetosvalor;

/**
 * Contexto que encapsula los datos necesarios para validar un préstamo.
 * Usado por todas las reglas de validación (IReglaValidacion).
 * 
 * Patrón: Strategy Pattern + Contexto
 */
public class ContextoValidacionPrestamo {
    private String idUsuario;
    private String tipoUsuario;
    private boolean usuarioActivo;
    private String idMaterial;
    private boolean materialDisponible;
    private int prestamosActivos;
    private int limiteMaximoPrestamos;
    private double deudaPendiente;
    private String estadoMaterial;
    
    public ContextoValidacionPrestamo() {
    }
    
    // Getters
    public String getIdUsuario() {
        return idUsuario;
    }
    
    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }
    
    public String getTipoUsuario() {
        return tipoUsuario;
    }
    
    public void setTipoUsuario(String tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }
    
    public boolean isUsuarioActivo() {
        return usuarioActivo;
    }
    
    public void setUsuarioActivo(boolean usuarioActivo) {
        this.usuarioActivo = usuarioActivo;
    }
    
    public String getIdMaterial() {
        return idMaterial;
    }
    
    public void setIdMaterial(String idMaterial) {
        this.idMaterial = idMaterial;
    }
    
    public boolean isMaterialDisponible() {
        return materialDisponible;
    }
    
    public void setMaterialDisponible(boolean materialDisponible) {
        this.materialDisponible = materialDisponible;
    }
    
    public int getPrestamosActivos() {
        return prestamosActivos;
    }
    
    public void setPrestamosActivos(int prestamosActivos) {
        this.prestamosActivos = prestamosActivos;
    }
    
    public int getLimiteMaximoPrestamos() {
        return limiteMaximoPrestamos;
    }
    
    public void setLimiteMaximoPrestamos(int limiteMaximoPrestamos) {
        this.limiteMaximoPrestamos = limiteMaximoPrestamos;
    }
    
    public double getDeudaPendiente() {
        return deudaPendiente;
    }
    
    public void setDeudaPendiente(double deudaPendiente) {
        this.deudaPendiente = deudaPendiente;
    }
    
    public String getEstadoMaterial() {
        return estadoMaterial;
    }
    
    public void setEstadoMaterial(String estadoMaterial) {
        this.estadoMaterial = estadoMaterial;
    }
}
