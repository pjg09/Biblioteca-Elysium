package com.example.Biblioteca;

import java.time.LocalDate;

public class Prestamo {

    //Atributos de un prestamo
    public Libro libro;
    public String persona;
    public LocalDate fecha;

    //Constructor para crear cada prestamo
    public Prestamo(Libro libro, String persona, LocalDate fecha) {

        this.libro = libro;
        this.persona = persona;
        this.fecha = fecha;

    }

    //Gets para acceder a la información de los prestamos
    public Libro getLibro() {

        return libro;

    }

    public String getPersona() {

        return persona;

    }

    public LocalDate getFecha() {

        return fecha;

    }

}