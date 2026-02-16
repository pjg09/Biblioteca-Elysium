package com.biblioteca.servicios.implementaciones;

import com.biblioteca.dominio.enumeraciones.NivelGravedad;
import com.biblioteca.dominio.enumeraciones.TipoDano;
import com.biblioteca.dominio.objetosValor.Dano;
import com.biblioteca.servicios.interfaces.ICalculadorCostoDanoService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CalculadorCostoDanoService implements ICalculadorCostoDanoService {
    
    // Mapa de tarifas: TipoDano -> (NivelGravedad -> costo)
    private Map<TipoDano, Map<NivelGravedad, Double>> tarifas;
    
    public CalculadorCostoDanoService() {
        this.tarifas = new HashMap<>();
        inicializarTarifas();
    }
    
    private void inicializarTarifas() {
        // Páginas rasgadas
        Map<NivelGravedad, Double> tarifasPaginas = new HashMap<>();
        tarifasPaginas.put(NivelGravedad.LEVE, 2000.0);
        tarifasPaginas.put(NivelGravedad.MODERADO, 5000.0);
        tarifasPaginas.put(NivelGravedad.GRAVE, 15000.0);
        tarifasPaginas.put(NivelGravedad.IRREPARABLE, 50000.0);
        tarifas.put(TipoDano.PAGINAS_RASGADAS, tarifasPaginas);
        
        // Manchas
        Map<NivelGravedad, Double> tarifasManchas = new HashMap<>();
        tarifasManchas.put(NivelGravedad.LEVE, 1000.0);
        tarifasManchas.put(NivelGravedad.MODERADO, 3000.0);
        tarifasManchas.put(NivelGravedad.GRAVE, 8000.0);
        tarifasManchas.put(NivelGravedad.IRREPARABLE, 20000.0);
        tarifas.put(TipoDano.MANCHAS, tarifasManchas);
        
        // Cubierta dañada
        Map<NivelGravedad, Double> tarifasCubierta = new HashMap<>();
        tarifasCubierta.put(NivelGravedad.LEVE, 3000.0);
        tarifasCubierta.put(NivelGravedad.MODERADO, 8000.0);
        tarifasCubierta.put(NivelGravedad.GRAVE, 20000.0);
        tarifasCubierta.put(NivelGravedad.IRREPARABLE, 60000.0);
        tarifas.put(TipoDano.CUBIERTA_DANADA, tarifasCubierta);
        
        // Rayones
        Map<NivelGravedad, Double> tarifasRayones = new HashMap<>();
        tarifasRayones.put(NivelGravedad.LEVE, 500.0);
        tarifasRayones.put(NivelGravedad.MODERADO, 2000.0);
        tarifasRayones.put(NivelGravedad.GRAVE, 5000.0);
        tarifasRayones.put(NivelGravedad.IRREPARABLE, 15000.0);
        tarifas.put(TipoDano.RAYONES, tarifasRayones);
        
        // No funcional
        Map<NivelGravedad, Double> tarifasNoFuncional = new HashMap<>();
        tarifasNoFuncional.put(NivelGravedad.LEVE, 0.0); // No aplica
        tarifasNoFuncional.put(NivelGravedad.MODERADO, 0.0); // No aplica
        tarifasNoFuncional.put(NivelGravedad.GRAVE, 0.0); // No aplica
        tarifasNoFuncional.put(NivelGravedad.IRREPARABLE, 100000.0);
        tarifas.put(TipoDano.NO_FUNCIONAL, tarifasNoFuncional);
    }
    
    @Override
    public double calcularCosto(Dano dano) {
        if (dano == null) {
            return 0.0;
        }
        
        Map<NivelGravedad, Double> tarifasPorGravedad = tarifas.get(dano.getTipo());
        if (tarifasPorGravedad == null) {
            return 0.0;
        }
        
        Double costo = tarifasPorGravedad.get(dano.getGravedad());
        return costo != null ? costo : 0.0;
    }
    
    @Override
    public double calcularCostoTotal(List<Dano> danos) {
        if (danos == null || danos.isEmpty()) {
            return 0.0;
        }
        
        return danos.stream()
                .mapToDouble(this::calcularCosto)
                .sum();
    }
    
    /**
     * Método para agregar o modificar tarifas dinámicamente
     */
    public void agregarTarifa(TipoDano tipo, NivelGravedad gravedad, double costo) {
        tarifas.computeIfAbsent(tipo, k -> new HashMap<>()).put(gravedad, costo);
    }
    
    /**
     * Obtiene la tarifa para un tipo y gravedad específicos
     */
    public Double obtenerTarifa(TipoDano tipo, NivelGravedad gravedad) {
        Map<NivelGravedad, Double> porGravedad = tarifas.get(tipo);
        if (porGravedad == null) {
            return null;
        }
        return porGravedad.get(gravedad);
    }
    
    /**
     * Lista todas las tarifas configuradas
     */
    public Map<TipoDano, Map<NivelGravedad, Double>> listarTarifas() {
        return new HashMap<>(tarifas);
    }
}