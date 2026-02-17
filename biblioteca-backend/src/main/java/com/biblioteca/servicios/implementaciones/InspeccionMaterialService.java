package com.biblioteca.servicios.implementaciones;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.biblioteca.dominio.entidades.Material;
import com.biblioteca.dominio.enumeraciones.NivelGravedad;
import com.biblioteca.dominio.enumeraciones.TipoDano;
import com.biblioteca.dominio.objetosvalor.Dano;
import com.biblioteca.dominio.objetosvalor.Evaluacion;
import com.biblioteca.repositorios.IRepositorio;
import com.biblioteca.servicios.interfaces.IInspeccionMaterialService;

public class InspeccionMaterialService implements IInspeccionMaterialService {
    
    private final IRepositorio<Material> repoMaterial;
    private final Random random;
    
    public InspeccionMaterialService(IRepositorio<Material> repoMaterial) {
        this.repoMaterial = repoMaterial;
        this.random = new Random();
    }
    
    @Override
    public Evaluacion inspeccionarMaterial(String idMaterial) {
        Material material = repoMaterial.obtenerPorId(idMaterial);
        if (material == null) {
            // Si no existe el material, retornar evaluación vacía
            return new Evaluacion(false, new ArrayList<>());
        }
        
        // En un caso real, aquí se haría una inspección física
        // Por ahora, simulamos una inspección aleatoria
        return simularInspeccion(material);
    }
    
    /**
     * Inspección manual donde se especifican los daños encontrados
     */
    public Evaluacion inspeccionarManual(String idMaterial, List<Dano> danosEncontrados) {
        Material material = repoMaterial.obtenerPorId(idMaterial);
        if (material == null) {
            return new Evaluacion(false, new ArrayList<>());
        }
        
        boolean usable = determinarUsabilidad(danosEncontrados);
        return new Evaluacion(usable, danosEncontrados);
    }
    
    /**
     * Inspección rápida solo para verificar si está en buen estado
     */
    public boolean verificarBuenEstado(String idMaterial) {
        Evaluacion eval = inspeccionarMaterial(idMaterial);
        return eval.esUsable() && !eval.tieneDanos();
    }
    
    private Evaluacion simularInspeccion(Material material) {
        List<Dano> danos = new ArrayList<>();
        
        // 70% de probabilidad de que esté en buen estado
        if (random.nextInt(100) < 70) {
            return new Evaluacion(true, danos);
        }
        
        // 30% de probabilidad de tener daños
        int cantidadDanos = random.nextInt(3) + 1; // 1 a 3 daños
        
        for (int i = 0; i < cantidadDanos; i++) {
            Dano dano = generarDanoAleatorio();
            danos.add(dano);
        }
        
        boolean usable = determinarUsabilidad(danos);
        return new Evaluacion(usable, danos);
    }
    
    private Dano generarDanoAleatorio() {
        TipoDano[] tipos = TipoDano.values();
        NivelGravedad[] gravedades = NivelGravedad.values();
        
        TipoDano tipo = tipos[random.nextInt(tipos.length)];
        NivelGravedad gravedad = gravedades[random.nextInt(gravedades.length)];
        
        String descripcion = generarDescripcionDano(tipo, gravedad);
        
        return new Dano(descripcion, gravedad, tipo);
    }
    
    private String generarDescripcionDano(TipoDano tipo, NivelGravedad gravedad) {
        switch (tipo) {
            case PAGINAS_RASGADAS:
                return "Páginas " + (gravedad == NivelGravedad.LEVE ? "levemente" :
                                    gravedad == NivelGravedad.MODERADO ? "moderadamente" :
                                    gravedad == NivelGravedad.GRAVE ? "gravemente" : "totalmente") + 
                                    " rasgadas";
            case MANCHAS:
                return "Manchas de " + (gravedad == NivelGravedad.LEVE ? "agua" :
                                       gravedad == NivelGravedad.MODERADO ? "café" :
                                       gravedad == NivelGravedad.GRAVE ? "tinta" : "humedad generalizada");
            case CUBIERTA_DANADA:
                return "Cubierta " + (gravedad == NivelGravedad.LEVE ? "desgastada" :
                                      gravedad == NivelGravedad.MODERADO ? "rota parcialmente" :
                                      gravedad == NivelGravedad.GRAVE ? "gravemente dañada" : "destruida");
            case RAYONES:
                return (gravedad == NivelGravedad.LEVE ? "Rayones superficiales" :
                       gravedad == NivelGravedad.MODERADO ? "Rayones visibles" :
                       gravedad == NivelGravedad.GRAVE ? "Rayones profundos" : "Superficie ilegible");
            case NO_FUNCIONAL:
                return "Material " + (gravedad == NivelGravedad.IRREPARABLE ? "inservible" : "con fallas");
            default:
                return "Daño no especificado";
        }
    }
    
    private boolean determinarUsabilidad(List<Dano> danos) {
        if (danos.isEmpty()) {
            return true;
        }
        
        // Si hay algún daño IRREPARABLE, el material no es usable
        boolean tieneIrreparable = danos.stream()
            .anyMatch(d -> d.getGravedad() == NivelGravedad.IRREPARABLE);
        
        if (tieneIrreparable) {
            return false;
        }
        
        // Si hay más de 2 daños GRAVE, considerar no usable
        long danosGraves = danos.stream()
            .filter(d -> d.getGravedad() == NivelGravedad.GRAVE)
            .count();
        
        return danosGraves <= 2;
    }
    
    /**
     * Inspección específica para libros
     */
    public Evaluacion inspeccionarLibro(String idMaterial) {
        // Podría tener lógica específica para libros
        return inspeccionarMaterial(idMaterial);
    }
    
    /**
     * Inspección específica para DVDs
     */
    public Evaluacion inspeccionarDVD(String idMaterial) {
        // Podría tener lógica específica para DVDs
        return inspeccionarMaterial(idMaterial);
    }
    
    /**
     * Inspección detallada que genera un reporte
     */
    public String generarReporteInspeccion(String idMaterial) {
        Evaluacion eval = inspeccionarMaterial(idMaterial);
        Material material = repoMaterial.obtenerPorId(idMaterial);
        
        StringBuilder reporte = new StringBuilder();
        reporte.append("=== REPORTE DE INSPECCIÓN ===\n");
        reporte.append("Material: ").append(material != null ? material.getTitulo() : "Desconocido").append("\n");
        reporte.append("ID: ").append(idMaterial).append("\n");
        reporte.append("Estado general: ").append(eval.esUsable() ? "USABLE" : "NO USABLE").append("\n");
        
        if (eval.tieneDanos()) {
            reporte.append("\nDAÑOS ENCONTRADOS:\n");
            for (Dano d : eval.getDanos()) {
                reporte.append("  • ").append(d.toString()).append("\n");
            }
        } else {
            reporte.append("\nMaterial en perfecto estado.\n");
        }
        
        return reporte.toString();
    }
}