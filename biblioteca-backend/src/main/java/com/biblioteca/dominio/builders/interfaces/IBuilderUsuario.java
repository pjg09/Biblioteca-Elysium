package com.biblioteca.dominio.builders.interfaces;

import com.biblioteca.dominio.entidades.Usuario;
import com.biblioteca.dominio.objetosvalor.IdUsuario;

public interface IBuilderUsuario {
    IBuilderUsuario conId(IdUsuario id);
    IBuilderUsuario conNombre(String nombre);
    IBuilderUsuario conEmail(String email);
    
    IBuilderUsuario esEstudiante(String carrera, int semestre, String universidad);
    IBuilderUsuario esProfesor(String departamento, String universidad, String especialidad);
    IBuilderUsuario esInvestigador(String lineaInvestigacion, String institucion);
    IBuilderUsuario esPublicoGeneral(String direccion, String nombreFiador);
    
    Usuario construir();
}
