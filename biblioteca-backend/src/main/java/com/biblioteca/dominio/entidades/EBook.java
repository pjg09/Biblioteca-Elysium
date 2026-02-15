package com.biblioteca.dominio.entidades;

import java.time.LocalDateTime;

import com.biblioteca.dominio.enumeraciones.TipoMaterial;

public class EBook extends Material {
    private String urlDescarga;
    private int licenciasDisponibles;
    private LocalDateTime fechaVencimientoLicencia;
    
    public EBook(String id, String titulo, String autor, String urlDescarga, 
                 int licenciasDisponibles, LocalDateTime fechaVencimientoLicencia) {
        super(id, titulo, autor, TipoMaterial.EBOOK);
        this.urlDescarga = urlDescarga;
        this.licenciasDisponibles = licenciasDisponibles;
        this.fechaVencimientoLicencia = fechaVencimientoLicencia;
    }
    
    public String getUrlDescarga() {
        return urlDescarga;
    }
    
    public int getLicenciasDisponibles() {
        return licenciasDisponibles;
    }
    
    public boolean tieneLicenciasDisponibles() {
        return licenciasDisponibles > 0 && 
               LocalDateTime.now().isBefore(fechaVencimientoLicencia);
    }
    
    public LocalDateTime getFechaVencimientoLicencia() {
        return fechaVencimientoLicencia;
    }
}