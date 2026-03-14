package com.biblioteca.dominio.estados;

import com.biblioteca.dominio.entidades.Prestamo;
import com.biblioteca.dominio.enumeraciones.EstadoTransaccion;
import com.biblioteca.dominio.objetosvalor.Resultado;
import com.biblioteca.dominio.objetosvalor.ResultadoValidacion;
import java.time.LocalDateTime;
import java.util.Collections;

public class PrestamoActivoState implements IEstadoPrestamo {
    @Override
    public ResultadoValidacion puedeRenovarse(Prestamo prestamo) {
        if (prestamo.getRenovacionesUsadas() >= 2) {
            return ResultadoValidacion.Invalido(
                Collections.singletonList("Máximo de renovaciones alcanzado")
            );
        }
        
        if (prestamo.getFechaDevolucionEsperada().isBefore(LocalDateTime.now())) {
            return ResultadoValidacion.Invalido(
                Collections.singletonList("Préstamo vencido, no puede renovarse")
            );
        }
        
        return ResultadoValidacion.Valido();
    }
    
    @Override
    public Resultado devolver(Prestamo prestamo) {
        prestamo.setEstado(EstadoTransaccion.COMPLETADA);
        prestamo.cambiarEstado(new PrestamoCompletadoState());
        return Resultado.Exitoso("Préstamo devuelto", prestamo);
    }
    
    @Override
    public EstadoTransaccion obtenerEstado() {
        return EstadoTransaccion.ACTIVA;
    }
}
