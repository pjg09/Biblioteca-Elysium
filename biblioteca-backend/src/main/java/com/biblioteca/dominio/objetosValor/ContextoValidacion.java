package com.biblioteca.dominio.objetosvalor;

import com.biblioteca.dominio.entidades.Material;
import com.biblioteca.dominio.entidades.Usuario;
import com.biblioteca.dominio.enumeraciones.TipoOperacion;

/**
 * Encapsula el contexto de validación
 * Objeto de valor que pasa información a las reglas
 */
public class ContextoValidacion {
    private final String idUsuario;
    private final String idMaterial;
    private final Usuario usuario;
    private final Material material;
    private final TipoOperacion operacion;
    
    public ContextoValidacion(String idUsuario, String idMaterial, 
                              Usuario usuario, Material material, TipoOperacion operacion) {
        this.idUsuario = idUsuario;
        this.idMaterial = idMaterial;
        this.usuario = usuario;
        this.material = material;
        this.operacion = operacion;
    }
    
    public String getIdUsuario() { return idUsuario; }
    public String getIdMaterial() { return idMaterial; }
    public Usuario getUsuario() { return usuario; }
    public Material getMaterial() { return material; }
    public TipoOperacion getOperacion() { return operacion; }
    
    public boolean tieneUsuario() { return usuario != null; }
    public boolean tieneMaterial() { return material != null; }
}
