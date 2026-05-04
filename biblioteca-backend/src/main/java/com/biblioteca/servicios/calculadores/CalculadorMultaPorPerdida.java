package com.biblioteca.servicios.calculadores;

import com.biblioteca.dominio.entidades.Multa;
import com.biblioteca.dominio.entidades.MultaPorPerdida;
import com.biblioteca.dominio.enumeraciones.TipoMulta;
import com.biblioteca.dominio.enumeraciones.TipoUsuario;
import com.biblioteca.dominio.objetosvalor.ContextoMulta;
import com.biblioteca.servicios.interfaces.ICalculadorMulta;

/**
 * Calcula multas por pérdida a partir de datos ya resueltos en el ContextoMulta.
 * No accede a repositorios — valorMaterial y tipoUsuario son resueltos por
 * el servicio de aplicación (DevolucionService) antes de invocar el calculador.
 */
public class CalculadorMultaPorPerdida implements ICalculadorMulta {

    public CalculadorMultaPorPerdida() { }

    @Override
    public Multa calcular(ContextoMulta contexto) {
        double valorMaterial = contexto.getValorMaterial();
        if (valorMaterial <= 0) valorMaterial = 50000.0; // fallback conservador

        double recargo = recargoParaTipoUsuario(contexto.getTipoUsuario());

        return new MultaPorPerdida(
            contexto.getIdPrestamo(),
            contexto.getIdUsuario(),
            valorMaterial,
            recargo
        );
    }

    @Override
    public boolean puedeCalcular(ContextoMulta contexto) {
        return contexto.getTipoMulta() == TipoMulta.POR_PERDIDA;
    }

    private double recargoParaTipoUsuario(TipoUsuario tipo) {
        if (tipo == null) return 0.20;
        switch (tipo) {
            case ESTUDIANTE:      return 0.20;
            case PROFESOR:        return 0.10;
            case INVESTIGADOR:    return 0.00;
            case PUBLICO_GENERAL: return 0.30;
            default:              return 0.20;
        }
    }
}
