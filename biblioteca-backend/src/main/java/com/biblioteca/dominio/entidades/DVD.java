package com.biblioteca.dominio.entidades;

import com.biblioteca.dominio.enumeraciones.TipoMaterial;

public class DVD extends Material {
    private String codigo;
    private int duracionMinutos;
    private String director;
    
    public DVD(String id, String titulo, String autor, String codigo, int duracionMinutos, String director) {
        super(id, titulo, autor, TipoMaterial.DVD);
        this.codigo = codigo;
        this.duracionMinutos = duracionMinutos;
        this.director = director;
    }
    
    public String getCodigo() {
        return codigo;
    }
    
    public int getDuracionMinutos() {
        return duracionMinutos;
    }
    
    public String getDirector() {
        return director;
    }
}