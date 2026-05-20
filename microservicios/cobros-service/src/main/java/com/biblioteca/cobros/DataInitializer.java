package com.biblioteca.cobros;

import com.biblioteca.cobros.infraestructura.persistencia.DeudaPendienteEntity;
import com.biblioteca.cobros.infraestructura.persistencia.DeudaPendienteJpaRepository;
import com.biblioteca.cobros.infraestructura.persistencia.RegistroPagoJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("dev")
@Component
public class DataInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);
    private final DeudaPendienteJpaRepository deudaRepo;
    private final RegistroPagoJpaRepository pagoRepo;

    public DataInitializer(DeudaPendienteJpaRepository deudaRepo,
                           RegistroPagoJpaRepository pagoRepo) {
        this.deudaRepo = deudaRepo;
        this.pagoRepo = pagoRepo;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (deudaRepo.count() > 0) return;

        log.info("Cargando datos mock de cobros...");

        DeudaPendienteEntity deuda = new DeudaPendienteEntity();
        deuda.setMultaId("MUL-000001");
        deuda.setUsuarioId("USR-000002");
        deuda.setMonto(30000.0);
        deuda.setPagado(false);
        deudaRepo.save(deuda);

        log.info("1 deuda pendiente mock cargada.");
    }
}
