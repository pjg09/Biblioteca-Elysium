package com.biblioteca.dominio.objetosValor;

import java.util.ArrayList;
import java.util.List;

public class ResultadoValidacion {
    private boolean esValido;
    private List<String> errores;
    private List<String> advertencias;
    
    private ResultadoValidacion(boolean esValido, List<String> errores, List<String> advertencias) {
        this.esValido = esValido;
        this.errores = errores != null ? errores : new ArrayList<>();
        this.advertencias = advertencias != null ? advertencias : new ArrayList<>();
    }
    
    public boolean esValido() {
        return esValido;
    }
    
    public List<String> getErrores() {
        return new ArrayList<>(errores);
    }
    
    public List<String> getAdvertencias() {
        return new ArrayList<>(advertencias);
    }
    
    public ResultadoValidacion combinar(ResultadoValidacion otro) {
        boolean nuevoEsValido = this.esValido && otro.esValido;
        List<String> nuevosErrores = new ArrayList<>(this.errores);
        nuevosErrores.addAll(otro.errores);
        List<String> nuevasAdvertencias = new ArrayList<>(this.advertencias);
        nuevasAdvertencias.addAll(otro.advertencias);
        
        return new ResultadoValidacion(nuevoEsValido, nuevosErrores, nuevasAdvertencias);
    }
    
    public static ResultadoValidacion Valido() {
        return new ResultadoValidacion(true, new ArrayList<>(), new ArrayList<>());
    }
    
    public static ResultadoValidacion Invalido(List<String> errores) {
        return new ResultadoValidacion(false, errores, new ArrayList<>());
    }
    
    public static ResultadoValidacion Invalido(String error) {
        List<String> errores = new ArrayList<>();
        errores.add(error);
        return new ResultadoValidacion(false, errores, new ArrayList<>());
    }
    
    @Override
    public String toString() {
        return "ResultadoValidacion{" +
                "esValido=" + esValido +
                ", errores=" + errores +
                ", advertencias=" + advertencias +
                '}';
    }
}