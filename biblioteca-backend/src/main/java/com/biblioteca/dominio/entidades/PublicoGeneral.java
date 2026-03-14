package com.biblioteca.dominio.entidades;

import com.biblioteca.dominio.enumeraciones.TipoUsuario;
import com.biblioteca.dominio.objetosvalor.IdUsuario;

public class PublicoGeneral extends Usuario {
    private String direccion;
    private String nombreFiador;
    
    public PublicoGeneral(IdUsuario id, String nombre, String email, String direccion, String nombreFiador) {
        super(id, nombre, email, TipoUsuario.PUBLICO_GENERAL);
        this.direccion = direccion;
        this.nombreFiador = nombreFiador;
    }
    
    public String getDireccion() {
        return direccion;
    }
    
    public String getFiador() {
        return nombreFiador;
    }
}