package com.biblioteca.multas.dominio.servicios;

import org.springframework.stereotype.Service;

@Service
public class CalculadorMultaService {

    private static final double TARIFA_LIBRO_NORMAL = 1000.0;

    public double calcularMontoRetraso(int diasRetraso, String tipoUsuario) {
        double tarifa = TARIFA_LIBRO_NORMAL;

        double multiplicador;
        if (tipoUsuario == null) {
            multiplicador = 1.0;
        } else {
            switch (tipoUsuario.toUpperCase()) {
                case "ESTUDIANTE":
                    multiplicador = 1.0;
                    break;
                case "PROFESOR":
                    multiplicador = 0.5;
                    break;
                case "INVESTIGADOR":
                    multiplicador = 0.0;
                    break;
                case "PUBLICO_GENERAL":
                    multiplicador = 1.5;
                    break;
                default:
                    multiplicador = 1.0;
            }
        }

        return diasRetraso * tarifa * multiplicador;
    }

    public double calcularMontoPerdida(double valorMaterial, String tipoUsuario) {
        double recargo;
        if (tipoUsuario == null) {
            recargo = 0.20;
        } else {
            switch (tipoUsuario.toUpperCase()) {
                case "ESTUDIANTE":
                    recargo = 0.20;
                    break;
                case "PROFESOR":
                    recargo = 0.10;
                    break;
                case "INVESTIGADOR":
                    recargo = 0.0;
                    break;
                case "PUBLICO_GENERAL":
                    recargo = 0.30;
                    break;
                default:
                    recargo = 0.20;
            }
        }

        return valorMaterial * (1 + recargo);
    }

    public double calcularMontoDano(double valorMaterial, String gravedad) {
        double factor;
        if (gravedad == null) {
            factor = 0.20;
        } else {
            switch (gravedad.toUpperCase()) {
                case "LEVE":
                    factor = 0.20;
                    break;
                case "MODERADO":
                    factor = 0.40;
                    break;
                case "GRAVE":
                    factor = 0.70;
                    break;
                case "IRREPARABLE":
                    factor = 1.0;
                    break;
                default:
                    factor = 0.20;
            }
        }

        return valorMaterial * factor;
    }
}
