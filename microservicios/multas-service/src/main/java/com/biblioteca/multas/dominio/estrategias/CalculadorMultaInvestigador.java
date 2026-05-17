package com.biblioteca.multas.dominio.estrategias;

/**
 * Estrategia de cálculo de multas para INVESTIGADORES.
 * 
 * Tarifas muy reducidas (privilegio para investigación):
 * - Retraso: 0 (sin multa)
 * - Pérdida: sin recargo (solo valor del material)
 * - Daño: solo costo de reparación según gravedad
 * 
 * Justificación: Los investigadores necesitan acceso extendido a materiales
 * para su investigación académica.
 */
public class CalculadorMultaInvestigador implements ICalculadorMulta {
    
    @Override
    public double calcularMontoRetraso(int diasRetraso) {
        // Investigadores no pagan multa por retraso
        return 0.0;
    }
    
    @Override
    public double calcularMontoPerdida(double valorMaterial) {
        // Solo el valor del material, sin recargo
        return valorMaterial;
    }
    
    @Override
    public double calcularMontoDano(double valorMaterial, String gravedad) {
        // Solo el costo de reparación, según gravedad (sin recargo)
        double factor = parsearGravedad(gravedad);
        return valorMaterial * factor;
    }
    
    @Override
    public String getTipoUsuario() {
        return "INVESTIGADOR";
    }
    
    private double parsearGravedad(String gravedad) {
        if (gravedad == null) {
            return 0.20;
        }
        return switch (gravedad.toUpperCase()) {
            case "LEVE" -> 0.20;
            case "MODERADO" -> 0.40;
            case "GRAVE" -> 0.70;
            case "IRREPARABLE" -> 1.0;
            default -> 0.20;
        };
    }
}
