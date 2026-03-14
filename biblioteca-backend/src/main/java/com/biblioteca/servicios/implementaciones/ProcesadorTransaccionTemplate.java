package com.biblioteca.servicios.implementaciones;

import com.biblioteca.dominio.objetosvalor.IdMaterial;
import com.biblioteca.dominio.objetosvalor.IdUsuario;
import com.biblioteca.dominio.objetosvalor.Resultado;
import com.biblioteca.dominio.objetosvalor.ResultadoValidacion;
import java.time.LocalDateTime;

public abstract class ProcesadorTransaccionTemplate {
    
    public final Resultado procesar(IdUsuario idUsuario, IdMaterial idMaterial) {
        ResultadoValidacion validacion = validarTransaccion(idUsuario, idMaterial);
        if (!validacion.esValido()) {
            return Resultado.Fallido(String.join(", ", validacion.getErrores()));
        }
        
        if (!verificarDisponibilidad(idMaterial)) {
            return Resultado.Fallido("Material no disponible");
        }
        
        LocalDateTime fecha = calcularFecha(idUsuario, idMaterial);
        
        Object transaccion = crearTransaccion(idUsuario, idMaterial, fecha);
        
        Resultado guardado = guardarTransaccion(transaccion);
        
        if (guardado.getExito()) {
            notificar(idUsuario, transaccion);
        }
        
        return guardado;
    }
    
    protected ResultadoValidacion validarTransaccion(IdUsuario idUsuario, IdMaterial idMaterial) {
        return ResultadoValidacion.Valido(); // implementación base comun
    }
    
    protected boolean verificarDisponibilidad(IdMaterial idMaterial) {
        return true; // implementación base comun
    }
    
    protected Resultado guardarTransaccion(Object transaccion) {
        return Resultado.Exitoso("Guardado simulado", transaccion); // implementacion base comun
    }
    
    protected abstract LocalDateTime calcularFecha(IdUsuario idUsuario, IdMaterial idMaterial);
    protected abstract Object crearTransaccion(IdUsuario idUsuario, IdMaterial idMaterial, LocalDateTime fecha);
    protected abstract void notificar(IdUsuario idUsuario, Object transaccion);
}
