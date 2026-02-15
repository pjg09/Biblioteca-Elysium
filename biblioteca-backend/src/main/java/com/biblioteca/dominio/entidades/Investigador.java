package com.biblioteca.dominio.entidades;

import com.biblioteca.dominio.enumeraciones.TipoUsuario;

public class Investigador extends Usuario {
    private String lineaInvestigacion;
    private String institucion;
    
    public Investigador(String id, String nombre, String email, 
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