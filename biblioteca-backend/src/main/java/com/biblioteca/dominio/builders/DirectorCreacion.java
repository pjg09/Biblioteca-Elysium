package com.biblioteca.dominio.builders;

import com.biblioteca.dominio.builders.interfaces.IBuilderMaterial;
import com.biblioteca.dominio.builders.interfaces.IBuilderUsuario;

/**
 * Director para el patrón Builder.
 * Encapsula las configuraciones más comunes de creación de objetos para no repetir código.
 */
public class DirectorCreacion {

    // =========================================
    // USUARIOS
    // =========================================

    public void construirEstudianteBasico(IBuilderUsuario builder, String nombre, String email, String carrera) {
        builder.conNombre(nombre)
               .conEmail(email)
               .esEstudiante(carrera, 1, "Universidad Nacional");
    }

    public void construirProfesorInvestigador(IBuilderUsuario builder, String nombre, String email, String departamento, String lineaInvestigacion) {
        // Ejemplo de una configuración específica
        builder.conNombre(nombre)
               .conEmail(email)
               .esProfesor(departamento, "Universidad Nacional", lineaInvestigacion);
    }

    public void construirUsuarioGeneral(IBuilderUsuario builder, String nombre, String email) {
        builder.conNombre(nombre)
               .conEmail(email)
               .esPublicoGeneral("Sin Registro", "Sin Fiador");
    }

    // =========================================
    // MATERIALES
    // =========================================

    public void construirLibroReferencia(IBuilderMaterial builder, String titulo, String autor, String isbn) {
        builder.conTitulo(titulo)
               .deAutor(autor)
               .esLibro()
               .conISBN(isbn)
               .esReferencia();
    }

    public void construirRevistaMensual(IBuilderMaterial builder, String titulo, String issn, int edicion) {
        builder.conTitulo(titulo)
               .deAutor("Varios Autores")
               .esRevista()
               .conIssn(issn)
               .numeroEdicion(edicion);
    }
}
