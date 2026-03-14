package com.biblioteca.eventos;

public class ObservadorEstadisticas implements IObservadorPrestamo {
    @Override
    public void actualizar(EventoPrestamo evento) {
        System.out.println("📊 Estadística: Nuevo evento registrado tipo [" + evento.getTipo() + "] para el préstamo " + evento.getIdPrestamo());
        // Actualizar base de datos de estadísticas o data warehouse
    }
}
