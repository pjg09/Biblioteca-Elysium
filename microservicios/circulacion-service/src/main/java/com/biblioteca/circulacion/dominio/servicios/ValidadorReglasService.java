package com.biblioteca.circulacion.dominio.servicios;

import com.biblioteca.circulacion.dominio.objetosvalor.ContextoValidacionPrestamo;
import com.biblioteca.commons.patrones.IReglaValidacion;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Servicio que ejecuta múltiples reglas de validación compostas.
 * 
 * Implementa el patrón Strategy Composition, permitiendo aplicar
 * múltiples reglas en secuencia y detenerse en la primera que falla.
 * 
 * Uso:
 * ```java
 * ValidadorReglasService validador = new ValidadorReglasService();
 * validador.agregar(new ReglaUsuarioActivo())
 *          .agregar(new ReglaMaterialDisponible())
 *          .agregar(new ReglaLimitePrestamos())
 *          .agregar(new ReglaUsuarioNoMoroso());
 * 
 * Resultado<Void> resultado = validador.validar(contexto);
 * if (resultado.esError()) {
 *     return ResultadoOperacion.fallido(resultado.getMensajeError());
 * }
 * ```
 */
@Service
public class ValidadorReglasService {
    
    private final List<IReglaValidacion> reglas = new ArrayList<>();
    
    /**
     * Agrega una regla al validador.
     * 
     * @param regla Regla a agregar
     * @return this para encadenamiento fluido
     */
    public ValidadorReglasService agregar(IReglaValidacion regla) {
        this.reglas.add(regla);
        return this;
    }
    
    /**
     * Ejecuta todas las reglas registradas en secuencia.
     * Se detiene en la primera que falla.
     * 
     * @param contexto Contexto a validar (ContextoValidacionPrestamo)
     * @return true si todas las reglas pasan, false si alguna falla
     */
    public boolean validar(Object contexto) {
        for (IReglaValidacion regla : reglas) {
            if (!regla.validar(contexto)) {
                // Primera regla que falla, detener validación
                return false;
            }
        }
        return true;
    }
    
    /**
     * Obtiene el mensaje de error de la primera regla que falló.
     * Debe llamarse solo después de que validar() retorna false.
     * 
     * @param contexto Contexto que fue validado
     * @return Mensaje de error de la primera regla fallida, o mensaje genérico
     */
    public String obtenerMensajeError(Object contexto) {
        for (IReglaValidacion regla : reglas) {
            if (!regla.validar(contexto)) {
                return regla.obtenerMensajeError();
            }
        }
        return "Validación desconocida fallida";
    }
    
    /**
     * Limpia las reglas registradas.
     */
    public void limpiar() {
        this.reglas.clear();
    }
    
    /**
     * Retorna la lista de reglas registradas.
     */
    public List<IReglaValidacion> obtenerReglas() {
        return new ArrayList<>(this.reglas);
    }
}
