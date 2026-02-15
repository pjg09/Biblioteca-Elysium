package com.biblioteca.dominio.entidades;

import com.biblioteca.dominio.enumeraciones.TipoUsuario;

public class Profesor extends Usuario {
    private String departamento;
    private String universidad;
    private String especialidad;
    
    public Profesor(String id, String nombre, String email, 
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