package com.biblioteca.cobros.aplicacion;

import com.biblioteca.cobros.aplicacion.dto.RegistrarPagoRequest;
import com.biblioteca.cobros.dominio.RegistroPago;
import com.biblioteca.cobros.infraestructura.mensajeria.RabbitMQConfig;
import com.biblioteca.cobros.infraestructura.persistencia.DeudaPendienteEntity;
import com.biblioteca.cobros.infraestructura.persistencia.DeudaPendienteJpaRepository;
import com.biblioteca.cobros.infraestructura.persistencia.RegistroPagoEntity;
import com.biblioteca.cobros.infraestructura.persistencia.RegistroPagoJpaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CobroService {

    private static final Logger log = LoggerFactory.getLogger(CobroService.class);

    private final RegistroPagoJpaRepository registroPagoJpaRepository;
    private final DeudaPendienteJpaRepository deudaPendienteJpaRepository;
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public CobroService(RegistroPagoJpaRepository registroPagoJpaRepository,
                        DeudaPendienteJpaRepository deudaPendienteJpaRepository,
                        RabbitTemplate rabbitTemplate,
                        ObjectMapper objectMapper) {
        this.registroPagoJpaRepository = registroPagoJpaRepository;
        this.deudaPendienteJpaRepository = deudaPendienteJpaRepository;
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public RegistroPagoEntity registrarPago(RegistrarPagoRequest req) {
        // Step 1: Create domain object and save
        RegistroPago registroPago = RegistroPago.crear(req.getMultaId(), req.getUsuarioId(), req.getMonto());
        RegistroPagoEntity savedEntity = registroPagoJpaRepository.save(RegistroPagoEntity.fromDomain(registroPago));
        log.info("Payment recorded: id={} multaId={} usuarioId={} monto={}",
            savedEntity.getId(), savedEntity.getMultaId(), savedEntity.getUsuarioId(), savedEntity.getMonto());

        // Step 2: Publish MultaPagada event
        Map<String, Object> multaPagadaEvent = new HashMap<>();
        multaPagadaEvent.put("multaId", req.getMultaId());
        multaPagadaEvent.put("usuarioId", req.getUsuarioId());
        multaPagadaEvent.put("montoPagado", req.getMonto());
        multaPagadaEvent.put("fechaPago", registroPago.getFechaPago().toString());

        rabbitTemplate.convertAndSend(
            RabbitMQConfig.EXCHANGE_NAME,
            RabbitMQConfig.ROUTING_KEY_MULTA_PAGADA,
            multaPagadaEvent
        );
        log.debug("Published multa.pagada event for multaId={}", req.getMultaId());

        // Step 3: Mark debt as paid in DeudaPendienteEntity
        Optional<DeudaPendienteEntity> deudaOpt = deudaPendienteJpaRepository.findByMultaId(req.getMultaId());
        if (deudaOpt.isPresent()) {
            DeudaPendienteEntity deuda = deudaOpt.get();
            deuda.setPagado(true);
            deudaPendienteJpaRepository.save(deuda);
            log.debug("Marked debt as paid for multaId={}", req.getMultaId());
        } else {
            log.warn("No tracked debt found for multaId={}, payment still recorded", req.getMultaId());
        }

        // Step 4: Check remaining debt for user
        List<DeudaPendienteEntity> deudasPendientes =
            deudaPendienteJpaRepository.findByUsuarioIdAndPagado(req.getUsuarioId(), false);

        double totalPendiente = deudasPendientes.stream()
            .mapToDouble(DeudaPendienteEntity::getMonto)
            .sum();

        log.debug("Remaining debt for usuario={}: {}", req.getUsuarioId(), totalPendiente);

        // Step 5: If no remaining debt, publish PeticionDesbloqueoUsuario
        if (totalPendiente == 0.0) {
            Map<String, Object> desbloqueoEvent = new HashMap<>();
            desbloqueoEvent.put("usuarioId", req.getUsuarioId());
            desbloqueoEvent.put("origen", "cobros-service");

            rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.ROUTING_KEY_USUARIO_DESBLOQUEO,
                desbloqueoEvent
            );
            log.info("Published usuario.desbloqueo.peticion for usuario={} — zero remaining debt",
                req.getUsuarioId());
        }

        return savedEntity;
    }

    public Optional<RegistroPagoEntity> obtenerPorId(String id) {
        return registroPagoJpaRepository.findById(id);
    }

    public List<RegistroPagoEntity> obtenerPorUsuario(String usuarioId) {
        return registroPagoJpaRepository.findByUsuarioId(usuarioId);
    }
}
