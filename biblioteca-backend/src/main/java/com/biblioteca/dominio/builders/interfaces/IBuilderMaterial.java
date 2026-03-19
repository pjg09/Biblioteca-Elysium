package com.biblioteca.dominio.builders.interfaces;

import java.time.LocalDateTime;

import com.biblioteca.dominio.entidades.Material;
public interface IBuilderMaterial {
    IBuilderMaterial conId(String id);
    IBuilderMaterial conTitulo(String titulo);
    IBuilderMaterial deAutor(String autor);
    IBuilderMaterial conPrecio(double precio);
    
    IBuilderMaterial esLibro();
    IBuilderMaterial conISBN(String isbn);
    IBuilderMaterial conPaginas(int numeroPaginas);
    IBuilderMaterial esBestSeller();
    IBuilderMaterial esReferencia();
    
    IBuilderMaterial esDVD();
    IBuilderMaterial conCodigo(String codigo);
    IBuilderMaterial conDuracion(int duracionMinutos);
    IBuilderMaterial dirigidoPor(String director);
    
    IBuilderMaterial esRevista();
    IBuilderMaterial conIssn(String issn);
    IBuilderMaterial numeroEdicion(int numero);
    IBuilderMaterial esUltimoNumero();
    
    IBuilderMaterial esEBook();
    IBuilderMaterial desdeUrl(String url);
    IBuilderMaterial licencias(int cant);
    IBuilderMaterial expiraEl(LocalDateTime fecha);
    
    Material construir();
}
