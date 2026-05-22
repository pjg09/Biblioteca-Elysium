package com.biblioteca.circulacion.dominio.builders;

import com.biblioteca.commons.objetosvalor.Resultado;
import com.biblioteca.commons.patrones.IBuilder;
import com.biblioteca.circulacion.dominio.agregados.Prestamo;

import java.time.LocalDateTime;

/**
 * Builder para Prestamo que implementa validaciones de dominio.
 * Garantiza que un Prestamo se cree en un estado consistente.
 * 
 * Principios SOLID aplicados:
 * - Single Responsibility: Solo responsable de validar y construir Prestamo
 * - Open/Closed: Fácil extender con nuevas validaciones sin modificar
 */
public class PrestamoBuilder implements IBuilder<Prestamo> {
    private String id;
    private String idUsuario;
    private String idMaterial;
    private LocalDateTime fechaDevolucionEsperada;
    private String tipoPrestamo = "NORMAL";
    private String sede;

    public PrestamoBuilder conId(String id) {
        this.id = id;
        return this;
    }

    public PrestamoBuilder conIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
        return this;
    }

    public PrestamoBuilder conIdMaterial(String idMaterial) {
        this.idMaterial = idMaterial;
        return this;
    }

    public PrestamoBuilder conFechaDevolucionEsperada(LocalDateTime fechaDevolucionEsperada) {
        this.fechaDevolucionEsperada = fechaDevolucionEsperada;
        return this;
    }

    public PrestamoBuilder conTipoPrestamo(String tipoPrestamo) {
        this.tipoPrestamo = tipoPrestamo != null ? tipoPrestamo : "NORMAL";
        return this;
    }

    public PrestamoBuilder conSede(String sede) {
        this.sede = sede;
        return this;
    }

    @Override
    public Resultado<Prestamo> construir() {
        // Validaciones
        if (id == null || id.isBlank()) {
            return Resultado.fallo("El ID del préstamo es obligatorio");
        }

        if (idUsuario == null || idUsuario.isBlank()) {
            return Resultado.fallo("El ID del usuario es obligatorio");
        }

        if (idMaterial == null || idMaterial.isBlank()) {
            return Resultado.fallo("El ID del material es obligatorio");
        }

        if (fechaDevolucionEsperada == null) {
            return Resultado.fallo("La fecha de devolución esperada es obligatoria");
        }

        // La fecha de devolución debe ser en el futuro
        if (fechaDevolucionEsperada.isBefore(LocalDateTime.now())) {
            return Resultado.fallo("La fecha de devolución esperada no puede estar en el pasado");
        }

        if (!tipoPrestamoValido(tipoPrestamo)) {
            return Resultado.fallo("Tipo de préstamo inválido: " + tipoPrestamo);
        }

        if (sede == null || sede.isBlank()) {
            return Resultado.fallo("La sede es obligatoria");
        }

        // Si todas las validaciones pasan, construir el Prestamo
        Prestamo prestamo = Prestamo.crear(id, idUsuario, idMaterial, fechaDevolucionEsperada, tipoPrestamo, sede);
        return Resultado.exitoso(prestamo);
    }

    private boolean tipoPrestamoValido(String tipo) {
        return tipo.equals("NORMAL") || tipo.equals("INTERBIBLIOTECARIO");
    }
}
