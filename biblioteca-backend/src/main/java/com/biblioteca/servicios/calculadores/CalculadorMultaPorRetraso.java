package com.biblioteca.servicios.calculadores;

import com.biblioteca.dominio.entidades.Multa;
import com.biblioteca.dominio.entidades.MultaPorRetraso;
import com.biblioteca.dominio.enumeraciones.TipoMaterial;
import com.biblioteca.dominio.enumeraciones.TipoMulta;
import com.biblioteca.dominio.enumeraciones.TipoUsuario;
import com.biblioteca.dominio.objetosvalor.ContextoMulta;
import com.biblioteca.servicios.interfaces.ICalculadorMulta;

import java.util.HashMap;
import java.util.Map;

/**
 * Calcula multas por retraso a partir de datos ya resueltos en el ContextoMulta.
 * No accede a repositorios — la resolución de diasRetraso y tipoUsuario es
 * responsabilidad del servicio de aplicación (DevolucionService).
 */
public class CalculadorMultaPorRetraso implements ICalculadorMulta {
    private final Map<TipoMaterial, Double> tarifasPorTipo;
    private final Map<TipoUsuario, Double> multiplicadorPorUsuario;

    public CalculadorMultaPorRetraso() {
        this.tarifasPorTipo = new HashMap<>();
        this.multiplicadorPorUsuario = new HashMap<>();
        inicializarTarifas();
        inicializarMultiplicadores();
    }

    private void inicializarTarifas() {
        tarifasPorTipo.put(TipoMaterial.LIBRO_NORMAL,     1000.0);
        tarifasPorTipo.put(TipoMaterial.LIBRO_BESTSELLER, 2000.0);
        tarifasPorTipo.put(TipoMaterial.LIBRO_REFERENCIA, 1500.0);
        tarifasPorTipo.put(TipoMaterial.DVD,              3000.0);
        tarifasPorTipo.put(TipoMaterial.REVISTA,           500.0);
        tarifasPorTipo.put(TipoMaterial.EBOOK,               0.0);
    }

    private void inicializarMultiplicadores() {
        multiplicadorPorUsuario.put(TipoUsuario.ESTUDIANTE,      1.0);
        multiplicadorPorUsuario.put(TipoUsuario.PROFESOR,        0.5);
        multiplicadorPorUsuario.put(TipoUsuario.INVESTIGADOR,    0.0);
        multiplicadorPorUsuario.put(TipoUsuario.PUBLICO_GENERAL, 1.5);
    }

    @Override
    public Multa calcular(ContextoMulta contexto) {
        int diasRetraso = contexto.getDiasRetraso();
        if (diasRetraso <= 0) return null;

        double tarifaBase = tarifasPorTipo.getOrDefault(TipoMaterial.LIBRO_NORMAL, 1000.0);
        double multiplicador = 1.0;

        if (contexto.getTipoUsuario() != null) {
            multiplicador = multiplicadorPorUsuario.getOrDefault(contexto.getTipoUsuario(), 1.0);
        }

        return new MultaPorRetraso(
            contexto.getIdPrestamo(),
            contexto.getIdUsuario(),
            diasRetraso,
            tarifaBase * multiplicador
        );
    }

    @Override
    public boolean puedeCalcular(ContextoMulta contexto) {
        return contexto.getTipoMulta() == TipoMulta.POR_RETRASO;
    }
}
