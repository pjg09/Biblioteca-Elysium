package com.biblioteca.servicios.interfaces;

import com.biblioteca.dominio.entidades.Prestamo;
import com.biblioteca.dominio.factories.ContextoCreacionPrestamo;

public interface IPrestamoFactory {
    Prestamo crearPrestamo(ContextoCreacionPrestamo contexto);
}
