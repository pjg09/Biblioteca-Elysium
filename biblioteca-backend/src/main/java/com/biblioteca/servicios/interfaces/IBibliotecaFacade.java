package com.biblioteca.servicios.interfaces;

import com.biblioteca.dominio.entidades.Multa;
import com.biblioteca.dominio.objetosvalor.ContextoMulta;
import com.biblioteca.dominio.objetosvalor.Evaluacion;
import com.biblioteca.dominio.objetosvalor.Resultado;

public interface IBibliotecaFacade {
    Resultado procesarSolicitudMaterial(String idUsuarioStr, String idMaterialStr);
    Resultado registrarPrestamo(String idUsuario, String idMaterial, String tipoPrestamo);
    Resultado devolverMaterial(String idPrestamo, Evaluacion evaluacion);
    Resultado devolverMaterialSinInspeccion(String idPrestamo);
    Resultado renovarPrestamo(String idPrestamo);
    Resultado crearReserva(String idUsuario, String idMaterial, String tipoReserva);
    Resultado cancelarReserva(String idReserva);
    void limpiarReservasExpiradas();
    Multa calcularMulta(ContextoMulta contexto);
}
