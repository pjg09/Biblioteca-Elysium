package com.biblioteca.dominio.entidades;

import com.biblioteca.dominio.enumeraciones.TipoMaterial;
import com.biblioteca.dominio.objetosvalor.IdMaterial;

public class Revista extends Material {
    private String issn;
    private int numeroEdicion;
    private boolean esUltimoNumero;
    
    public Revista(IdMaterial id, String titulo, String autor, String issn, int numeroEdicion, boolean esUltimoNumero, double precio) {
        super(id, titulo, autor, TipoMaterial.REVISTA, precio);
        this.issn = issn;
        this.numeroEdicion = numeroEdicion;
        this.esUltimoNumero = esUltimoNumero;
    }
    
    public String getIssn() {
        return issn;
    }
    
    public int getNumeroEdicion() {
        return numeroEdicion;
    }
    
    public boolean esUltimoNumero() {
        return esUltimoNumero;
    }
}