package com.biblioteca.servicios;

import com.biblioteca.dominio.objetosvalor.Evaluacion;
import com.biblioteca.dominio.objetosvalor.Resultado;
import com.biblioteca.servicios.interfaces.IDevolucionService;
import com.biblioteca.servicios.interfaces.IPrestamoService;
import com.biblioteca.servicios.interfaces.IRenovacionService;
import com.biblioteca.servicios.interfaces.IReservaService;
import com.biblioteca.servicios.interfaces.IBibliotecaFacade;
import com.biblioteca.servicios.implementaciones.ReservaService;

import java.util.ArrayList;

public class BibliotecaFacade implements IBibliotecaFacade {
    private final IPrestamoService prestamoService;
    private final IDevolucionService devolucionService;
    private final IReservaService reservaService;
    private final IRenovacionService renovacionService;

    public BibliotecaFacade(
            IPrestamoService prestamoService,
            IDevolucionService devolucionService,
            IReservaService reservaService,
            IRenovacionService renovacionService) {
        this.prestamoService = prestamoService;
        this.devolucionService = devolucionService;
        this.reservaService = reservaService;
        this.renovacionService = renovacionService;
    }

    @Override
    public Resultado procesarSolicitudMaterial(String idUsuarioStr, String idMaterialStr) {
        try {
            String idUsuario = (idUsuarioStr);
            String idMaterial = (idMaterialStr);

            Resultado resultadoPrestamo = prestamoService.registrarPrestamo(idUsuario, idMaterial, "normal");

            if (resultadoPrestamo.getExito()) {
                return resultadoPrestamo;
            }

            Resultado resultadoReserva = reservaService.crearReserva(idUsuario, idMaterial, "normal");

            if (resultadoReserva.getExito()) {
                return Resultado.Exitoso("Material no disponible. Reserva creada automáticamente", resultadoReserva.getData());
            }

            return Resultado.Fallido("No se pudo procesar la solicitud: " + resultadoPrestamo.getMensaje() + " | " + resultadoReserva.getMensaje());
        } catch (IllegalArgumentException e) {
            return Resultado.Fallido("Error de formato: " + e.getMessage());
        }
    }

    @Override
    public Resultado registrarPrestamo(String idUsuario, String idMaterial, String tipoPrestamo) {
        try {
            return prestamoService.registrarPrestamo(
                    (idUsuario), (idMaterial), tipoPrestamo);
        } catch (IllegalArgumentException e) {
            return Resultado.Fallido("Error de formato: " + e.getMessage());
        }
    }

    @Override
    public Resultado devolverMaterial(String idPrestamo, Evaluacion evaluacion) {
        return devolucionService.registrarDevolucion(idPrestamo, evaluacion);
    }

    @Override
    public Resultado devolverMaterialSinInspeccion(String idPrestamo) {
        Evaluacion evaluacion = new Evaluacion(true, new ArrayList<>());
        return devolucionService.registrarDevolucion(idPrestamo, evaluacion);
    }

    @Override
    public Resultado renovarPrestamo(String idPrestamo) {
        return renovacionService.renovarPrestamo(idPrestamo);
    }

    @Override
    public Resultado crearReserva(String idUsuario, String idMaterial, String tipoReserva) {
        try {
            return reservaService.crearReserva(
                    (idUsuario), (idMaterial), tipoReserva);
        } catch (IllegalArgumentException e) {
            return Resultado.Fallido("Error de formato: " + e.getMessage());
        }
    }

    @Override
    public Resultado cancelarReserva(String idReserva) {
        return reservaService.cancelarReserva(idReserva);
    }

    @Override
    public void limpiarReservasExpiradas() {
        if (reservaService instanceof ReservaService) {
            ((ReservaService) reservaService).limpiarReservasExpiradas();
        }
    }
}

