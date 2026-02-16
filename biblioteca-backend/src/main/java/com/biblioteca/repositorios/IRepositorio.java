package com.biblioteca.repositorios;

import java.util.List;

import com.biblioteca.dominio.objetosvalor.Resultado;

public interface IRepositorio<T> {
    Resultado agregar(T entidad);
    Resultado actualizar(T entidad);
    Resultado eliminar(String id);
    T obtenerPorId(String id);
    List<T> obtenerTodos();
    boolean existe(String id);
    long contar();
}