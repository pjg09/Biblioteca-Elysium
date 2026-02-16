package com.biblioteca.servicios.calculadores;

import com.biblioteca.dominio.entidades.DVD;
import com.biblioteca.dominio.entidades.Libro;
import com.biblioteca.dominio.entidades.Material;
import com.biblioteca.dominio.entidades.Multa;
import com.biblioteca.dominio.entidades.MultaPorPerdida;
import com.biblioteca.dominio.entidades.Revista;
import com.biblioteca.dominio.enumeraciones.TipoMulta;
import com.biblioteca.dominio.objetosvalor.ContextoMulta;
import com.biblioteca.repositorios.IRepositorio;
import com.biblioteca.servicios.interfaces.ICalculadorMulta;

public class CalculadorMultaPorPerdida implements ICalculadorMulta {
    private IRepositorio<Material> repoMaterial;
    private static final double PORCENTAJE_RECARGO = 0.20; // 20% de recargo
    
    public CalculadorMultaPorPerdida(IRepositorio<Material> repoMaterial) {
        this.repoMaterial = repoMaterial;
    }
    
    @Override
    public Multa calcular(ContextoMulta contexto) {
        Material material = repoMaterial.obtenerPorId(contexto.getIdMaterial());
        if (material == null) {
            throw new IllegalArgumentException("Material no encontrado");
        }
        
        // Valor estimado del material
        double valorMaterial = calcularValorMaterial(material);
        
        MultaPorPerdida multa = new MultaPorPerdida(
            contexto.getIdPrestamo(),
            contexto.getIdUsuario(),
            valorMaterial,
            PORCENTAJE_RECARGO
        );
        
        return multa;
    }
    
    @Override
    public boolean puedeCalcular(ContextoMulta contexto) {
        return contexto.getTipoMulta() == TipoMulta.POR_PERDIDA;
    }
    
    private double calcularValorMaterial(Material material) {
        switch (material.getTipo()) {
            case LIBRO_NORMAL: 
                if (material instanceof Libro) {
                    Libro libro = (Libro) material;
                    // Los bestseller y referencia ya tienen su propio tipo
                    return 50000;
                }
                return 50000;
                
            case LIBRO_BESTSELLER: 
                return 80000;
                
            case LIBRO_REFERENCIA: 
                return 120000;
                
            case DVD: 
                if (material instanceof DVD) {
                    DVD dvd = (DVD) material;
                    // A mayor duración, mayor valor (simplificado)
                    return 30000 + (dvd.getDuracionMinutos() * 100);
                }
                return 30000;
                
            case REVISTA: 
                if (material instanceof Revista) {
                    Revista revista = (Revista) material;
                    return 15000 + (revista.esUltimoNumero() ? 5000 : 0);
                }
                return 15000;
                
            case EBOOK: 
                return 40000;
                
            default: 
                return 50000;
        }
    }
}