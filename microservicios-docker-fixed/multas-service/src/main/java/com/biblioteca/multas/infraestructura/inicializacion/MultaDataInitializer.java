package com.biblioteca.multas.infraestructura.inicializacion;

import com.biblioteca.multas.infraestructura.persistencia.MultaEntity;
import com.biblioteca.multas.infraestructura.persistencia.MultaJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class MultaDataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(MultaDataInitializer.class);

    private final MultaJpaRepository multaRepository;

    public MultaDataInitializer(MultaJpaRepository multaRepository) {
        this.multaRepository = multaRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (multaRepository.count() == 0) {
            log.info("Cargando datos de prueba de multas...");
            cargarDatosPrueba();
        } else {
            log.info("Multas ya cargadas. Saltando inicialización.");
        }
    }

    private void cargarDatosPrueba() {
        LocalDateTime ahora = LocalDateTime.now();
        
        MultaEntity[] multas = {
            crearMulta(UUID.randomUUID().toString(), "PREST-001", "EST-003", 
                      "RETRASO", 150.00, "PENDIENTE", ahora.minusDays(3), null,
                      "Retraso en devolución de revista"),
            crearMulta(UUID.randomUUID().toString(), "PREST-002", "EST-002", 
                      "DAÑO", 300.00, "PAGADA", ahora.minusDays(5), ahora.minusDays(2),
                      "Daño en material - Portada rota"),
        };

        for (MultaEntity multa : multas) {
            multaRepository.save(multa);
            log.debug("Multa guardada: {} - Usuario: {}, Monto: ${}", 
                     multa.getId(), multa.getIdUsuario(), multa.getMontoTotal());
        }

        log.info("✓ {} multas cargadas exitosamente", multas.length);
    }

    private MultaEntity crearMulta(String id, String idPrestamo, String idUsuario, 
                                   String tipoMulta, double montoTotal, String estado,
                                   LocalDateTime fechaGeneracion, LocalDateTime fechaPago,
                                   String motivo) {
        MultaEntity multa = new MultaEntity();
        multa.setId(id);
        multa.setIdPrestamo(idPrestamo);
        multa.setIdUsuario(idUsuario);
        multa.setTipoMulta(tipoMulta);
        multa.setMontoTotal(montoTotal);
        multa.setEstado(estado);
        multa.setFechaGeneracion(fechaGeneracion);
        multa.setFechaPago(fechaPago);
        multa.setMotivo(motivo);
        return multa;
    }
}
