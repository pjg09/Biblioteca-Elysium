package com.biblioteca.dominio.entidades;

import com.biblioteca.dominio.enumeraciones.TipoMaterial;

public class Libro extends Material {
    private String isbn;
    private int numeroPaginas;
    private boolean esBestSeller;
    private boolean esReferencia;
    
    public Libro(String id, String titulo, String autor, String isbn, 
                 int numeroPaginas, boolean esBestSeller, boolean esReferencia) {
        super(id, titulo, autor, determinarTipo(esBestSeller, esReferencia));
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