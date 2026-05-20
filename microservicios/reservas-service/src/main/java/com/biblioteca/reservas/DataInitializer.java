package com.biblioteca.reservas;

import com.biblioteca.reservas.infraestructura.persistencia.ReservaEntity;
import com.biblioteca.reservas.infraestructura.persistencia.ReservaJpaRepository;
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
    private final ReservaJpaRepository repo;

    public DataInitializer(ReservaJpaRepository repo) {
        this.repo = repo;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (repo.count() > 0) return;

        log.info("Cargando datos mock de reservas...");

        // MAT-000001 está PRESTADO por USR-000001 — 2 reservas en cola
        repo.save(reserva("RES-000001", "USR-000004", "MAT-000001", 1, "Sede Central"));
        repo.save(reserva("RES-000002", "USR-000003", "MAT-000001", 2, "Sede Central"));
        // MAT-000004 está PRESTADO por USR-000003 — 1 reserva en cola
        repo.save(reserva("RES-000003", "USR-000006", "MAT-000004", 1, "Sede Norte"));

        log.info("3 reservas mock cargadas.");
    }

    private ReservaEntity reserva(String id, String idUsuario, String idMaterial, int posicion, String sede) {
        ReservaEntity e = new ReservaEntity();
        e.setId(id);
        e.setIdUsuario(idUsuario);
        e.setIdMaterial(idMaterial);
        e.setPosicionCola(posicion);
        e.setEstadoReserva("EN_ESPERA");
        e.setFechaReserva(LocalDateTime.now());
        e.setFechaExpiracion(LocalDateTime.now().plusDays(7));
        e.setSede(sede);
        return e;
    }
}
