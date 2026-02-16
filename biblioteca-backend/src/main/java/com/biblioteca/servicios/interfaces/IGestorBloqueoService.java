package com.biblioteca.servicios.interfaces;

import com.biblioteca.dominio.objetosvalor.Resultado;
import com.biblioteca.dominio.objetosvalor.ResultadoValidacion;

/**
 * Servicio para gestionar bloqueos de usuarios por multas o pérdidas.
 * 
 * Respeta SRP: Solo se encarga de la lógica de bloqueos.
 * Respeta DIP: Define contrato, no implementación.
 */
public interface IGestorBloqueoService {
    
    /**
     * Verifica si un usuario debe ser bloqueado según sus multas pendientes.
     * 
     * Criterios de bloqueo:
     * - Tiene multas pendientes por encima del umbral permitido
     * - Tiene materiales reportados como perdidos
     * - Tiene préstamos vencidos por mucho tiempo
     * 
     * @param idUsuario ID del usuario a verificar
     * @return ResultadoValidacion indicando si debe bloquearse y por qué
     */
    ResultadoValidacion verificarSiDebeBloquear(String idUsuario);
    
    /**
     * Bloquea a un usuario especificando el motivo.
     * 
     * @param idUsuario ID del usuario a bloquear
     * @param motivo Razón del bloqueo
     * @return Resultado indicando éxito o fallo de la operación
     */
    Resultado bloquearUsuario(String idUsuario, String motivo);
    
    /**
     * Desbloquea a un usuario (cuando paga multas o resuelve situación).
     * 
     * @param idUsuario ID del usuario a desbloquear
     * @return Resultado indicando éxito o fallo de la operación
     */
    Resultado desbloquearUsuario(String idUsuario);
    
    /**
     * Verifica automáticamente y bloquea si es necesario.
     * Útil para ejecutar después de generar multas o detectar pérdidas.
     * 
     * @param idUsuario ID del usuario
     * @return Resultado indicando si se bloqueó o no
     */
    Resultado verificarYBloquearSiNecesario(String idUsuario);
}