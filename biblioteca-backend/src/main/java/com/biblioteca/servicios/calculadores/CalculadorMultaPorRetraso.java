package com.biblioteca.servicios.calculadores;

import com.biblioteca.dominio.entidades.*;
import com.biblioteca.dominio.enumeraciones.TipoMaterial;
import com.biblioteca.dominio.enumeraciones.TipoMulta;
import com.biblioteca.dominio.objetosValor.ContextoMulta;
import com.biblioteca.repositorios.IRepositorio;
import com.biblioteca.servicios.interfaces.ICalculadorMulta;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

public class CalculadorMultaPorRetraso implements ICalculadorMulta {
    private IRepositorio<Prestamo> repoPrestamo;
    private Map<TipoMaterial, Double> tarifasPorTipo;
    
    public CalculadorMultaPorRetraso(IRepositorio<Prestamo> repoPrestamo) {
        this.repoPrestamo = repoPrestamo;
        this.tarifasPorTipo = new HashMap<>();
        inicializarTarifas();
    }
    
    private void inicializarTarifas() {
        tarifasPorTipo.put(TipoMaterial.LIBRO_NORMAL, 1000.0);
        tarifasPorTipo.put(TipoMaterial.LIBRO_BESTSELLER, 2000.0);
        tarifasPorTipo.put(TipoMaterial.LIBRO_REFERENCIA, 1500.0);
        tarifasPorTipo.put(TipoMaterial.DVD, 3000.0);
        tarifasPorTipo.put(TipoMaterial.REVISTA, 500.0);
        tarifasPorTipo.put(TipoMaterial.EBOOK, 0.0); // Digital no tiene multa por retraso
    }
    
    @Override
    public Multa calcular(ContextoMulta contexto) {
        Prestamo prestamo = repoPrestamo.obtenerPorId(contexto.getIdPrestamo());
        if (prestamo == null) {
            throw new IllegalArgumentException("Préstamo no encontrado");
        }
        
        int diasRetraso = calcularDiasRetraso(prestamo, contexto.getFechaActual());
        if (diasRetraso <= 0) {
            return null; // No hay retraso
        }
        
        // Necesitamos el tipo de material - esto debería venir del contexto o buscarlo
        // Por simplicidad, asumimos LIBRO_NORMAL
        double tarifa = obtenerTarifa(TipoMaterial.LIBRO_NORMAL);
        
        MultaPorRetraso multa = new MultaPorRetraso(
            contexto.getIdPrestamo(),
            contexto.getIdUsuario(),
            diasRetraso,
            tarifa
        );
        
        return multa;
    }
    
    @Override
    public boolean puedeCalcular(ContextoMulta contexto) {
        return contexto.getTipoMulta() == TipoMulta.POR_RETRASO;
    }
    
    private int calcularDiasRetraso(Prestamo prestamo, LocalDateTime fechaActual) {
        if (prestamo.getFechaDevolucionReal() != null) {
            return 0; // Ya fue devuelto
        }
        
        if (fechaActual.isBefore(prestamo.getFechaDevolucionEsperada())) {
            return 0; // Aún no vence
        }
        
        return (int) ChronoUnit.DAYS.between(
            prestamo.getFechaDevolucionEsperada(), 
            fechaActual
        );
    }
    
    private double obtenerTarifa(TipoMaterial tipo) {
        return tarifasPorTipo.getOrDefault(tipo, 1000.0);
    }
}