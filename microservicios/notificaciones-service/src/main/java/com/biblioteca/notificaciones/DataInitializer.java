package com.biblioteca.notificaciones;

import com.biblioteca.notificaciones.dominio.Notificacion;
import com.biblioteca.notificaciones.infraestructura.persistencia.NotificacionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Profile("dev")
@Component
public class DataInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final NotificacionRepository notificacionRepository;

    public DataInitializer(NotificacionRepository notificacionRepository) {
        this.notificacionRepository = notificacionRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (notificacionRepository.count() > 0) return;

        log.info("Cargando datos mock de notificaciones...");

        List<Notificacion> mocks = List.of(
            notif("USR-000001", "PrestamoRegistrado",
                "Se ha registrado un nuevo préstamo para el material MAT-000001.",
                "PRE-000001", false, -2),
            notif("USR-000001", "PrestamoRenovado",
                "Tu préstamo ha sido renovado. Nueva fecha de devolución: 2026-06-05.",
                "PRE-000001", false, -1),
            notif("USR-000001", "MultaGenerada",
                "Se ha generado una multa de tipo RETRASO por un monto de 15000.00.",
                "MUL-000001", true, -5),
            notif("USR-000002", "ReservaCreada",
                "Reserva creada para el material MAT-000002. Posición en cola: 1.",
                "RES-000001", false, -3),
            notif("USR-000002", "ReservaNotificada",
                "El material que reservaste está disponible. Tienes hasta 2026-05-22 para recogerlo.",
                "RES-000001", false, -1),
            notif("USR-000003", "MaterialDevuelto",
                "Material devuelto a tiempo. ¡Gracias!",
                "PRE-000002", true, -4),
            notif("USR-000003", "MultaPagada",
                "Tu multa ha sido pagada por un monto de 30000.00.",
                "MUL-000002", true, -6)
        );

        notificacionRepository.saveAll(mocks);
        log.info("{} notificaciones mock cargadas.", mocks.size());
    }

    private Notificacion notif(String usuarioId, String eventType, String mensaje,
                               String aggregateId, boolean leida, int diasAtras) {
        Notificacion n = new Notificacion(usuarioId, eventType, mensaje, eventType, aggregateId);
        n.setFechaCreacion(LocalDateTime.now().plusDays(diasAtras));
        if (leida) n.marcarLeida();
        return n;
    }
}
