package com.biblioteca.commons.objetosvalor;

/**
 * Value Object universal para encapsular resultados de operaciones.
 * Puede usarse en cualquier microservicio para devolver operaciones exitosas o fallidas.
 * 
 * Ejemplo:
 * - Resultado.exitoso(usuario) cuando la operación tuvo éxito
 * - Resultado.fallo("El usuario no existe") cuando algo falló
 */
public class Resultado<T> {
    private final boolean esExitoso;
    private final T valor;
    private final String mensajeError;

    private Resultado(boolean esExitoso, T valor, String mensajeError) {
        this.esExitoso = esExitoso;
        this.valor = valor;
        this.mensajeError = mensajeError;
    }

    public static <T> Resultado<T> exitoso(T valor) {
        return new Resultado<>(true, valor, null);
    }

    public static <T> Resultado<T> fallo(String mensajeError) {
        return new Resultado<>(false, null, mensajeError);
    }

    public boolean esExitoso() {
        return esExitoso;
    }

    public boolean esError() {
        return !esExitoso;
    }

    public T getValor() {
        if (!esExitoso) {
            throw new IllegalStateException("No hay valor en un resultado fallido. Error: " + mensajeError);
        }
        return valor;
    }

    public String getMensajeError() {
        return mensajeError;
    }

    /**
     * Transforma el valor de este resultado si es exitoso.
     */
    public <U> Resultado<U> mapear(java.util.function.Function<T, U> transformador) {
        if (esExitoso) {
            return Resultado.exitoso(transformador.apply(valor));
        }
        return Resultado.fallo(mensajeError);
    }

    /**
     * Encadena resultados: si este es exitoso, ejecuta la función que devuelve otro Resultado.
     */
    public <U> Resultado<U> flatMapear(java.util.function.Function<T, Resultado<U>> transformador) {
        if (esExitoso) {
            return transformador.apply(valor);
        }
        return Resultado.fallo(mensajeError);
    }

    @Override
    public String toString() {
        return "Resultado{" +
                "esExitoso=" + esExitoso +
                ", valor=" + valor +
                ", mensajeError='" + mensajeError + '\'' +
                '}';
    }
}
