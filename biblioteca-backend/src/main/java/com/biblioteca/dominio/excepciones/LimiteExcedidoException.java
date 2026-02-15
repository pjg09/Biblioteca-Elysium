package com.biblioteca.dominio.excepciones;

public class LimiteExcedidoException extends BibliotecaException {
    private String idUsuario;
    private int limiteMaximo;
    private int cantidadActual;
    
    public LimiteExcedidoException(String idUsuario, int limiteMaximo, int cantidadActual) {
        super("Usuario " + idUsuario + " excedió el límite de préstamos. Límite: " + limiteMaximo + 
              ", Actual: " + cantidadActual, "LIM-001");
        this.idUsuario = idUsuario;
        this.limiteMaximo = limiteMaximo;
        this.cantidadActual = cantidadActual;
    }
    
    public String getIdUsuario() {
        return idUsuario;
    }
    
    public int getLimiteMaximo() {
        return limiteMaximo;
    }
}