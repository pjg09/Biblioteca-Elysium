package com.biblioteca.commons.objetosvalor;

import com.biblioteca.commons.enumeraciones.TipoUsuario;

/**
 * Value Object que encapsula el contexto necesario para calcular una multa.
 * Contiene todos los datos que un CalculadorMulta necesita sin tener que
 * inyectar servicios dentro de la lógica de cálculo.
 * 
 * Principio SOLID: Esto es un parámetro Object que encapsula múltiples datos,
 * evitando parámetros largos (Clean Code - parámetro objeto).
 */
public class ContextoMulta {
    private final int diasRetraso;
    private final TipoUsuario tipoUsuario;
    private final double valorMaterial;

    public ContextoMulta(int diasRetraso, TipoUsuario tipoUsuario, double valorMaterial) {
        if (diasRetraso < 0) {
            throw new IllegalArgumentException("Los días de retraso no pueden ser negativos");
        }
        if (tipoUsuario == null) {
            throw new IllegalArgumentException("El tipo de usuario no puede ser nulo");
        }
        if (valorMaterial < 0) {
            throw new IllegalArgumentException("El valor del material no puede ser negativo");
        }

        this.diasRetraso = diasRetraso;
        this.tipoUsuario = tipoUsuario;
        this.valorMaterial = valorMaterial;
    }

    public int getDiasRetraso() {
        return diasRetraso;
    }

    public TipoUsuario getTipoUsuario() {
        return tipoUsuario;
    }

    public double getValorMaterial() {
        return valorMaterial;
    }

    @Override
    public String toString() {
        return "ContextoMulta{" +
                "diasRetraso=" + diasRetraso +
                ", tipoUsuario=" + tipoUsuario +
                ", valorMaterial=" + valorMaterial +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ContextoMulta)) return false;

        ContextoMulta that = (ContextoMulta) o;

        if (diasRetraso != that.diasRetraso) return false;
        if (Double.compare(that.valorMaterial, valorMaterial) != 0) return false;
        return tipoUsuario == that.tipoUsuario;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = diasRetraso;
        result = 31 * result + tipoUsuario.hashCode();
        temp = Double.doubleToLongBits(valorMaterial);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
