package com.biblioteca.materiales.aplicacion.dto;

public class DisponibilidadDTO {

    private String materialId;
    private String titulo;
    private boolean disponible;
    private String estado;

    public DisponibilidadDTO() {}

    public DisponibilidadDTO(String materialId, String titulo, boolean disponible, String estado) {
        this.materialId = materialId;
        this.titulo = titulo;
        this.disponible = disponible;
        this.estado = estado;
    }

    public String getMaterialId() {
        return materialId;
    }

    public void setMaterialId(String materialId) {
        this.materialId = materialId;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
