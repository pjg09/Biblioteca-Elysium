package com.biblioteca.dominio.objetosvalor;

public class IdMaterial {
    private final String valor;

    public IdMaterial(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException("ID de material no puede ser vacío");
        }
        if (!valor.matches("MAT-\\d{6}")) {
            throw new IllegalArgumentException("Formato de ID de material inválido. Esperado MAT-XXXXXX");
        }
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IdMaterial)) return false;
        IdMaterial that = (IdMaterial) o;
        return valor.equals(that.valor);
    }

    @Override
    public int hashCode() {
        return valor.hashCode();
    }

    @Override
    public String toString() {
        return valor;
    }
}
