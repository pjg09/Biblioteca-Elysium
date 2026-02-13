package com.biblioteca.modelos;

public class Libro {

    //Atributos del libro
    public String titulo, autor, genero;
    public short anio;
    public boolean disponibilidad;

    //Constructor para cada libro
    public Libro(String titulo, String autor, String genero, short anio, boolean disponibilidad) {

        this.titulo = titulo;
        this.autor = autor;
        this.anio = anio;
        this.genero = genero;
        this.disponibilidad = disponibilidad;

    }

    //Gets para que las demás clases puedan acceder a los atributos, sobre todo el grid
    public String getTitulo() {

        return titulo;

    }

    public String getAutor() {

        return autor;

    }

    public String getGenero() {

        return genero;

    }

    public short getAnio() {

        return anio;

    }

    public boolean getDisponibilidad() {

        return disponibilidad;

    }

    //Sets para asignar los valores al modificarlos desde afuera
    public void setTitulo(String titulo) {

        this.titulo = titulo;

    }

    public void setAutor(String autor) {

        this.autor = autor;

    }

    public void setGenero(String genero) {

        this.genero = genero;

    }

    public void setAnio(short anio) {

        this.anio = anio;

    }

    public void setDisponibilidad(boolean disponibilidad) {

        this.disponibilidad = disponibilidad;

    }

}