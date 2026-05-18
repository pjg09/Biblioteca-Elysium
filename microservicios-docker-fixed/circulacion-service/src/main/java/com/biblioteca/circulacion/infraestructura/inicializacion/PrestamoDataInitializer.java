package com.biblioteca.circulacion.infraestructura.inicializacion;

import com.biblioteca.circulacion.infraestructura.persistencia.PrestamoEntity;
import com.biblioteca.circulacion.infraestructura.persistencia.PrestamoJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class PrestamoDataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(PrestamoDataInitializer.class);

    private final PrestamoJpaRepository prestamoRepository;

    public PrestamoDataInitializer(PrestamoJpaRepository prestamoRepository) {
        this.prestamoRepository = prestamoRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (prestamoRepository.count() == 0) {
            log.info("Cargando datos de prueba de préstamos...");
            cargarDatosPrueba();
        } else {
            log.info("Préstamos ya cargados. Saltando inicialización.");
        }
    }

    private void cargarDatosPrueba() {
        LocalDateTime ahora = LocalDateTime.now();
        
        PrestamoEntity[] prestamos = {
            crearPrestamo(UUID.randomUUID().toString(), "EST-001", "LIB-001", 
                         ahora.minusDays(5), ahora.plusDays(9), "ACTIVO", "NORMAL"),
            crearPrestamo(UUID.randomUUID().toString(), "EST-002", "LIB-002", 
                         ahora.minusDays(3), ahora.plusDays(11), "ACTIVO", "NORMAL"),
            crearPrestamo(UUID.randomUUID().toString(), "EST-003", "REV-001", 
                         ahora.minusDays(7), ahora.minusDays(2), "COMPLETADO", "NORMAL"),
            crearPrestamo(UUID.randomUUID().toString(), "DOC-001", "LIB-004", 
                         ahora.minusDays(10), ahora.plusDays(4), "ACTIVO", "DOCENTE"),
            crearPrestamo(UUID.randomUUID().toString(), "EST-004", "DVD-001", 
                         ahora.minusDays(2), ahora.plusDays(12), "ACTIVO", "NORMAL"),
        };

        for (PrestamoEntity prestamo : prestamos) {
            prestamoRepository.save(prestamo);
            log.debug("Préstamo guardado: {} - Usuario: {}, Material: {}", 
                     prestamo.getId(), prestamo.getIdUsuario(), prestamo.getIdMaterial());
        }

        log.info("✓ {} préstamos cargados exitosamente", prestamos.length);
    }

    private PrestamoEntity crearPrestamo(String id, String idUsuario, String idMaterial, 
                                        LocalDateTime fechaPrestamo, LocalDateTime fechaDevolucionEsperada,
                                        String estado, String tipoPrestamo) {
        PrestamoEntity prestamo = new PrestamoEntity();
        prestamo.setId(id);
        prestamo.setIdUsuario(idUsuario);
        prestamo.setIdMaterial(idMaterial);
        prestamo.setFechaPrestamo(fechaPrestamo);
        prestamo.setFechaDevolucionEsperada(fechaDevolucionEsperada);
        prestamo.setRenovacionesUsadas(0);
        prestamo.setEstado(estado);
        prestamo.setTipoPrestamo(tipoPrestamo);
        prestamo.setSede("Campus Principal");
        return prestamo;
    }
}
