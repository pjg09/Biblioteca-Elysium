package com.biblioteca.dominio.builders;

import java.util.ArrayList;
import java.util.List;

import com.biblioteca.dominio.builders.interfaces.IBuilderMulta;
import com.biblioteca.dominio.entidades.Multa;
import com.biblioteca.dominio.entidades.MultaPorDano;
import com.biblioteca.dominio.entidades.MultaPorPerdida;
import com.biblioteca.dominio.entidades.MultaPorRetraso;
import com.biblioteca.dominio.objetosvalor.Dano;

public class MultaBuilder implements IBuilderMulta {

    private String idPrestamo;
    private String idUsuario;
    
    private String tipoMulta = "RETRASO";
    
    private int diasRetraso;
    private double tarifaDiaria;
    
    private double costoMaterial;
    private double recargoPorPerdida;
    
    private List<Dano> danos;

    @Override
    public IBuilderMulta paraPrestamo(String idPrestamo) {
        this.idPrestamo = idPrestamo;
        return this;
    }

    @Override
    public IBuilderMulta aUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
        return this;
    }

    @Override
    public IBuilderMulta porRetraso(int diasRetraso, double tarifaDiaria) {
        this.tipoMulta = "RETRASO";
        this.diasRetraso = diasRetraso;
        this.tarifaDiaria = tarifaDiaria;
        return this;
    }

    @Override
    public IBuilderMulta porPerdida(double costoMaterial, double recargo) {
        this.tipoMulta = "PERDIDA";
        this.costoMaterial = costoMaterial;
        this.recargoPorPerdida = recargo;
        return this;
    }

    @Override
    public IBuilderMulta porDano(List<Dano> danos) {
        this.tipoMulta = "DANO";
        this.danos = danos;
        return this;
    }

    @Override
    public Multa construir() {
        validar();
        
        switch (tipoMulta) {
            case "RETRASO":
                return new MultaPorRetraso(idPrestamo, idUsuario, diasRetraso, tarifaDiaria);
            case "PERDIDA":
                return new MultaPorPerdida(idPrestamo, idUsuario, costoMaterial, recargoPorPerdida);
            case "DANO":
                return new MultaPorDano(idPrestamo, idUsuario, danos != null ? danos : new ArrayList<>());
            default:
                throw new IllegalStateException("Tipo de multa no soportado");
        }
    }
    
    private void validar() {
        List<String> errores = new ArrayList<>();
        if (idPrestamo == null) errores.add("Préstamo es obligatorio");
        if (idUsuario == null) errores.add("Usuario es obligatorio");
        
        if (!errores.isEmpty()) {
            throw new IllegalStateException("Errores de validación: " + String.join(", ", errores));
        }
    }
}
