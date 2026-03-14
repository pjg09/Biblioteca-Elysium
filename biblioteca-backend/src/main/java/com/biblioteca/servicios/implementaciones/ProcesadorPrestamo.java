package com.biblioteca.servicios.implementaciones;

import com.biblioteca.dominio.builders.PrestamoBuilder;
import com.biblioteca.dominio.entidades.Prestamo;
import com.biblioteca.dominio.objetosvalor.IdMaterial;
import com.biblioteca.dominio.objetosvalor.IdUsuario;
import java.time.LocalDateTime;

public class ProcesadorPrestamo extends ProcesadorTransaccionTemplate {
    
    @Override
    protected LocalDateTime calcularFecha(IdUsuario idUsuario, IdMaterial idMaterial) {
        return LocalDateTime.now().plusDays(15);
    }
    
    @Override
    protected Object crearTransaccion(IdUsuario idUsuario, IdMaterial idMaterial, LocalDateTime fecha) {
        return new PrestamoBuilder()
            .paraUsuario(idUsuario)
            .deMaterial(idMaterial)
            .conVencimiento(fecha)
            .enUbicacion("Sede Central")
            .tipoNormal()
            .construir();
    }
    
    @Override
    protected void notificar(IdUsuario idUsuario, Object transaccion) {
        if (transaccion instanceof Prestamo) {
            Prestamo prestamo = (Prestamo) transaccion;
            System.out.println("Template Method: Préstamo creado. Devolver antes del: " + prestamo.getFechaDevolucionEsperada());
        }
    }
}
