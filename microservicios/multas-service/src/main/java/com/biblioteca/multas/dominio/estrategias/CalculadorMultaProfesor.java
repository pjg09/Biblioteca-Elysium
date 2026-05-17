package com.biblioteca.multas.dominio.estrategias;

/**
 * Estrategia de cálculo de multas para PROFESORES.
 * 
 * Tarifas reducidas (privilegio académico):
 * - Retraso: 500 por día (50% descuento)
 * - Pérdida: valor + 10%
 * - Daño LEVE: 20% del valor
 * - Daño MODERADO: 40% del valor
 * - Daño GRAVE: 70% del valor
 * - Daño IRREPARABLE: 100% del valor
 */
public class CalculadorMultaProfesor implements ICalculadorMulta {
    
    private static final double TARIFA_DIARIA_RETRASO = 500.0; // 50% descuento
    private static final double RECARGO_PERDIDA = 0.10;
    
    @Override
    public double calcularMontoRetraso(int diasRetraso) {
        return diasRetraso * TARIFA_DIARIA_RETRASO;
    }
    
    @Override
    public double calcularMontoPerdida(double valorMaterial) {
        return valorMaterial * (1 + RECARGO_PERDIDA);
    }
    
    @Override
    public double calcularMontoDano(double valorMaterial, String gravedad) {
        double factor = parsearGravedad(gravedad);
        return valorMaterial * factor;
    }
    
    @Override
    public String getTipoUsuario() {
        return "PROFESOR";
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
