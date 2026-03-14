package com.biblioteca.dominio.objetosvalor;

public class IdUsuario {
    private final String valor;

    public IdUsuario(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException("ID de usuario no puede ser vacío");
        }
        if (!valor.matches("USR-\\d{6}")) {
            throw new IllegalArgumentException("Formato de ID de usuario inválido. Esperado USR-XXXXXX");
        }
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IdUsuario)) return false;
        IdUsuario that = (IdUsuario) o;
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
