package com.biblioteca.dominio.estados;

import com.biblioteca.dominio.entidades.Prestamo;
import com.biblioteca.dominio.enumeraciones.EstadoTransaccion;
import com.biblioteca.dominio.objetosvalor.Resultado;
import com.biblioteca.dominio.objetosvalor.ResultadoValidacion;
import java.util.Collections;

public class PrestamoCompletadoState implements IEstadoPrestamo {
    @Override
    public ResultadoValidacion puedeRenovarse(Prestamo prestamo) {
        return ResultadoValidacion.Invalido(
            Collections.singletonList("Préstamo ya completado, no puede renovarse")
        );
    }
    
    @Override
    public Resultado devolver(Prestamo prestamo) {
        return Resultado.Fallido("Préstamo ya fue devuelto");
    }
    
    @Override
    public EstadoTransaccion obtenerEstado() {
        return EstadoTransaccion.COMPLETADA;
    }
}
