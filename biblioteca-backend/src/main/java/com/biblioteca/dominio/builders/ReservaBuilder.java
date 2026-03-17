package com.biblioteca.dominio.builders;

import java.util.UUID;
import java.util.List;
import java.util.ArrayList;

import com.biblioteca.dominio.builders.interfaces.IBuilderReserva;
import com.biblioteca.dominio.entidades.Reserva;
import com.biblioteca.dominio.entidades.ReservaInterbibliotecaria;
import com.biblioteca.dominio.entidades.ReservaNormal;
import com.biblioteca.dominio.objetosvalor.IdMaterial;
import com.biblioteca.dominio.objetosvalor.IdUsuario;
import com.biblioteca.dominio.objetosvalor.IdTransaccion;

public class ReservaBuilder implements IBuilderReserva {
    private String id;
    private IdUsuario idUsuario;
    private IdMaterial idMaterial;
    
    private String tipoReserva = "NORMAL";
    private String ubicacionBiblioteca;
    private String bibliotecaOrigen;

    public ReservaBuilder() {
        this.id = "RES-" + UUID.randomUUID().toString().substring(0, 6);
    }

    @Override
    public IBuilderReserva paraUsuario(IdUsuario idUsuario) {
        this.idUsuario = idUsuario;
        return this;
    }

    @Override
    public IBuilderReserva deMaterial(IdMaterial idMaterial) {
        this.idMaterial = idMaterial;
        return this;
    }

    @Override
    public IBuilderReserva conId(String id) {
        this.id = id;
        return this;
    }

    @Override
    public IBuilderReserva tipoNormal(String ubicacionBiblioteca) {
        this.tipoReserva = "NORMAL";
        this.ubicacionBiblioteca = ubicacionBiblioteca;
        return this;
    }

    @Override
    public IBuilderReserva tipoInterbibliotecaria(String bibliotecaOrigen) {
        this.tipoReserva = "INTERBIBLIOTECARIA";
        this.bibliotecaOrigen = bibliotecaOrigen;
        return this;
    }

    @Override
    public Reserva construir() {
        validar();

        if (tipoReserva.equals("INTERBIBLIOTECARIA")) {
            return new ReservaInterbibliotecaria(new IdTransaccion(id), idUsuario, idMaterial, bibliotecaOrigen);
        } else {
            return new ReservaNormal(new IdTransaccion(id), idUsuario, idMaterial, ubicacionBiblioteca != null ? ubicacionBiblioteca : "Sala de lectura");
        }
    }

    private void validar() {
        List<String> errores = new ArrayList<>();
        if (idUsuario == null) errores.add("Usuario es obligatorio");
        if (idMaterial == null) errores.add("Material es obligatorio");
        
        if (!errores.isEmpty()) {
            throw new IllegalStateException("Errores de validación: " + String.join(", ", errores));
        }
    }
}
