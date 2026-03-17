package com.biblioteca.dominio.builders;

import java.util.UUID;

import com.biblioteca.dominio.entidades.Estudiante;
import com.biblioteca.dominio.entidades.Investigador;
import com.biblioteca.dominio.entidades.Profesor;
import com.biblioteca.dominio.entidades.PublicoGeneral;
import com.biblioteca.dominio.entidades.Usuario;
import com.biblioteca.dominio.enumeraciones.TipoUsuario;
import com.biblioteca.dominio.objetosvalor.IdUsuario;

import com.biblioteca.dominio.builders.interfaces.IBuilderUsuario;

public class UsuarioBuilder implements IBuilderUsuario {
    private IdUsuario id;
    private String nombre;
    private String email;
    private TipoUsuario tipo;
    
    // Específicos de Estudiante
    private String carrera;
    private int semestre;
    private String universidad;
    
    // Específicos de Profesor
    private String departamento;
    private String especialidad;
    
    // Específicos de Investigador
    private String lineaInvestigacion;
    private String institucion;
    
    // Específicos de Público General
    private String direccion;
    private String nombreFiador;
    
    public IBuilderUsuario conId(IdUsuario id) {
        this.id = id;
        return this;
    }

    public IBuilderUsuario conNombre(String nombre) {
        this.nombre = nombre;
        return this;
    }
    
    public IBuilderUsuario conEmail(String email) {
        this.email = email;
        return this;
    }
    
    // =========================================
    // TIPOS
    // =========================================
    
    public IBuilderUsuario esEstudiante(String carrera, int semestre, String universidad) {
        this.tipo = TipoUsuario.ESTUDIANTE;
        this.carrera = carrera;
        this.semestre = semestre;
        this.universidad = universidad;
        return this;
    }
    
    public IBuilderUsuario esProfesor(String departamento, String universidad, String especialidad) {
        this.tipo = TipoUsuario.PROFESOR;
        this.departamento = departamento;
        this.universidad = universidad;
        this.especialidad = especialidad;
        return this;
    }
    
    public IBuilderUsuario esInvestigador(String lineaInvestigacion, String institucion) {
        this.tipo = TipoUsuario.INVESTIGADOR;
        this.lineaInvestigacion = lineaInvestigacion;
        this.institucion = institucion;
        return this;
    }
    
    public IBuilderUsuario esPublicoGeneral(String direccion, String nombreFiador) {
        this.tipo = TipoUsuario.PUBLICO_GENERAL;
        this.direccion = direccion;
        this.nombreFiador = nombreFiador;
        return this;
    }
    
    public Usuario construir() {
        validar();
        
        IdUsuario targetId = id != null ? id : generarId();
        
        switch (tipo) {
            case ESTUDIANTE:
                return new Estudiante(
                    targetId,
                    nombre,
                    email,
                    carrera,
                    semestre,
                    universidad
                );
            case PROFESOR:
                return new Profesor(
                    targetId,
                    nombre,
                    email,
                    departamento,
                    universidad,
                    especialidad
                );
            case INVESTIGADOR:
                return new Investigador(
                    targetId,
                    nombre,
                    email,
                    lineaInvestigacion,
                    institucion
                );
            case PUBLICO_GENERAL:
                return new PublicoGeneral(
                    targetId,
                    nombre,
                    email,
                    direccion,
                    nombreFiador
                );
            default:
                throw new IllegalStateException("Tipo de usuario no especificado");
        }
    }
    
    private void validar() {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalStateException("Nombre es obligatorio");
        }
        if (email == null || !email.matches(".+@.+\\..+")) {
            throw new IllegalStateException("Email inválido");
        }
        if (tipo == null) {
            throw new IllegalStateException("Tipo de usuario es obligatorio");
        }
    }
    
    private IdUsuario generarId() {
        String num = String.format("%06d", (int)(Math.random() * 1000000));
        return new IdUsuario("USR-" + num);
    }
}
