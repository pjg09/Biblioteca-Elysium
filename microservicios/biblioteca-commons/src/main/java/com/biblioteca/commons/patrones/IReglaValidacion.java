package com.biblioteca.commons.patrones;

/**
 * Interfaz Strategy para definir reglas de validación de negocio.
 * 
 * Implementa el patrón Strategy permitiendo diferentes reglas de validación
 * que pueden ser compostas para crear validaciones complejas.
 * 
 * Uso típico:
 * ```java
 * List<IReglaValidacion> reglas = Arrays.asList(
 *     new ReglaUsuarioActivo(),
 *     new ReglaMaterialDisponible(),
 *     new ReglaLimitePrestamos()
 * );
 * 
 * for (IReglaValidacion regla : reglas) {
 *     if (!regla.validar(contexto)) {
 *         return Resultado.fallo(regla.obtenerMensajeError());
 *     }
 * }
 * ```
 */
public interface IReglaValidacion {
    
    /**
     * Valida si se cumple la regla.
     * 
     * @param contexto Objeto con los datos necesarios para la validación
     * @return true si la validación pasa, false en caso contrario
     */
    boolean validar(Object contexto);
    
    /**
     * Retorna el mensaje de error si la validación falla.
     * Se invoca después de que validar() retorna false.
     */
    String obtenerMensajeError();
    
    /**
     * Retorna el nombre/identificador de la regla.
     */
    String getNombre();
}
