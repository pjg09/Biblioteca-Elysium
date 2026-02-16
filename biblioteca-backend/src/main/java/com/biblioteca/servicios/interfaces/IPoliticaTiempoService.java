package com.biblioteca.servicios.interfaces;

import java.time.LocalDateTime;

import com.biblioteca.dominio.enumeraciones.TipoMaterial;
import com.biblioteca.dominio.enumeraciones.TipoUsuario;

/**
 * Servicio para calcular tiempos de préstamo según tipo de material y usuario.
 * 
 * Respeta SRP: Solo calcula políticas de tiempo.
 * Respeta OCP: Las políticas son configurables sin modificar código.
 */
public interface IPoliticaTiempoService {
    
    /**
     * Calcula cuántos días de préstamo corresponden según material y usuario.
     */
    int calcularDiasPrestamo(TipoMaterial tipoMaterial, TipoUsuario tipoUsuario);
    
    /**
     * Calcula la fecha de devolución esperada.
     */
    LocalDateTime obtenerFechaDevolucion(
            LocalDateTime fechaPrestamo, 
            TipoMaterial tipoMaterial, 
            TipoUsuario tipoUsuario
    );
}