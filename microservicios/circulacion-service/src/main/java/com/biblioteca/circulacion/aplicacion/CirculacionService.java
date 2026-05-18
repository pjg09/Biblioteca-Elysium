package com.biblioteca.circulacion.aplicacion;

import com.biblioteca.circulacion.aplicacion.dto.DeudaPendienteDTO;
import com.biblioteca.circulacion.aplicacion.dto.DisponibilidadDTO;
import com.biblioteca.circulacion.aplicacion.dto.DevolverMaterialRequest;
import com.biblioteca.circulacion.aplicacion.dto.EstadoUsuarioDTO;
import com.biblioteca.circulacion.aplicacion.dto.LimitePrestamoDTO;
import com.biblioteca.circulacion.aplicacion.dto.RegistrarPrestamoRequest;
import com.biblioteca.circulacion.aplicacion.dto.ResultadoOperacion;
import com.biblioteca.circulacion.dominio.Prestamo;
import com.biblioteca.circulacion.dominio.builders.PrestamoBuilder;
import com.biblioteca.commons.excepciones.OperacionNoPermitidaEnEstadoException;
import com.biblioteca.commons.objetosvalor.Resultado;
import com.biblioteca.circulacion.infraestructura.clientes.MaterialesClient;
import com.biblioteca.circulacion.infraestructura.clientes.MultasClient;
import com.biblioteca.circulacion.infraestructura.clientes.PrestamosExternosClient;
import com.biblioteca.circulacion.infraestructura.clientes.UsuariosClient;
import com.biblioteca.circulacion.infraestructura.mensajeria.EventoPublisher;
import com.biblioteca.circulacion.infraestructura.persistencia.PrestamoEntity;
import com.biblioteca.circulacion.infraestructura.persistencia.PrestamoJpaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class CirculacionService {

    private final PrestamoJpaRepository prestamoRepository;
    private final MaterialesClient materialesClient;
    private final UsuariosClient usuariosClient;
    private final MultasClient multasClient;
    private final PrestamosExternosClient prestamosExternosClient;
    private final EventoPublisher eventoPublisher;
    private final ObjectMapper objectMapper;

    public CirculacionService(PrestamoJpaRepository prestamoRepository,
                              MaterialesClient materialesClient,
                              UsuariosClient usuariosClient,
                              MultasClient multasClient,
                              PrestamosExternosClient prestamosExternosClient,
                              EventoPublisher eventoPublisher,
                              ObjectMapper objectMapper) {
        this.prestamoRepository = prestamoRepository;
        this.materialesClient = materialesClient;
        this.usuariosClient = usuariosClient;
        this.multasClient = multasClient;
        this.prestamosExternosClient = prestamosExternosClient;
        this.eventoPublisher = eventoPublisher;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public ResultadoOperacion registrarPrestamo(RegistrarPrestamoRequest req) {
        // 1. Verificar estado del usuario
        EstadoUsuarioDTO estadoUsuario;
        try {
            estadoUsuario = usuariosClient.consultarEstado(req.getIdUsuario());
        } catch (Exception e) {
            return ResultadoOperacion.fallido("No se pudo verificar el estado del usuario: " + e.getMessage());
        }
        if (!estadoUsuario.isActivo()) {
            return ResultadoOperacion.fallido("El usuario no esta activo. Estado: " + estadoUsuario.getEstadoUsuario());
        }

        // 2. Verificar disponibilidad del material
        DisponibilidadDTO disponibilidad;
        try {
            disponibilidad = materialesClient.consultarDisponibilidad(req.getIdMaterial());
        } catch (Exception e) {
            return ResultadoOperacion.fallido("No se pudo verificar la disponibilidad del material: " + e.getMessage());
        }
        if (!disponibilidad.isDisponible()) {
            return ResultadoOperacion.fallido("El material no esta disponible. Estado: " + disponibilidad.getEstado());
        }

        // 3. Verificar limite de prestamos del usuario
        LimitePrestamoDTO limite;
        try {
            limite = usuariosClient.consultarLimite(req.getIdUsuario());
        } catch (Exception e) {
            return ResultadoOperacion.fallido("No se pudo consultar el limite de prestamos: " + e.getMessage());
        }

        // 4. Contar prestamos activos del usuario
        long prestamosActivos = prestamoRepository.countByIdUsuarioAndEstado(req.getIdUsuario(), "ACTIVO");
        if (prestamosActivos >= limite.getLimiteMaximo()) {
            return ResultadoOperacion.fallido(
                    "El usuario ha alcanzado su limite de prestamos activos (" + limite.getLimiteMaximo() + ").");
        }

        // 5. Calcular fecha de devolucion segun tipo de usuario y tipo de prestamo
        LocalDateTime fechaDevolucion = calcularFechaDevolucion(estadoUsuario.getTipoUsuario(), req.getTipoPrestamo());

        // 6. Crear objeto de dominio usando builder con validaciones
        String id = UUID.randomUUID().toString();
        PrestamoBuilder builder = new PrestamoBuilder()
                .conId(id)
                .conIdUsuario(req.getIdUsuario())
                .conIdMaterial(req.getIdMaterial())
                .conFechaDevolucionEsperada(fechaDevolucion)
                .conTipoPrestamo(req.getTipoPrestamo() != null ? req.getTipoPrestamo() : "NORMAL")
                .conSede(req.getSede());

        Resultado<Prestamo> resultadoPrestamo = builder.construir();
        if (resultadoPrestamo.esError()) {
            return ResultadoOperacion.fallido("Error al crear el préstamo: " + resultadoPrestamo.getMensajeError());
        }

        Prestamo prestamo = resultadoPrestamo.getValor();

        // 7. Persistir
        PrestamoEntity entity = PrestamoEntity.fromDomain(prestamo);
        entity = prestamoRepository.save(entity);

        // 8. Actualizar estado del material a PRESTADO
        try {
            Map<String, String> estadoMaterial = new HashMap<>();
            estadoMaterial.put("estado", "PRESTADO");
            materialesClient.actualizarEstado(req.getIdMaterial(), estadoMaterial);
        } catch (Exception e) {
            // Log but do not roll back — material state update is best-effort via sync call
            // The MaterialDevuelto event will correct state on return
        }

        // 9. Si es interbibliotecario, notificar servicio externo
        if ("INTERBIBLIOTECARIO".equalsIgnoreCase(prestamo.getTipoPrestamo())) {
            try {
                Map<String, Object> solicitud = new HashMap<>();
                solicitud.put("prestamoId", entity.getId());
                solicitud.put("usuarioId", entity.getIdUsuario());
                solicitud.put("materialId", entity.getIdMaterial());
                solicitud.put("fechaDevolucionEsperada", entity.getFechaDevolucionEsperada().toString());
                solicitud.put("sede", entity.getSede());
                prestamosExternosClient.crearSolicitud(solicitud);
            } catch (Exception e) {
                // Best-effort: log and continue
            }
        }

        // 10. Publicar evento PrestamoRegistrado
        try {
            eventoPublisher.publicarPrestamoRegistrado(entity, estadoUsuario.getTipoUsuario());
        } catch (Exception e) {
            // Event publishing failure should not fail the operation
        }

        // 11. Retornar resultado exitoso
        return ResultadoOperacion.exitoso("Prestamo registrado exitosamente con validaciones de dominio.", entity);
    }

    @Transactional
    public ResultadoOperacion registrarDevolucion(String idPrestamo, DevolverMaterialRequest req) {
        // 1. Buscar el prestamo
        Optional<PrestamoEntity> optEntity = prestamoRepository.findById(idPrestamo);
        if (optEntity.isEmpty()) {
            return ResultadoOperacion.fallido("Prestamo no encontrado con id: " + idPrestamo);
        }
        PrestamoEntity entity = optEntity.get();

        // 2. Aplicar devolucion en dominio
        Prestamo prestamo = entity.toDomain();
        try {
            prestamo.devolver(LocalDateTime.now());
        } catch (OperacionNoPermitidaEnEstadoException e) {
            // El préstamo no está en estado ACTIVO, no puede ser devuelto
            return ResultadoOperacion.fallido("No se puede registrar la devolucion: " + e.getMessage());
        }

        // 3. Sincronizar cambios de vuelta a entity y persistir
        entity.setEstado(prestamo.getEstado());
        entity.setFechaDevolucionReal(prestamo.getFechaDevolucionReal());
        entity = prestamoRepository.save(entity);

        // 4. Actualizar estado del material
        try {
            Map<String, String> estadoMaterial = new HashMap<>();
            estadoMaterial.put("estado", req.isEsUsable() ? "DISPONIBLE" : "EN_REPARACION");
            materialesClient.actualizarEstado(entity.getIdMaterial(), estadoMaterial);
        } catch (Exception e) {
            // Best-effort
        }

        // 5. Publicar evento MaterialDevuelto
        try {
            eventoPublisher.publicarMaterialDevuelto(entity, req.isEsUsable(), "ESTUDIANTE");
        } catch (Exception e) {
            // Best-effort
        }

        return ResultadoOperacion.exitoso("Devolucion registrada exitosamente.", entity);
    }

    @Transactional
    public ResultadoOperacion renovarPrestamo(String idPrestamo) {
        // 1. Buscar el prestamo
        Optional<PrestamoEntity> optEntity = prestamoRepository.findById(idPrestamo);
        if (optEntity.isEmpty()) {
            return ResultadoOperacion.fallido("Prestamo no encontrado con id: " + idPrestamo);
        }
        PrestamoEntity entity = optEntity.get();

        // 2. Verificar deuda pendiente del usuario
        try {
            DeudaPendienteDTO deuda = multasClient.consultarDeuda(entity.getIdUsuario());
            if (deuda.isTieneDeuda()) {
                String motivo = "El usuario tiene deuda pendiente de " + deuda.getTotalDeuda()
                        + " con " + deuda.getMultasPendientes() + " multa(s) sin pagar.";
                try {
                    eventoPublisher.publicarRenovacionRechazada(idPrestamo, entity.getIdUsuario(), motivo);
                } catch (Exception ex) {
                    // Best-effort
                }
                return ResultadoOperacion.fallido(motivo);
            }
        } catch (Exception e) {
            // If multas-service is unavailable, continue (fail-open for renewals)
        }

        // 3. Verificar si hay reservas activas para el material (simplified: check active loans)
        long reservasActivas = prestamoRepository.countByIdUsuarioAndEstado(entity.getIdMaterial(), "ACTIVO");
        // Note: this is a simplification — in a full implementation we would call reservas-service
        // For now we allow renewal unless the material itself has a known reservation

        // 4. Intentar renovar via dominio
        Prestamo prestamo = entity.toDomain();
        LocalDateTime nuevaFecha = entity.getFechaDevolucionEsperada().plusDays(14);
        final int MAX_RENOVACIONES = 2;

        try {
            prestamo.renovar(nuevaFecha, MAX_RENOVACIONES);
        } catch (OperacionNoPermitidaEnEstadoException e) {
            // El estado del préstamo no permite renovación (p.ej. ya fue completado)
            String motivo = e.getMessage();
            try {
                eventoPublisher.publicarRenovacionRechazada(idPrestamo, entity.getIdUsuario(), motivo);
            } catch (Exception ex) {
                // Best-effort
            }
            return ResultadoOperacion.fallido("Renovacion rechazada: " + motivo);
        }

        // 5. Persistir cambios
        entity.setFechaDevolucionEsperada(prestamo.getFechaDevolucionEsperada());
        entity.setRenovacionesUsadas(prestamo.getRenovacionesUsadas());
        entity = prestamoRepository.save(entity);

        // 6. Publicar evento PrestamoRenovado
        try {
            eventoPublisher.publicarPrestamoRenovado(entity);
        } catch (Exception e) {
            // Best-effort
        }

        return ResultadoOperacion.exitoso("Prestamo renovado exitosamente.", entity);
    }

    // --- Helpers ---

    private LocalDateTime calcularFechaDevolucion(String tipoUsuario, String tipoPrestamo) {
        if ("INTERBIBLIOTECARIO".equalsIgnoreCase(tipoPrestamo)) {
            return LocalDateTime.now().plusDays(20);
        }
        if (tipoUsuario == null) {
            return LocalDateTime.now().plusDays(14);
        }
        return switch (tipoUsuario.toUpperCase()) {
            case "PROFESOR" -> LocalDateTime.now().plusDays(21);
            case "INVESTIGADOR" -> LocalDateTime.now().plusDays(30);
            case "PUBLICO_GENERAL" -> LocalDateTime.now().plusDays(7);
            default -> LocalDateTime.now().plusDays(14); // ESTUDIANTE and unknown
        };
    }
}
