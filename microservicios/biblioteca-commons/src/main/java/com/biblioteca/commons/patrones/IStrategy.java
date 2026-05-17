package com.biblioteca.commons.patrones;

/**
 * Interfaz para implementar el patrón Strategy.
 * 
 * El patrón Strategy encapsula un conjunto de algoritmos intercambiables,
 * permitiendo que el algoritmo varíe independientemente del cliente.
 * 
 * Ejemplo de implementación para cálculo de multas:
 * <pre>
 * public interface ICalculadorMulta {
 *     double calcular(ContextoMulta contexto);
 * }
 * 
 * public class CalculadorMultaEstudiante implements ICalculadorMulta {
 *     public double calcular(ContextoMulta contexto) {
 *         // Lógica específica para estudiantes
 *         return contexto.getDiasRetraso() * 0.5; // Tarifa reducida
 *     }
 * }
 * 
 * public class CalculadorMultaProfesor implements ICalculadorMulta {
 *     public double calcular(ContextoMulta contexto) {
 *         // Lógica específica para profesores
 *         return contexto.getDiasRetraso() * 1.0; // Tarifa normal
 *     }
 * }
 * 
 * public class CalculadorMultaService {
 *     private Map<TipoUsuario, ICalculadorMulta> calculadores = new HashMap<>();
 *     
 *     public CalculadorMultaService() {
 *         calculadores.put(ESTUDIANTE, new CalculadorMultaEstudiante());
 *         calculadores.put(PROFESOR, new CalculadorMultaProfesor());
 *     }
 *     
 *     public double calcularMulta(ContextoMulta contexto) {
 *         ICalculadorMulta calculador = calculadores.get(contexto.getTipoUsuario());
 *         return calculador.calcular(contexto);
 *     }
 * }
 * </pre>
 * 
 * Beneficios:
 * - Nuevas reglas/estrategias sin modificar código existente
 * - Algoritmos pueden ser probados independientemente
 * - Principio Open/Closed (OCP) cumplido
 * - Depende del tipo de usuario, no de una larga cadena if/else
 */
public interface IStrategy<T, R> {
    /**
     * Ejecuta la estrategia con el parámetro dado.
     * @param parametro el parámetro para ejecutar la estrategia
     * @return el resultado de la estrategia
     */
    R ejecutar(T parametro);
}
