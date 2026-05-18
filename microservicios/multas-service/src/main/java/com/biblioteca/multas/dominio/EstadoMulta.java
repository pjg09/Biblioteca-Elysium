package com.biblioteca.multas.dominio;

/**
 * Estados posibles de una multa según el State Pattern.
 * 
 * GENERADA → PAGADA (transición normal)
 * GENERADA → CONDONADA (condonación)
 * PAGADA → (terminal, sin más transiciones)
 * CONDONADA → (terminal, sin más transiciones)
 */
public enum EstadoMulta {
    GENERADA("Multa generada, pendiente de pago"),
    PAGADA("Multa pagada"),
    CONDONADA("Multa condonada");

    private final String descripcion;

    EstadoMulta(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
