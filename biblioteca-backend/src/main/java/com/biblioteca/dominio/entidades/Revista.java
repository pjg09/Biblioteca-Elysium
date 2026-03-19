package com.biblioteca.dominio.entidades;

import com.biblioteca.dominio.enumeraciones.TipoMaterial;
public class Revista extends Material {
    private String issn;
    private int numeroEdicion;
    private boolean esUltimoNumero;
    
    public Revista(String id, String titulo, String autor, String issn, int numeroEdicion, boolean esUltimoNumero, double precio) {
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