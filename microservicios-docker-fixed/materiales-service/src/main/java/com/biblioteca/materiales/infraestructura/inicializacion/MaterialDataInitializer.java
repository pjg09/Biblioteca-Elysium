package com.biblioteca.materiales.infraestructura.inicializacion;

import com.biblioteca.materiales.infraestructura.persistencia.MaterialEntity;
import com.biblioteca.materiales.infraestructura.persistencia.MaterialJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
public class MaterialDataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(MaterialDataInitializer.class);

    private final MaterialJpaRepository materialRepository;

    public MaterialDataInitializer(MaterialJpaRepository materialRepository) {
        this.materialRepository = materialRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (materialRepository.count() == 0) {
            log.info("Cargando datos de prueba de materiales...");
            cargarDatosPrueba();
        } else {
            log.info("Materiales ya cargados. Saltando inicialización.");
        }
    }

    private void cargarDatosPrueba() {
        MaterialEntity[] materiales = {
            crearMaterial("LIB-001", "Clean Code", "Robert C. Martin", "LIBRO", 450.00),
            crearMaterial("LIB-002", "Design Patterns", "Gang of Four", "LIBRO", 520.00),
            crearMaterial("LIB-003", "The Pragmatic Programmer", "Hunt & Thomas", "LIBRO", 480.00),
            crearMaterial("REV-001", "Java Magazine", "Oracle", "REVISTA", 150.00),
            crearMaterial("DVD-001", "Introduction to Algorithms", "MIT", "DVD", 300.00),
            crearMaterial("LIB-004", "Refactoring", "Martin Fowler", "LIBRO", 490.00),
            crearMaterial("LIB-005", "The Art of Computer Programming", "Donald Knuth", "LIBRO", 600.00),
            crearMaterial("REV-002", "Software Architecture Review", "IEEE", "REVISTA", 180.00),
        };

        for (MaterialEntity material : materiales) {
            materialRepository.save(material);
            log.debug("Material guardado: {} - {}", material.getId(), material.getTitulo());
        }

        log.info("✓ {} materiales cargados exitosamente", materiales.length);
    }

    private MaterialEntity crearMaterial(String id, String titulo, String autor, String tipo, double precio) {
        MaterialEntity material = new MaterialEntity();
        material.setId(id);
        material.setTitulo(titulo);
        material.setAutor(autor);
        material.setTipo(tipo);
        material.setEstado("DISPONIBLE");
        material.setPrecio(precio);
        material.setFechaCreacion(LocalDateTime.now());
        return material;
    }
}
