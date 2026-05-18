package com.biblioteca.reservas.infraestructura.inicializacion;

import com.biblioteca.reservas.infraestructura.persistencia.ReservaEntity;
import com.biblioteca.reservas.infraestructura.persistencia.ReservaJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class ReservaDataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(ReservaDataInitializer.class);

    private final ReservaJpaRepository reservaRepository;

    public ReservaDataInitializer(ReservaJpaRepository reservaRepository) {
        this.reservaRepository = reservaRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (reservaRepository.count() == 0) {
            log.info("Cargando datos de prueba de reservas...");
            cargarDatosPrueba();
        } else {
            log.info("Reservas ya cargadas. Saltando inicialización.");
        }
    }

    private void cargarDatosPrueba() {
        LocalDateTime ahora = LocalDateTime.now();
        
        ReservaEntity[] reservas = {
            crearReserva(UUID.randomUUID().toString(), "EST-001", "LIB-005", 
                        1, "EN_ESPERA", ahora.minusDays(2), null, 
                        ahora.plusDays(30), "Campus Principal"),
            crearReserva(UUID.randomUUID().toString(), "EST-002", "LIB-004", 
                        2, "EN_ESPERA", ahora.minusDays(1), null,
                        ahora.plusDays(30), "Campus Principal"),
            crearReserva(UUID.randomUUID().toString(), "EST-004", "LIB-001", 
                        1, "EN_ESPERA", ahora, null,
                        ahora.plusDays(30), "Campus Principal"),
        };

        for (ReservaEntity reserva : reservas) {
            reservaRepository.save(reserva);
            log.debug("Reserva guardada: {} - Usuario: {}, Material: {}, Posición: {}", 
                     reserva.getId(), reserva.getIdUsuario(), reserva.getIdMaterial(), 
                     reserva.getPosicionCola());
        }

        log.info("✓ {} reservas cargadas exitosamente", reservas.length);
    }

    private ReservaEntity crearReserva(String id, String idUsuario, String idMaterial,
                                      int posicionCola, String estadoReserva,
                                      LocalDateTime fechaReserva, LocalDateTime fechaNotificacion,
                                      LocalDateTime fechaExpiracion, String sede) {
        ReservaEntity reserva = new ReservaEntity();
        reserva.setId(id);
        reserva.setIdUsuario(idUsuario);
        reserva.setIdMaterial(idMaterial);
        reserva.setPosicionCola(posicionCola);
        reserva.setEstadoReserva(estadoReserva);
        reserva.setFechaReserva(fechaReserva);
        reserva.setFechaNotificacion(fechaNotificacion);
        reserva.setFechaExpiracion(fechaExpiracion);
        reserva.setSede(sede);
        return reserva;
    }
}
