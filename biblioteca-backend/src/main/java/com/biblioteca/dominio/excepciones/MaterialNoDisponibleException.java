package com.biblioteca.dominio.excepciones;

import java.time.LocalDateTime;

import com.biblioteca.dominio.enumeraciones.EstadoMaterial;

public class MaterialNoDisponibleException extends BibliotecaException {
    private String idMaterial;
    private EstadoMaterial estadoActual;
    private LocalDateTime fechaDisponibilidadEstimada;
    
    public MaterialNoDisponibleException(String idMaterial, EstadoMaterial estadoActual) {
        super("El material " + idMaterial + " no está disponible. Estado actual: " + estadoActual, "MAT-001");
        this.idMaterial = idMaterial;
        this.estadoActual = estadoActual;
    }
    
    public String getIdMaterial() {
        return idMaterial;
    }
    
    public EstadoMaterial getEstadoActual() {
        return estadoActual;
    }
}