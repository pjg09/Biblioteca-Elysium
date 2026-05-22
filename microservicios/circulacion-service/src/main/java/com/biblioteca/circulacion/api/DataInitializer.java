package com.biblioteca.circulacion.api;

import com.biblioteca.circulacion.infraestructura.persistencia.PrestamoEntity;
import com.biblioteca.circulacion.infraestructura.persistencia.PrestamoJpaRepository;
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
    private final PrestamoJpaRepository repo;

    public DataInitializer(PrestamoJpaRepository repo) {
        this.repo = repo;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (repo.count() > 0) return;

        log.info("Cargando datos mock de préstamos...");

        LocalDateTime hoy = LocalDateTime.now();

        // Préstamo activo normal
        repo.save(prestamo("PRE-000001", "USR-000001", "MAT-000001",
                hoy.minusDays(5), hoy.plusDays(10), null, "ACTIVO", "NORMAL", "Estante A1"));

        // Préstamo activo normal
        repo.save(prestamo("PRE-000002", "USR-000003", "MAT-000004",
                hoy.minusDays(3), hoy.plusDays(5), null, "ACTIVO", "NORMAL", "Estante D3"));

        // Préstamo activo interbibliotecario
        repo.save(prestamo("PRE-000003", "USR-000005", "MAT-000003",
                hoy.minusDays(1), hoy.plusDays(20), null, "ACTIVO", "INTERBIBLIOTECARIO", "Biblioteca Central"));

        // Préstamo vencido (genera multa a USR-000002)
        repo.save(prestamo("PRE-000004", "USR-000002", "MAT-000002",
                hoy.minusDays(30), hoy.minusDays(15), null, "ACTIVO", "NORMAL", "Estante B2"));

        // USR-000008 (PUBLICO_GENERAL, límite=3) con 3 préstamos activos — para probar límite máximo
        repo.save(prestamo("PRE-000005", "USR-000008", "MAT-000005",
                hoy.minusDays(2), hoy.plusDays(7), null, "ACTIVO", "NORMAL", "Sede Central"));
        repo.save(prestamo("PRE-000006", "USR-000008", "MAT-000006",
                hoy.minusDays(1), hoy.plusDays(8), null, "ACTIVO", "NORMAL", "Sede Central"));
        repo.save(prestamo("PRE-000007", "USR-000008", "MAT-000007",
                hoy.minusDays(1), hoy.plusDays(8), null, "ACTIVO", "NORMAL", "Sede Central"));

        log.info("7 préstamos mock cargados.");
    }

    private PrestamoEntity prestamo(String id, String idUsuario, String idMaterial,
                                    LocalDateTime fechaPrestamo, LocalDateTime fechaDevEsperada,
                                    LocalDateTime fechaDevReal, String estado, String tipo, String sede) {
        PrestamoEntity e = new PrestamoEntity();
        e.setId(id);
        e.setIdUsuario(idUsuario);
        e.setIdMaterial(idMaterial);
        e.setFechaPrestamo(fechaPrestamo);
        e.setFechaDevolucionEsperada(fechaDevEsperada);
        e.setFechaDevolucionReal(fechaDevReal);
        e.setRenovacionesUsadas(0);
        e.setEstado(estado);
        e.setTipoPrestamo(tipo);
        e.setSede(sede);
        return e;
    }
}
