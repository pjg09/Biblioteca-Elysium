package com.biblioteca.dominio.entidades;

import com.biblioteca.dominio.enumeraciones.TipoUsuario;

public class Estudiante extends Usuario {
    private String carrera;
    private int semestre;
    private String universidad;
    
    public Estudiante(String id, String nombre, String email, 
                      String carrera, int semestre, String universidad) {
        super(id, nombre, email, TipoUsuario.ESTUDIANTE);
        this.carrera = carrera;
        this.semestre = semestre;
        this.universidad = universidad;
    }
    
    public String getCarrera() {
        return carrera;
    }
    
    public int getSemestre() {
        return semestre;
    }
    
    public String getUniversidad() {
        return universidad;
    }
}