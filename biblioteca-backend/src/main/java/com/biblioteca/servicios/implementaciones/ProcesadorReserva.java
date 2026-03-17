package com.biblioteca.servicios.implementaciones;

import com.biblioteca.dominio.entidades.Reserva;
import com.biblioteca.dominio.entidades.ReservaNormal;
import com.biblioteca.dominio.objetosvalor.IdMaterial;
import com.biblioteca.dominio.objetosvalor.IdUsuario;
import com.biblioteca.dominio.objetosvalor.IdTransaccion;
import java.time.LocalDateTime;
import java.util.UUID;

public class ProcesadorReserva extends ProcesadorTransaccionTemplate {
    
    @Override
    protected LocalDateTime calcularFecha(IdUsuario idUsuario, IdMaterial idMaterial) {
        return LocalDateTime.now().plusHours(48); // 48 horas para recoger el material reservado
    }
    
    @Override
    protected Object crearTransaccion(IdUsuario idUsuario, IdMaterial idMaterial, LocalDateTime fecha) {
        return new ReservaNormal(
            new IdTransaccion("RES-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase()),
            idUsuario, 
            idMaterial,
            "Sede Central"
        ); 
    }
    
    @Override
    protected void notificar(IdUsuario idUsuario, Object transaccion) {
        if (transaccion instanceof Reserva) {
            Reserva reserva = (Reserva) transaccion;
            System.out.println("Template Method: Reserva creada. Recoger antes de 48h.");
        }
    }
}
