package com.biblioteca.dominio.entidades;

import com.biblioteca.dominio.enumeraciones.TipoUsuario;
import com.biblioteca.dominio.objetosvalor.IdUsuario;

public class Investigador extends Usuario {
    private String lineaInvestigacion;
    private String institucion;
    
    public Investigador(IdUsuario id, String nombre, String email, 
                        String lineaInvestigacion, String institucion) {
        super(id, nombre, email, TipoUsuario.INVESTIGADOR);
        this.lineaInvestigacion = lineaInvestigacion;
        this.institucion = institucion;
    }
    
    public String getLineaInvestigacion() {
        return lineaInvestigacion;
    }
    
    public String getInstitucion() {
        return institucion;
    }
}