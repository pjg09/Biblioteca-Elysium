package com.biblioteca.servicios.implementaciones;

import com.biblioteca.dominio.objetosvalor.Resultado;
import com.biblioteca.servicios.interfaces.INotificacionService;

public class NotificacionConReintentosDecorator implements INotificacionService {
    private final INotificacionService servicioBase;
    private final int maximoReintentos;
    
    public NotificacionConReintentosDecorator(INotificacionService servicioBase, int maximoReintentos) {
        this.servicioBase = servicioBase;
        this.maximoReintentos = maximoReintentos;
    }
    
    @Override
    public Resultado enviarNotificacion(String idUsuario, String mensaje) {
        for (int intento = 1; intento <= maximoReintentos; intento++) {
            Resultado resultado = servicioBase.enviarNotificacion(idUsuario, mensaje);
            
            if (resultado.getExito()) {
                return resultado;
            }
            
            System.out.println("Reintento " + intento + " fallido. Esperando...");
            if (intento < maximoReintentos) {
                try {
                    Thread.sleep(100); // Backoff rápido emulado para pruebas
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        
        return Resultado.Fallido("Falló después de " + maximoReintentos + " intentos");
    }
}
