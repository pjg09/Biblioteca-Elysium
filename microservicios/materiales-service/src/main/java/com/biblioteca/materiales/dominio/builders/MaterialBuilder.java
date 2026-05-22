package com.biblioteca.materiales.dominio.builders;

import com.biblioteca.commons.objetosvalor.Resultado;
import com.biblioteca.commons.patrones.IBuilder;
import com.biblioteca.materiales.dominio.agregados.Material;

/**
 * Builder para Material que implementa validaciones de dominio.
 * Garantiza que un Material se cree en un estado consistente.
 * 
 * Principios SOLID aplicados:
 * - Single Responsibility: Solo responsable de construir Material válido
 * - Liskov Substitution: Diferentes tipos de materiales (Libro, DVD, etc) respetan el contrato
 */
public class MaterialBuilder implements IBuilder<Material> {
    private String id;
    private String titulo;
    private String autor;
    private String tipo;
    private String estado = "DISPONIBLE";
    private double precio;

    public MaterialBuilder conId(String id) {
        this.id = id;
        return this;
    }

    public MaterialBuilder conTitulo(String titulo) {
        this.titulo = titulo;
        return this;
    }

    public MaterialBuilder conAutor(String autor) {
        this.autor = autor;
        return this;
    }

    public MaterialBuilder conTipo(String tipo) {
        this.tipo = tipo;
        return this;
    }

    public MaterialBuilder conEstado(String estado) {
        this.estado = estado;
        return this;
    }

    public MaterialBuilder conPrecio(double precio) {
        this.precio = precio;
        return this;
    }

    @Override
    public Resultado<Material> construir() {
        // Validaciones
        if (id == null || id.isBlank()) {
            return Resultado.fallo("El ID del material es obligatorio");
        }

        if (titulo == null || titulo.isBlank()) {
            return Resultado.fallo("El título del material es obligatorio");
        }

        if (titulo.length() > 200) {
            return Resultado.fallo("El título no puede exceder 200 caracteres");
        }

        if (autor == null || autor.isBlank()) {
            return Resultado.fallo("El autor del material es obligatorio");
        }

        if (autor.length() > 100) {
            return Resultado.fallo("El autor no puede exceder 100 caracteres");
        }

        if (tipo == null || tipo.isBlank()) {
            return Resultado.fallo("El tipo de material es obligatorio");
        }

        if (!tipoMaterialValido(tipo)) {
            return Resultado.fallo("Tipo de material inválido: " + tipo);
        }

        if (!estadoValido(estado)) {
            return Resultado.fallo("Estado inválido: " + estado);
        }

        if (precio < 0) {
            return Resultado.fallo("El precio no puede ser negativo");
        }

        if (precio > 1000000) {
            return Resultado.fallo("El precio excede el límite máximo permitido");
        }

        // Si todas las validaciones pasan, construir el Material
        Material material = new Material(id, titulo, autor, tipo, estado, precio);
        return Resultado.exitoso(material);
    }

    private boolean tipoMaterialValido(String tipo) {
        return tipo.equals("LIBRO_NORMAL") ||
               tipo.equals("BESTSELLER") ||
               tipo.equals("REFERENCIA") ||
               tipo.equals("DVD") ||
               tipo.equals("REVISTA") ||
               tipo.equals("EBOOK");
    }

    private boolean estadoValido(String estado) {
        return estado.equals("DISPONIBLE") ||
               estado.equals("PRESTADO") ||
               estado.equals("RESERVADO") ||
               estado.equals("EN_REPARACION") ||
               estado.equals("PERDIDO");
    }
}
