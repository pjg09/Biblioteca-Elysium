package com.biblioteca.reservas.aplicacion;

import com.biblioteca.reservas.aplicacion.dto.CrearReservaRequest;
import com.biblioteca.reservas.dominio.Reserva;
import com.biblioteca.reservas.dominio.builders.ReservaBuilder;
import com.biblioteca.commons.excepciones.OperacionNoPermitidaEnEstadoReservaException;
import com.biblioteca.reservas.infraestructura.mensajeria.RabbitMQConfig;
import com.biblioteca.reservas.infraestructura.persistencia.ReservaEntity;
import com.biblioteca.reservas.infraestructura.persistencia.ReservaJpaRepository;
import com.biblioteca.commons.objetosvalor.Resultado;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class ReservaService {

    private static final Logger log = LoggerFactory.getLogger(ReservaService.class);

    private final ReservaJpaRepository reservaJpaRepository;
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    private static final List<String> ESTADOS_ACTIVOS = List.of("EN_ESPERA", "NOTIFICADA");

    public ReservaService(ReservaJpaRepository reservaJpaRepository,
                          RabbitTemplate rabbitTemplate,
                          ObjectMapper objectMapper) {
        this.reservaJpaRepository = reservaJpaRepository;
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * Crea una nueva reserva en la cola para un material usando validaciones de dominio.
     * La posición se calcula como el total de reservas activas + 1.
     */
    @Transactional
    public ReservaEntity crearReserva(CrearReservaRequest req) {
        // Calcular posición en cola
        List<ReservaEntity> activas = reservaJpaRepository
                .findByIdMaterialAndEstadoReservaIn(req.getIdMaterial(), ESTADOS_ACTIVOS);
        int posicion = activas.size() + 1;

        // Usar builder para crear y validar la reserva
        long count = reservaJpaRepository.count();
        String id = String.format("RES-%06d", count + 1);
        ReservaBuilder builder = new ReservaBuilder()
                .conId(id)
                .conIdUsuario(req.getIdUsuario())
                .conIdMaterial(req.getIdMaterial())
                .conPosicionCola(posicion)
                .conEstadoReserva("EN_ESPERA")
                .conFechaReserva(LocalDateTime.now())
                .conSede(req.getSede());

        Resultado<Reserva> resultadoReserva = builder.construir();
        if (resultadoReserva.esError()) {
            throw new IllegalArgumentException("Error al crear la reserva: " + resultadoReserva.getMensajeError());
        }

        Reserva reservaValida = resultadoReserva.getValor();

        // Convertir a Entity para persistencia
        ReservaEntity entity = new ReservaEntity();
        entity.setId(reservaValida.getId());
        entity.setIdUsuario(reservaValida.getIdUsuario());
        entity.setIdMaterial(reservaValida.getIdMaterial());
        entity.setPosicionCola(reservaValida.getPosicionCola());
        entity.setEstadoReserva(reservaValida.getEstadoReserva());
        entity.setFechaReserva(reservaValida.getFechaReserva());
        entity.setSede(reservaValida.getSede());

        ReservaEntity saved = reservaJpaRepository.save(entity);
        log.info("Reserva creada con validaciones de dominio: id={}, usuario={}, material={}, posicion={}",
                saved.getId(), saved.getIdUsuario(), saved.getIdMaterial(), saved.getPosicionCola());

        // Publicar evento ReservaCreada
        Map<String, Object> evento = new HashMap<>();
        evento.put("eventType", "reserva.creada");
        evento.put("reservaId", saved.getId());
        evento.put("usuarioId", saved.getIdUsuario());
        evento.put("materialId", saved.getIdMaterial());
        evento.put("posicionCola", saved.getPosicionCola());
        evento.put("sede", saved.getSede());
        evento.put("fechaReserva", saved.getFechaReserva().toString());

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.ROUTING_KEY_RESERVA_CREADA,
                evento);

        return saved;
    }

    /**
     * Cancela una reserva por id y reorganiza la cola del material.
     */
    /**
 * Cancela una reserva por id y reorganiza la cola del material.
 */
@Transactional
public ReservaEntity cancelarReserva(String id) throws OperacionNoPermitidaEnEstadoReservaException {
    ReservaEntity entity = reservaJpaRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada: " + id));

    Reserva dominio = entity.toDomain();
    try {
        dominio.cancelar();
    } catch (OperacionNoPermitidaEnEstadoReservaException e) {
        log.warn("No se puede cancelar reserva en estado {}: {}", dominio.getEstadoReserva(), e.getMessage());
        throw new OperacionNoPermitidaEnEstadoReservaException(
            "cancelarReserva",
            "Reserva en estado " + dominio.getEstadoReserva() + " no puede ser cancelada"
        );
    }

    entity.setEstadoReserva(dominio.getEstadoReserva());
    ReservaEntity saved = reservaJpaRepository.save(entity);
    log.info("Reserva cancelada: id={}, material={}", id, entity.getIdMaterial());

    // Publicar evento ReservaCancelada
        Map<String, Object> evento = new HashMap<>();
        evento.put("eventType", "reserva.cancelada");
        evento.put("reservaId", saved.getId());
        evento.put("usuarioId", saved.getIdUsuario());
        evento.put("materialId", saved.getIdMaterial());

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.ROUTING_KEY_RESERVA_CANCELADA,
                evento);

        // Reorganizar cola del material
        reorganizarCola(entity.getIdMaterial());

        return saved;
    }

    /**
     * Obtiene una reserva por su id.
     */
    @Transactional(readOnly = true)
    public Optional<ReservaEntity> obtenerPorId(String id) {
        return reservaJpaRepository.findById(id);
    }

    /**
     * Lista las reservas activas de un material ordenadas por posición.
     */
    @Transactional(readOnly = true)
    public List<ReservaEntity> listarPorMaterial(String idMaterial) {
        return reservaJpaRepository.findByIdMaterialAndEstadoReservaIn(idMaterial, ESTADOS_ACTIVOS);
    }

    /**
     * Lista las reservas activas de un usuario.
     */
    @Transactional(readOnly = true)
    public List<ReservaEntity> listarPorUsuario(String idUsuario) {
        return reservaJpaRepository.findByIdUsuarioAndEstadoReservaIn(idUsuario, ESTADOS_ACTIVOS);
    }

    @Transactional(readOnly = true)
    public List<ReservaEntity> listarTodas() {
        return reservaJpaRepository.findAll();
    }

    /**
     * Reorganiza las posiciones de la cola EN_ESPERA para un material.
     * Asigna posiciones 1, 2, 3... consecutivamente.
     */
    @Transactional
    public void reorganizarCola(String idMaterial) {
        List<ReservaEntity> enEspera = reservaJpaRepository
                .findByIdMaterialAndEstadoReserva(idMaterial, "EN_ESPERA");

        int nuevaPosicion = 1;
        for (ReservaEntity r : enEspera) {
            r.setPosicionCola(nuevaPosicion++);
            reservaJpaRepository.save(r);
        }
        log.info("Cola reorganizada para material={}: {} reservas en espera", idMaterial, enEspera.size());
    }
}
