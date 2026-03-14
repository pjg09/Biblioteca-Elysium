package com.biblioteca.servicios.implementaciones;

import com.biblioteca.dominio.entidades.Prestamo;
import com.biblioteca.dominio.entidades.PrestamoNormal;
import com.biblioteca.dominio.builders.PrestamoBuilder;
import com.biblioteca.servicios.interfaces.IPrestamoFactory;
import com.biblioteca.dominio.factories.ContextoCreacionPrestamo;

public class PrestamoNormalFactory implements IPrestamoFactory {
    
    private final String ubicacionPredeterminada;

    public PrestamoNormalFactory(String ubicacionPredeterminada) {
        this.ubicacionPredeterminada = ubicacionPredeterminada;
    }

    public PrestamoNormalFactory() {
        this.ubicacionPredeterminada = "Biblioteca Central";
    }

    @Override
    public Prestamo crearPrestamo(ContextoCreacionPrestamo contexto) {
        return new PrestamoBuilder()
            .paraUsuario(contexto.getUsuario().getId())
            .deMaterial(contexto.getMaterial().getId())
            .conVencimiento(contexto.getFechaDevolucion())
            .enUbicacion(ubicacionPredeterminada)
            .tipoNormal()
            .construir();
    }
}
