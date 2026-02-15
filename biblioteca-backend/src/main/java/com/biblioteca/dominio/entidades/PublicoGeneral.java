package com.biblioteca.dominio.entidades;

import com.biblioteca.dominio.enumeraciones.TipoUsuario;

public class PublicoGeneral extends Usuario {
    private String direccion;
    private String nombreFiador;
    
    public PublicoGeneral(String id, String nombre, String email, String direccion, String nombreFiador) {
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