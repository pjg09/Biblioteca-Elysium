package com.biblioteca.usuarios.dominio.builders;

import com.biblioteca.commons.objetosvalor.Resultado;
import com.biblioteca.commons.patrones.IBuilder;
import com.biblioteca.usuarios.dominio.Usuario;

/**
 * Builder para Usuario que implementa validaciones de dominio.
 * Garantiza que un Usuario se cree en un estado consistente.
 * 
 * Principios SOLID aplicados:
 * - Single Responsibility: Solo responsable de construir Usuario válido
 * - Open/Closed: Fácil extender con nuevas validaciones sin modificar
 */
public class UsuarioBuilder implements IBuilder<Usuario> {
    private String id;
    private String nombre;
    private String email;
    private String tipoUsuario;
    private String estadoUsuario = "ACTIVO";
    private int limiteMaximoPrestamos;

    public UsuarioBuilder conId(String id) {
        this.id = id;
        return this;
    }

    public UsuarioBuilder conNombre(String nombre) {
        this.nombre = nombre;
        return this;
    }

    public UsuarioBuilder conEmail(String email) {
        this.email = email;
        return this;
    }

    public UsuarioBuilder conTipoUsuario(String tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
        return this;
    }

    public UsuarioBuilder conEstado(String estadoUsuario) {
        this.estadoUsuario = estadoUsuario;
        return this;
    }

    public UsuarioBuilder conLimiteMaximoPrestamos(int limite) {
        this.limiteMaximoPrestamos = limite;
        return this;
    }

    @Override
    public Resultado<Usuario> construir() {
        // Validaciones
        if (id == null || id.isBlank()) {
            return Resultado.fallo("El ID del usuario es obligatorio");
        }

        if (nombre == null || nombre.isBlank()) {
            return Resultado.fallo("El nombre del usuario es obligatorio");
        }

        if (nombre.length() > 100) {
            return Resultado.fallo("El nombre no puede exceder 100 caracteres");
        }

        if (email == null || email.isBlank()) {
            return Resultado.fallo("El email es obligatorio");
        }

        if (!esEmailValido(email)) {
            return Resultado.fallo("El email no tiene un formato válido");
        }

        if (tipoUsuario == null || tipoUsuario.isBlank()) {
            return Resultado.fallo("El tipo de usuario es obligatorio");
        }

        if (!tipoUsuarioValido(tipoUsuario)) {
            return Resultado.fallo("Tipo de usuario inválido: " + tipoUsuario);
        }

        if (limiteMaximoPrestamos <= 0) {
            return Resultado.fallo("El límite de préstamos debe ser mayor a 0");
        }

        if (limiteMaximoPrestamos > 50) {
            return Resultado.fallo("El límite de préstamos no puede exceder 50");
        }

        // Si todas las validaciones pasan, construir el Usuario
        Usuario usuario = new Usuario(id, nombre, email, tipoUsuario, estadoUsuario, limiteMaximoPrestamos);
        return Resultado.exitoso(usuario);
    }

    private boolean esEmailValido(String email) {
        // Validación simple de email
        return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    private boolean tipoUsuarioValido(String tipo) {
        // Validar contra los tipos conocidos
        return tipo.equals("ESTUDIANTE") || 
               tipo.equals("PROFESOR") || 
               tipo.equals("INVESTIGADOR") ||
               tipo.equals("BIBLIOTECARIO");
    }
}
