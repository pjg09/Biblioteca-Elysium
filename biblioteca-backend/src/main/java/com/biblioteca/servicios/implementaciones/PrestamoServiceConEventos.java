package com.biblioteca.servicios.implementaciones;

import com.biblioteca.dominio.entidades.Prestamo;
import com.biblioteca.dominio.objetosvalor.IdMaterial;
import com.biblioteca.dominio.objetosvalor.IdUsuario;
import com.biblioteca.dominio.objetosvalor.Resultado;
import com.biblioteca.eventos.EventoPrestamo;
import com.biblioteca.eventos.IObservadorPrestamo;
import com.biblioteca.servicios.interfaces.IPrestamoService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PrestamoServiceConEventos implements IPrestamoService {
    private final IPrestamoService prestamoService;
    private final List<IObservadorPrestamo> observadores;
    
    public PrestamoServiceConEventos(IPrestamoService prestamoService) {
        this.prestamoService = prestamoService;
        this.observadores = new ArrayList<>();
    }
    
    public void registrarObservador(IObservadorPrestamo observador) {
        observadores.add(observador);
    }
    
    @Override
    public Resultado registrarPrestamo(IdUsuario idUsuario, IdMaterial idMaterial, String tipoPrestamo) {
        Resultado resultado = prestamoService.registrarPrestamo(idUsuario, idMaterial, tipoPrestamo);
        
        if (resultado.getExito() && resultado.getData() instanceof Prestamo) {
            Prestamo prestamo = (Prestamo) resultado.getData();
            // Notificar a todos los observadores
            EventoPrestamo evento = new EventoPrestamo(
                prestamo.getId(),
                idUsuario.getValor(),
                idMaterial.getValor(),
                LocalDateTime.now(),
                EventoPrestamo.TipoEvento.PRESTAMO_CREADO
            );
            
            notificarObservadores(evento);
        }
        
        return resultado;
    }

    @Override
    public List<Prestamo> obtenerPrestamosActivos(IdUsuario idUsuario) {
        return prestamoService.obtenerPrestamosActivos(idUsuario);
    }

    @Override
    public Prestamo obtenerPrestamoPorId(String idPrestamo) {
        return prestamoService.obtenerPrestamoPorId(idPrestamo);
    }
    
    private void notificarObservadores(EventoPrestamo evento) {
        for (IObservadorPrestamo observador : observadores) {
            observador.actualizar(evento);
        }
    }
}
