package com.biblioteca.servicios.implementaciones;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import com.biblioteca.dominio.enumeraciones.TipoMaterial;
import com.biblioteca.dominio.enumeraciones.TipoUsuario;
import com.biblioteca.servicios.interfaces.IPoliticaTiempoService;

public class PoliticaTiempoPorTipoService implements IPoliticaTiempoService {
    
    private final Map<TipoMaterial, Map<TipoUsuario, Integer>> politicas;
    
    public PoliticaTiempoPorTipoService() {
        this.politicas = new HashMap<>();
        inicializarPoliticas();
    }
    
    private void inicializarPoliticas() {
        // Estudiante
        Map<TipoUsuario, Integer> politicasLibroNormal = new HashMap<>();
        politicasLibroNormal.put(TipoUsuario.ESTUDIANTE, 15);
        politicasLibroNormal.put(TipoUsuario.PROFESOR, 30);
        politicasLibroNormal.put(TipoUsuario.INVESTIGADOR, 45);
        politicasLibroNormal.put(TipoUsuario.PUBLICO_GENERAL, 10);
        politicas.put(TipoMaterial.LIBRO_NORMAL, politicasLibroNormal);
        
        // Libro Best Seller
        Map<TipoUsuario, Integer> politicasLibroBestSeller = new HashMap<>();
        politicasLibroBestSeller.put(TipoUsuario.ESTUDIANTE, 7);
        politicasLibroBestSeller.put(TipoUsuario.PROFESOR, 15);
        politicasLibroBestSeller.put(TipoUsuario.INVESTIGADOR, 20);
        politicasLibroBestSeller.put(TipoUsuario.PUBLICO_GENERAL, 5);
        politicas.put(TipoMaterial.LIBRO_BESTSELLER, politicasLibroBestSeller);
        
        // Libro Referencia (no se presta, pero por si acaso)
        Map<TipoUsuario, Integer> politicasLibroReferencia = new HashMap<>();
        politicasLibroReferencia.put(TipoUsuario.ESTUDIANTE, 0);
        politicasLibroReferencia.put(TipoUsuario.PROFESOR, 2);
        politicasLibroReferencia.put(TipoUsuario.INVESTIGADOR, 3);
        politicasLibroReferencia.put(TipoUsuario.PUBLICO_GENERAL, 0);
        politicas.put(TipoMaterial.LIBRO_REFERENCIA, politicasLibroReferencia);
        
        // DVD
        Map<TipoUsuario, Integer> politicasDVD = new HashMap<>();
        politicasDVD.put(TipoUsuario.ESTUDIANTE, 3);
        politicasDVD.put(TipoUsuario.PROFESOR, 7);
        politicasDVD.put(TipoUsuario.INVESTIGADOR, 10);
        politicasDVD.put(TipoUsuario.PUBLICO_GENERAL, 2);
        politicas.put(TipoMaterial.DVD, politicasDVD);
        
        // Revista
        Map<TipoUsuario, Integer> politicasRevista = new HashMap<>();
        politicasRevista.put(TipoUsuario.ESTUDIANTE, 5);
        politicasRevista.put(TipoUsuario.PROFESOR, 10);
        politicasRevista.put(TipoUsuario.INVESTIGADOR, 15);
        politicasRevista.put(TipoUsuario.PUBLICO_GENERAL, 3);
        politicas.put(TipoMaterial.REVISTA, politicasRevista);
        
        // EBook
        Map<TipoUsuario, Integer> politicasEBook = new HashMap<>();
        politicasEBook.put(TipoUsuario.ESTUDIANTE, 7);
        politicasEBook.put(TipoUsuario.PROFESOR, 14);
        politicasEBook.put(TipoUsuario.INVESTIGADOR, 21);
        politicasEBook.put(TipoUsuario.PUBLICO_GENERAL, 5);
        politicas.put(TipoMaterial.EBOOK, politicasEBook);
    }
    
    @Override
    public int calcularDiasPrestamo(TipoMaterial tipoMaterial, TipoUsuario tipoUsuario) {
        if (tipoMaterial == null || tipoUsuario == null) {
            return 0;
        }
        
        Map<TipoUsuario, Integer> politicasPorMaterial = politicas.get(tipoMaterial);
        if (politicasPorMaterial == null) {
            return 7; // Valor por defecto
        }
        
        return politicasPorMaterial.getOrDefault(tipoUsuario, 7);
    }
    
    @Override
    public LocalDateTime obtenerFechaDevolucion(LocalDateTime fechaPrestamo, 
                                                TipoMaterial tipoMaterial, 
                                                TipoUsuario tipoUsuario) {
        
        int dias = calcularDiasPrestamo(tipoMaterial, tipoUsuario);
        return fechaPrestamo.plusDays(dias);
    }
    
    /**
     * Actualiza la política para un tipo de material y usuario
     */
    public void actualizarPolitica(TipoMaterial tipoMaterial, TipoUsuario tipoUsuario, int dias) {
        politicas.computeIfAbsent(tipoMaterial, k -> new HashMap<>()).put(tipoUsuario, dias);
    }
    
    /**
     * Obtiene todas las políticas configuradas
     */
    public Map<TipoMaterial, Map<TipoUsuario, Integer>> obtenerTodasLasPoliticas() {
        Map<TipoMaterial, Map<TipoUsuario, Integer>> copia = new HashMap<>();
        for (Map.Entry<TipoMaterial, Map<TipoUsuario, Integer>> entry : politicas.entrySet()) {
            copia.put(entry.getKey(), new HashMap<>(entry.getValue()));
        }
        return copia;
    }
    
    /**
     * Restablece las políticas a los valores por defecto
     */
    public void restablecerPoliticasPorDefecto() {
        politicas.clear();
        inicializarPoliticas();
    }
    
    /**
     * Verifica si un material puede ser prestado a un usuario (días > 0)
     */
    public boolean puedeSerPrestado(TipoMaterial tipoMaterial, TipoUsuario tipoUsuario) {
        return calcularDiasPrestamo(tipoMaterial, tipoUsuario) > 0;
    }
    
    /**
     * Obtiene la fecha máxima de devolución permitida (considerando renovaciones)
     */
    public LocalDateTime obtenerFechaMaximaDevolucion(LocalDateTime fechaPrestamo,
                                                      TipoMaterial tipoMaterial,
                                                      TipoUsuario tipoUsuario,
                                                      int maxRenovaciones) {
        
        int diasBase = calcularDiasPrestamo(tipoMaterial, tipoUsuario);
        int diasPorRenovacion = diasBase / 2; // Cada renovación da la mitad del tiempo base
        
        int diasTotales = diasBase + (diasPorRenovacion * maxRenovaciones);
        return fechaPrestamo.plusDays(diasTotales);
    }
}