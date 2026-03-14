package com.biblioteca.dominio.builders;

import java.time.LocalDateTime;
import java.util.UUID;

import com.biblioteca.dominio.entidades.DVD;
import com.biblioteca.dominio.entidades.EBook;
import com.biblioteca.dominio.entidades.Libro;
import com.biblioteca.dominio.entidades.Material;
import com.biblioteca.dominio.entidades.Revista;
import com.biblioteca.dominio.enumeraciones.TipoMaterial;
import com.biblioteca.dominio.objetosvalor.IdMaterial;

public class MaterialBuilder {
    // Campos comunes
    private IdMaterial id;
    private String titulo;
    private String autor;
    private TipoMaterial tipo;
    
    // Campos específicos de Libro
    private String isbn;
    private int numeroPaginas;
    private boolean esBestSeller;
    private boolean esReferencia;
    
    // Campos específicos de DVD
    private String codigo;
    private int duracionMinutos;
    private String director;
    
    // Campos específicos de Revista
    private String issn;
    private int numeroEdicion;
    private boolean esUltimoNumero;
    
    // Campos específicos de EBook
    private String urlDescarga;
    private int licenciasDisponibles;
    private LocalDateTime fechaVencimientoLicencia;
    
    // =========================================
    // MÉTODOS COMUNES
    // =========================================
    
    public MaterialBuilder conId(IdMaterial id) {
        this.id = id;
        return this;
    }
    
    public MaterialBuilder conTitulo(String titulo) {
        this.titulo = titulo;
        return this;
    }
    
    public MaterialBuilder deAutor(String autor) {
        this.autor = autor;
        return this;
    }
    
    // =========================================
    // CONFIGURACIÓN TIPO LIBRO
    // =========================================
    
    public MaterialBuilder esLibro() {
        this.tipo = TipoMaterial.LIBRO_NORMAL;
        return this;
    }
    
    public MaterialBuilder conISBN(String isbn) {
        this.isbn = isbn;
        return this;
    }
    
    public MaterialBuilder conPaginas(int numeroPaginas) {
        this.numeroPaginas = numeroPaginas;
        return this;
    }
    
    public MaterialBuilder esBestSeller() {
        this.esBestSeller = true;
        this.tipo = TipoMaterial.LIBRO_BESTSELLER;
        return this;
    }
    
    public MaterialBuilder esReferencia() {
        this.esReferencia = true;
        this.tipo = TipoMaterial.LIBRO_REFERENCIA;
        return this;
    }
    
    // =========================================
    // CONFIGURACIÓN TIPO DVD
    // =========================================
    
    public MaterialBuilder esDVD() {
        this.tipo = TipoMaterial.DVD;
        return this;
    }
    
    public MaterialBuilder conCodigo(String codigo) {
        this.codigo = codigo;
        return this;
    }
    
    public MaterialBuilder conDuracion(int duracionMinutos) {
        this.duracionMinutos = duracionMinutos;
        return this;
    }
    
    public MaterialBuilder dirigidoPor(String director) {
        this.director = director;
        return this;
    }
    
    // =========================================
    // CONFIGURACIÓN TIPO REVISTA
    // =========================================

    public MaterialBuilder esRevista() {
        this.tipo = TipoMaterial.REVISTA;
        return this;
    }

    public MaterialBuilder conIssn(String issn) {
        this.issn = issn;
        return this;
    }

    public MaterialBuilder numeroEdicion(int numero) {
        this.numeroEdicion = numero;
        return this;
    }

    public MaterialBuilder esUltimoNumero() {
        this.esUltimoNumero = true;
        return this;
    }

    // =========================================
    // CONFIGURACIÓN TIPO EBOOK
    // =========================================

    public MaterialBuilder esEBook() {
        this.tipo = TipoMaterial.EBOOK;
        return this;
    }

    public MaterialBuilder desdeUrl(String url) {
        this.urlDescarga = url;
        return this;
    }

    public MaterialBuilder licencias(int cant) {
        this.licenciasDisponibles = cant;
        return this;
    }

    public MaterialBuilder expiraEl(LocalDateTime fecha) {
        this.fechaVencimientoLicencia = fecha;
        return this;
    }

    // =========================================
    // BUILD
    // =========================================
    
    public Material construir() {
        validar();
        
        switch (tipo) {
            case LIBRO_NORMAL:
            case LIBRO_BESTSELLER:
            case LIBRO_REFERENCIA:
                return construirLibro();
            case DVD:
                return construirDVD();
            case REVISTA:
                return construirRevista();
            case EBOOK:
                return construirEBook();
            default:
                throw new IllegalStateException("Tipo de material no especificado");
        }
    }
    
    private void validar() {
        if (titulo == null || titulo.trim().isEmpty()) {
            throw new IllegalStateException("Título es obligatorio");
        }
        if (tipo == null) {
            throw new IllegalStateException("Tipo de material es obligatorio");
        }
    }
    
    private Libro construirLibro() {
        return new Libro(
            id != null ? id : generarId("LIB"),
            titulo,
            autor,
            isbn,
            numeroPaginas,
            esBestSeller,
            esReferencia
        );
    }
    
    private DVD construirDVD() {
        return new DVD(
            id != null ? id : generarId("DVD"),
            titulo,
            autor,
            codigo,
            duracionMinutos,
            director
        );
    }
    
    private Revista construirRevista() {
        return new Revista(
            id != null ? id : generarId("REV"),
            titulo,
            autor,
            issn,
            numeroEdicion,
            esUltimoNumero
        );
    }

    private EBook construirEBook() {
        return new EBook(
            id != null ? id : generarId("EBK"),
            titulo,
            autor,
            urlDescarga,
            licenciasDisponibles,
            fechaVencimientoLicencia != null ? fechaVencimientoLicencia : LocalDateTime.now().plusYears(1)
        );
    }

    private IdMaterial generarId(String prefijo) {
        // En una implementación real se puede validar formato, aquí generamos con UUID format. 
        // Genera algo como: MAT-123456
        String num = String.format("%06d", (int)(Math.random() * 1000000));
        return new IdMaterial("MAT-" + num);
    }
}
