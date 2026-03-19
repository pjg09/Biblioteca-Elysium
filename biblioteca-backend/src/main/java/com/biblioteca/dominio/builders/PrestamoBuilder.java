package com.biblioteca.dominio.builders;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;

import com.biblioteca.dominio.entidades.Prestamo;
import com.biblioteca.dominio.entidades.PrestamoInterbibliotecario;
import com.biblioteca.dominio.entidades.PrestamoNormal;
import com.biblioteca.dominio.objetosvalor.IdUsuario;
import com.biblioteca.dominio.objetosvalor.IdMaterial;
import com.biblioteca.dominio.objetosvalor.IdTransaccion;

import com.biblioteca.dominio.builders.interfaces.IBuilderPrestamo;

public class PrestamoBuilder implements IBuilderPrestamo {
    // Campos obligatorios
    private IdUsuario idUsuario;
    private IdMaterial idMaterial;
    
    // Campos opcionales con valores por defecto
    private String id = generarIdAutomatico();
    private LocalDateTime fechaDevolucion = LocalDateTime.now().plusDays(15);
    private String tipoPrestamo = "normal";
    
    // Campos específicos por tipo
    private String ubicacionBiblioteca = "Sede Central";
    private String bibliotecaOrigen;
    private String bibliotecaDestino;
    private double costoTransferencia = 0.0;
    // MÉTODOS OBLIGATORIOS
    
    public IBuilderPrestamo paraUsuario(IdUsuario idUsuario) {
        this.idUsuario = idUsuario;
        return this;
    }
    
    public IBuilderPrestamo deMaterial(IdMaterial idMaterial) {
        this.idMaterial = idMaterial;
        return this;
    }
    // MÉTODOS OPCIONALES
    
    public IBuilderPrestamo conId(String id) {
        this.id = id;
        return this;
    }
    
    public IBuilderPrestamo conVencimiento(LocalDateTime fechaDevolucion) {
        this.fechaDevolucion = fechaDevolucion;
        return this;
    }
    
    public IBuilderPrestamo porDias(int dias) {
        this.fechaDevolucion = LocalDateTime.now().plusDays(dias);
        return this;
    }
    
    public IBuilderPrestamo enUbicacion(String ubicacion) {
        this.ubicacionBiblioteca = ubicacion;
        return this;
    }
    // CONFIGURACIÓN POR TIPO
    
    public IBuilderPrestamo tipoNormal() {
        this.tipoPrestamo = "normal";
        return this;
    }
    
    public IBuilderPrestamo tipoInterbibliotecario(String origen, String destino, double costo) {
        this.tipoPrestamo = "interbibliotecario";
        this.bibliotecaOrigen = origen;
        this.bibliotecaDestino = destino;
        this.costoTransferencia = costo;
        return this;
    }
    // BUILD
    
    public Prestamo construir() {
        // Validación
        validar();
        
        // Construcción según tipo
        if (tipoPrestamo.equalsIgnoreCase("normal")) {
            return construirPrestamoNormal();
        } else if (tipoPrestamo.equalsIgnoreCase("interbibliotecario")) {
            return construirPrestamoInterbibliotecario();
        } else {
            throw new IllegalStateException("Tipo de préstamo no soportado: " + tipoPrestamo);
        }
    }
    
    private void validar() {
        List<String> errores = new ArrayList<>();
        
        if (idUsuario == null) {
            errores.add("Usuario es obligatorio");
        }
        if (idMaterial == null) {
            errores.add("Material es obligatorio");
        }
        if (fechaDevolucion == null) {
            errores.add("Fecha de devolución es obligatoria");
        }
        if (fechaDevolucion != null && fechaDevolucion.isBefore(LocalDateTime.now())) {
            errores.add("Fecha de devolución no puede estar en el pasado");
        }
        
        if (tipoPrestamo.equalsIgnoreCase("interbibliotecario")) {
            if (bibliotecaOrigen == null || bibliotecaOrigen.trim().isEmpty()) {
                errores.add("Biblioteca origen es obligatoria para préstamo interbibliotecario");
            }
            if (bibliotecaDestino == null || bibliotecaDestino.trim().isEmpty()) {
                errores.add("Biblioteca destino es obligatoria para préstamo interbibliotecario");
            }
        }
        
        if (!errores.isEmpty()) {
            throw new IllegalStateException("Errores de validación: " + String.join(", ", errores));
        }
    }
    
    private PrestamoNormal construirPrestamoNormal() {
        return new PrestamoNormal(
            new IdTransaccion(id),
            idUsuario,
            idMaterial,
            fechaDevolucion,
            ubicacionBiblioteca
        );
    }
    
    private PrestamoInterbibliotecario construirPrestamoInterbibliotecario() {
        return new PrestamoInterbibliotecario(
            new IdTransaccion(id),
            idUsuario,
            idMaterial,
            fechaDevolucion,
            bibliotecaOrigen,
            bibliotecaDestino,
            costoTransferencia
        );
    }
    
    private static String generarIdAutomatico() {
        return "PRES-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}

