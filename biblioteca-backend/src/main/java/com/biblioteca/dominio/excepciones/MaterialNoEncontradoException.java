package com.biblioteca.dominio.excepciones;

public class MaterialNoEncontradoException extends BibliotecaException {
    private String idMaterial;
    
    public MaterialNoEncontradoException(String idMaterial) {
        super("Material con ID " + idMaterial + " no encontrado", "MAT-404");
        this.idMaterial = idMaterial;
    }
    
    public String getIdMaterial() {
        return idMaterial;
    }
}