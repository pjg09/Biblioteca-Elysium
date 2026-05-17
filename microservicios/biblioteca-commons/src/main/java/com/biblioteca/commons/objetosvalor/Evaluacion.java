package com.biblioteca.commons.objetosvalor;

/**
 * Value Object que encapsula la evaluación de una devolución.
 * Contiene el estado del material al devolver y cualquier observación.
 */
public class Evaluacion {
    public enum EstadoMaterial {
        EXCELENTE, BUENO, DESGASTADO, DANADO
    }

    private final EstadoMaterial estado;
    private final String observaciones;

    public Evaluacion(EstadoMaterial estado, String observaciones) {
        if (estado == null) {
            throw new IllegalArgumentException("El estado del material no puede ser nulo");
        }
        this.estado = estado;
        this.observaciones = observaciones != null ? observaciones : "";
    }

    public EstadoMaterial getEstado() {
        return estado;
    }

    public String getObservaciones() {
        return observaciones;
    }

    @Override
    public String toString() {
        return "Evaluacion{" +
                "estado=" + estado +
                ", observaciones='" + observaciones + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Evaluacion)) return false;

        Evaluacion that = (Evaluacion) o;

        if (estado != that.estado) return false;
        return observaciones.equals(that.observaciones);
    }

    @Override
    public int hashCode() {
        int result = estado.hashCode();
        result = 31 * result + observaciones.hashCode();
        return result;
    }
}
