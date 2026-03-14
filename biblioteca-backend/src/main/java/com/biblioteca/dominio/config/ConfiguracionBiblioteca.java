package com.biblioteca.dominio.config;

import java.util.HashMap;
import java.util.Map;

import com.biblioteca.dominio.enumeraciones.TipoUsuario;

public class ConfiguracionBiblioteca {
    private static volatile ConfiguracionBiblioteca instancia;
    
    private final double umbralBloqueoMulta;
    private final int maximoRenovaciones;
    private final Map<TipoUsuario, Integer> limitesPorTipo;
    
    private ConfiguracionBiblioteca() {
        // Cargar desde archivo de configuración o base de datos en el futuro
        this.umbralBloqueoMulta = 10000.0;
        this.maximoRenovaciones = 2;
        this.limitesPorTipo = new HashMap<>();
        this.limitesPorTipo.put(TipoUsuario.ESTUDIANTE, 3);
        this.limitesPorTipo.put(TipoUsuario.PROFESOR, 5);
        this.limitesPorTipo.put(TipoUsuario.INVESTIGADOR, 10);
        this.limitesPorTipo.put(TipoUsuario.PUBLICO_GENERAL, 2);
    }
    
    public static ConfiguracionBiblioteca obtenerInstancia() {
        if (instancia == null) {
            synchronized (ConfiguracionBiblioteca.class) {
                if (instancia == null) {
                    instancia = new ConfiguracionBiblioteca();
                }
            }
        }
        return instancia;
    }
    
    public double getUmbralBloqueoMulta() {
        return umbralBloqueoMulta;
    }
    
    public int getMaximoRenovaciones() {
        return maximoRenovaciones;
    }

    public int getLimitePara(TipoUsuario tipoUsuario) {
        return limitesPorTipo.getOrDefault(tipoUsuario, 1);
    }
}
