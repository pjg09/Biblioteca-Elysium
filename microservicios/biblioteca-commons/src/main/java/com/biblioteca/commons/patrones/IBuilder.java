package com.biblioteca.commons.patrones;

/**
 * Interfaz para implementar el patrón Builder.
 * 
 * Garantiza que todas las entidades construidas a través de un Builder
 * puedan ser validadas y construidas de manera uniforme.
 * 
 * Ejemplo de implementación:
 * <pre>
 * public class UsuarioBuilder implements IBuilder<Usuario> {
 *     private String nombre;
 *     private String email;
 *     // ... más campos
 *     
 *     public UsuarioBuilder conNombre(String nombre) {
 *         this.nombre = nombre;
 *         return this;
 *     }
 *     
 *     @Override
 *     public Resultado<Usuario> construir() {
 *         // Validaciones
 *         if (nombre == null || nombre.isBlank()) {
 *             return Resultado.fallo("El nombre es obligatorio");
 *         }
 *         // Si todo está bien, construcción
 *         return Resultado.exitoso(new Usuario(nombre, email, ...));
 *     }
 * }
 * </pre>
 */
public interface IBuilder<T> {
    /**
     * Construye la entidad con las validaciones necesarias.
     * @return Un Resultado<T> con la entidad construida o un mensaje de error
     */
    com.biblioteca.commons.objetosvalor.Resultado<T> construir();
}
