package com.biblioteca.multas.api;

import com.biblioteca.multas.infraestructura.persistencia.MultaEntity;
import com.biblioteca.multas.infraestructura.persistencia.MultaJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Profile("dev")
@Component
public class DataInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);
    private final MultaJpaRepository repo;

    public DataInitializer(MultaJpaRepository repo) {
        this.repo = repo;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (repo.count() > 0) return;

        log.info("Cargando datos mock de multas...");

        MultaEntity multa = new MultaEntity();
        multa.setId("MUL-000001");
        multa.setIdPrestamo("PRE-000004");
        multa.setIdUsuario("USR-000002");
        multa.setTipoMulta("POR_RETRASO");
        multa.setMontoTotal(30000.0);
        multa.setEstado("GENERADA");
        multa.setFechaGeneracion(LocalDateTime.now());
        multa.setMotivo("Retraso de 15 días en devolución de MAT-000002");
        repo.save(multa);

        log.info("1 multa mock cargada.");
    }
}
