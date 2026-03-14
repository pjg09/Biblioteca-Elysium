package com.biblioteca.dominio.entidades;

import com.biblioteca.dominio.enumeraciones.TipoUsuario;
import com.biblioteca.dominio.objetosvalor.IdUsuario;

public class Profesor extends Usuario {
    private String departamento;
    private String universidad;
    private String especialidad;
    
    public Profesor(IdUsuario id, String nombre, String email, 
                    String departamento, String universidad, String especialidad) {
        super(id, nombre, email, TipoUsuario.PROFESOR);
        this.departamento = departamento;
        this.universidad = universidad;
        this.especialidad = especialidad;
    }
    
    public String getDepartamento() {
        return departamento;
    }
    
    public String getUniversidad() {
        return universidad;
    }
    
    public String getEspecialidad() {
        return especialidad;
    }
}