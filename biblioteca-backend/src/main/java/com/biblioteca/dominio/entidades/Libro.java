package com.biblioteca.dominio.entidades;

import com.biblioteca.dominio.enumeraciones.TipoMaterial;
import com.biblioteca.dominio.objetosvalor.IdMaterial;

public class Libro extends Material {
    private String isbn;
    private int numeroPaginas;
    private boolean esBestSeller;
    private boolean esReferencia;
    
    public Libro(IdMaterial id, String titulo, String autor, String isbn, 
                 int numeroPaginas, boolean esBestSeller, boolean esReferencia, double precio) {
        super(id, titulo, autor, determinarTipo(esBestSeller, esReferencia), precio);
        this.isbn = isbn;
        this.numeroPaginas = numeroPaginas;
        this.esBestSeller = esBestSeller;
        this.esReferencia = esReferencia;
    }
    
    private static TipoMaterial determinarTipo(boolean esBestSeller, boolean esReferencia) {
        if (esReferencia) return TipoMaterial.LIBRO_REFERENCIA;
        if (esBestSeller) return TipoMaterial.LIBRO_BESTSELLER;
        return TipoMaterial.LIBRO_NORMAL;
    }
    
    public String getIsbn() {
        return isbn;
    }
    
    public int getNumeroPaginas() {
        return numeroPaginas;
    }
    
    public boolean esBestSeller() {
        return esBestSeller;
    }
    
    public boolean esReferencia() {
        return esReferencia;
    }
}