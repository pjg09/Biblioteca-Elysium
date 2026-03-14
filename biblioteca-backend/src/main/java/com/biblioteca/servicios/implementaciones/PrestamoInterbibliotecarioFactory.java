package com.biblioteca.servicios.implementaciones;

import com.biblioteca.dominio.entidades.Prestamo;
import com.biblioteca.dominio.entidades.PrestamoInterbibliotecario;
import com.biblioteca.dominio.builders.PrestamoBuilder;
import com.biblioteca.servicios.interfaces.IPrestamoFactory;
import com.biblioteca.dominio.factories.ContextoCreacionPrestamo;

public class PrestamoInterbibliotecarioFactory implements IPrestamoFactory {
    
    private final String bibliotecaOrigenPredeterminada;
    private final String bibliotecaDestinoPredeterminada;
    private final double costoTransferenciaPredeterminado;

    public PrestamoInterbibliotecarioFactory(String bibliotecaOrigen, String bibliotecaDestino, double costoTransferencia) {
        this.bibliotecaOrigenPredeterminada = bibliotecaOrigen;
        this.bibliotecaDestinoPredeterminada = bibliotecaDestino;
        this.costoTransferenciaPredeterminado = costoTransferencia;
    }

    public PrestamoInterbibliotecarioFactory() {
        this.bibliotecaOrigenPredeterminada = "Biblioteca Central";
        this.bibliotecaDestinoPredeterminada = "Biblioteca Secundaria";
        this.costoTransferenciaPredeterminado = 5000.0;
    }

    @Override
    public Prestamo crearPrestamo(ContextoCreacionPrestamo contexto) {
        return new PrestamoBuilder()
            .paraUsuario(contexto.getUsuario().getId())
            .deMaterial(contexto.getMaterial().getId())
            .conVencimiento(contexto.getFechaDevolucion())
            .tipoInterbibliotecario(bibliotecaOrigenPredeterminada, bibliotecaDestinoPredeterminada, costoTransferenciaPredeterminado)
            .construir();
    }
}
