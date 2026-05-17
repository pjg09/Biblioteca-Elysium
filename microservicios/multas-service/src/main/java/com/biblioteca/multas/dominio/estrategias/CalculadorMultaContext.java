package com.biblioteca.multas.dominio.estrategias;

/**
 * Context para seleccionar la estrategia de cálculo de multas.
 * 
 * Implementa el patrón Strategy permitiendo elegir dinámicamente
 * la estrategia correcta según el tipo de usuario.
 */
public class CalculadorMultaContext {
    
    private ICalculadorMulta calculador;
    
    /**
     * Establece la estrategia de cálculo según el tipo de usuario.
     * 
     * @param tipoUsuario Tipo de usuario (ESTUDIANTE, PROFESOR, INVESTIGADOR, PUBLICO_GENERAL)
     * @return true si la estrategia fue establecida, false si el tipo es desconocido
     */
    public boolean establecerEstrategia(String tipoUsuario) {
        if (tipoUsuario == null) {
            this.calculador = new CalculadorMultaEstudiante(); // Default
            return true;
        }
        
        this.calculador = switch (tipoUsuario.toUpperCase()) {
            case "ESTUDIANTE" -> new CalculadorMultaEstudiante();
            case "PROFESOR" -> new CalculadorMultaProfesor();
            case "INVESTIGADOR" -> new CalculadorMultaInvestigador();
            case "PUBLICO_GENERAL" -> new CalculadorMultaPublico();
            default -> null;
        };
        
        return this.calculador != null;
    }
    
    /**
     * Calcula la multa por retraso usando la estrategia establecida.
     */
    public double calcularMontoRetraso(int diasRetraso) {
        if (calculador == null) {
            throw new IllegalStateException("Estrategia de cálculo no inicializada. Llama a establecerEstrategia() primero.");
        }
        return calculador.calcularMontoRetraso(diasRetraso);
    }
    
    /**
     * Calcula la multa por pérdida usando la estrategia establecida.
     */
    public double calcularMontoPerdida(double valorMaterial) {
        if (calculador == null) {
            throw new IllegalStateException("Estrategia de cálculo no inicializada. Llama a establecerEstrategia() primero.");
        }
        return calculador.calcularMontoPerdida(valorMaterial);
    }
    
    /**
     * Calcula la multa por daño usando la estrategia establecida.
     */
    public double calcularMontoDano(double valorMaterial, String gravedad) {
        if (calculador == null) {
            throw new IllegalStateException("Estrategia de cálculo no inicializada. Llama a establecerEstrategia() primero.");
        }
        return calculador.calcularMontoDano(valorMaterial, gravedad);
    }
    
    /**
     * Retorna la estrategia actual.
     */
    public ICalculadorMulta getCalculador() {
        return calculador;
    }
}
