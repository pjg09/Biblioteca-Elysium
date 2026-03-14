package com.biblioteca.servicios;

import com.biblioteca.dominio.objetosvalor.Evaluacion;
import com.biblioteca.dominio.objetosvalor.IdMaterial;
import com.biblioteca.dominio.objetosvalor.IdUsuario;
import com.biblioteca.dominio.objetosvalor.Resultado;
import com.biblioteca.servicios.interfaces.IDevolucionService;
import com.biblioteca.servicios.interfaces.IPrestamoService;
import com.biblioteca.servicios.interfaces.IRenovacionService;
import com.biblioteca.servicios.interfaces.IReservaService;
import java.util.ArrayList;

public class BibliotecaFacade {
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
    
    public Resultado procesarSolicitudMaterial(String idUsuarioStr, String idMaterialStr) {
        try {
            IdUsuario idUsuario = new IdUsuario(idUsuarioStr);
            IdMaterial idMaterial = new IdMaterial(idMaterialStr);

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
    
    public Resultado devolverMaterialSinInspeccion(String idPrestamo) {
        Evaluacion evaluacion = new Evaluacion(true, new ArrayList<>());
        return devolucionService.registrarDevolucion(idPrestamo, evaluacion);
    }
}
