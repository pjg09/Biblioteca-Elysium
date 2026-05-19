package com.biblioteca.materiales;

import com.biblioteca.materiales.infraestructura.persistencia.MaterialEntity;
import com.biblioteca.materiales.infraestructura.persistencia.MaterialJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);
    private final MaterialJpaRepository repo;

    public DataInitializer(MaterialJpaRepository repo) {
        this.repo = repo;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (repo.count() > 0) return;

        log.info("Cargando datos mock de materiales...");

        repo.save(material("MAT-000001", "Cien años de soledad",   "Gabriel García Márquez", "BESTSELLER",   80000.0, "PRESTADO"));
        repo.save(material("MAT-000002", "El principito",           "Antoine de Saint-Exupéry","LIBRO_NORMAL", 50000.0, "PRESTADO"));
        repo.save(material("MAT-000003", "Don Quijote de la Mancha","Miguel de Cervantes",     "REFERENCIA",  120000.0, "PRESTADO"));
        repo.save(material("MAT-000004", "Inception",               "Christopher Nolan",       "DVD",          35000.0, "PRESTADO"));
        repo.save(material("MAT-000005", "El Padrino",              "Francis Ford Coppola",    "DVD",          40000.0, "DISPONIBLE"));
        repo.save(material("MAT-000006", "National Geographic",     "Varios",                  "REVISTA",      20000.0, "DISPONIBLE"));
        repo.save(material("MAT-000007", "Muy Interesante",         "Varios",                  "REVISTA",      15000.0, "DISPONIBLE"));
        repo.save(material("MAT-000008", "Clean Code",              "Robert Martin",           "EBOOK",        60000.0, "DISPONIBLE"));
        repo.save(material("MAT-000009", "The Pragmatic Programmer","David Thomas",            "EBOOK",        60000.0, "DISPONIBLE"));

        log.info("9 materiales mock cargados.");
    }

    private MaterialEntity material(String id, String titulo, String autor, String tipo, double precio, String estado) {
        MaterialEntity e = new MaterialEntity();
        e.setId(id);
        e.setTitulo(titulo);
        e.setAutor(autor);
        e.setTipo(tipo);
        e.setPrecio(precio);
        e.setEstado(estado);
        e.setFechaCreacion(LocalDateTime.now());
        return e;
    }
}
