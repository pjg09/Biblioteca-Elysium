package com.biblioteca.multas.dominio.estrategias;

/**
 * Interfaz que define la estrategia para calcular multas.
 * 
 * Implementa el patrón Strategy permitiendo diferentes formas de calcular
 * multas según el tipo de usuario.
 * 
 * Ventajas sobre switch:
 * - Fácil agregar nuevos tipos de usuario
 * - Cada estrategia es testeable independientemente
 * - Cumple con Open/Closed Principle
 * - Eliminación de condicionales complejos
 */
public interface ICalculadorMulta {
    
    /**
     * Calcula el monto de multa por retraso en la devolución.
     * 
     * @param diasRetraso Número de días de retraso
     * @return Monto de la multa en pesos/moneda
     */
    double calcularMontoRetraso(int diasRetraso);
    
    /**
     * Calcula el monto de multa por pérdida del material.
     * 
     * @param valorMaterial Valor del material perdido
     * @return Monto de la multa (valor + recargo según tipo usuario)
     */
    double calcularMontoPerdida(double valorMaterial);
    
    /**
     * Calcula el monto de multa por daño del material.
     * 
     * @param valorMaterial Valor del material dañado
     * @param gravedad Nivel de daño (LEVE, MODERADO, GRAVE, IRREPARABLE)
     * @return Monto de la multa según gravedad
     */
    double calcularMontoDano(double valorMaterial, String gravedad);
    
    /**
     * Retorna el tipo de usuario para esta estrategia.
     */
    String getTipoUsuario();
}
