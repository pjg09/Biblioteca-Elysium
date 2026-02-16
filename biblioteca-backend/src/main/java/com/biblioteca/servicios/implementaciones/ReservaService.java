package com.biblioteca.servicios.implementaciones;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.biblioteca.dominio.entidades.Material;
import com.biblioteca.dominio.entidades.Reserva;
import com.biblioteca.dominio.entidades.ReservaInterbibliotecaria;
import com.biblioteca.dominio.entidades.ReservaNormal;
import com.biblioteca.dominio.enumeraciones.EstadoMaterial;
import com.biblioteca.dominio.enumeraciones.EstadoTransaccion;
import com.biblioteca.dominio.objetosvalor.Resultado;
import com.biblioteca.dominio.objetosvalor.ResultadoValidacion;
import com.biblioteca.repositorios.IRepositorio;
import com.biblioteca.servicios.interfaces.IDisponibilidadService;
import com.biblioteca.servicios.interfaces.INotificacionService;
import com.biblioteca.servicios.interfaces.IReservaService;
import com.biblioteca.servicios.interfaces.IValidadorReglasService;

public class ReservaService implements IReservaService {
    
    private final IRepositorio<Reserva> repoReserva;
    private final IRepositorio<Material> repoMaterial;
    private final IDisponibilidadService disponibilidad;
    private final IValidadorReglasService validador;
    private final INotificacionService notificador;
    
    public ReservaService(
            IRepositorio<Reserva> repoReserva,
            IRepositorio<Material> repoMaterial,
            IDisponibilidadService disponibilidad,
            IValidadorReglasService validador,
            INotificacionService notificador) {
        
        this.repoReserva = repoReserva;
        this.repoMaterial = repoMaterial;
        this.disponibilidad = disponibilidad;
        this.validador = validador;
        this.notificador = notificador;
    }
    
    @Override
    public Resultado crearReserva(String idUsuario, String idMaterial, String tipoReserva) {
        try {
            ResultadoValidacion validacion = validador.validarReserva(idUsuario, idMaterial);
            if (!validacion.esValido()) {
                return Resultado.Fallido(validacion.getErrores().get(0));
            }
            
            if (tieneReservaActiva(idUsuario, idMaterial)) {
                return Resultado.Fallido("El usuario ya tiene una reserva activa para este material");
            }
            
            Material material = repoMaterial.obtenerPorId(idMaterial);
            Reserva reserva = crearReservaSegunTipo(idUsuario, idMaterial, tipoReserva);
            
            int posicion = calcularPosicionCola(idMaterial);
            reserva.setPosicionCola(posicion);
            
            Resultado resultado = repoReserva.agregar(reserva);
            
            if (resultado.getExito()) {
                if (material.getEstado() == EstadoMaterial.DISPONIBLE) {
                    material.setEstado(EstadoMaterial.RESERVADO);
                    repoMaterial.actualizar(material);
                }
                
                String mensaje = "Reserva creada exitosamente. Posición en cola: " + posicion;
                notificador.enviarNotificacion(idUsuario, mensaje);
            }
            
            return resultado;
            
        } catch (Exception e) {
            return Resultado.Fallido("Error al crear reserva: " + e.getMessage());
        }
    }
    
    @Override
    public Resultado cancelarReserva(String idReserva) {
        try {
            Reserva reserva = repoReserva.obtenerPorId(idReserva);
            if (reserva == null) {
                return Resultado.Fallido("Reserva no encontrada");
            }
            
            if (reserva.getEstado() != EstadoTransaccion.ACTIVA) {
                return Resultado.Fallido("La reserva no está activa");
            }
            
            reserva.setEstado(EstadoTransaccion.CANCELADA);
            Resultado resultado = repoReserva.actualizar(reserva);
            
            if (resultado.getExito()) {
                notificador.enviarNotificacion(
                    reserva.getIdUsuario(),
                    "Su reserva ha sido cancelada exitosamente."
                );
                actualizarPosicionesCola(reserva.getIdMaterial());
                actualizarEstadoMaterial(reserva.getIdMaterial());
            }
            
            return resultado;
            
        } catch (Exception e) {
            return Resultado.Fallido("Error al cancelar reserva: " + e.getMessage());
        }
    }
    
    @Override
    public Resultado notificarDisponibilidad(String idReserva) {
        try {
            Reserva reserva = repoReserva.obtenerPorId(idReserva);
            if (reserva == null) {
                return Resultado.Fallido("Reserva no encontrada");
            }
            
            reserva.setFechaNotificacion(LocalDateTime.now());
            repoReserva.actualizar(reserva);
            
            Material material = repoMaterial.obtenerPorId(reserva.getIdMaterial());
            String mensaje = "El material " + (material != null ? material.getTitulo() : "") + 
                            " ya está disponible. Tiene 3 días para recogerlo.";
            
            return notificador.enviarNotificacion(reserva.getIdUsuario(), mensaje);
            
        } catch (Exception e) {
            return Resultado.Fallido("Error al notificar disponibilidad: " + e.getMessage());
        }
    }
    
    public List<Reserva> obtenerReservasActivasPorUsuario(String idUsuario) {
        LocalDateTime ahora = LocalDateTime.now();
        return repoReserva.obtenerTodos().stream()
            .filter(r -> r.getIdUsuario().equals(idUsuario))
            .filter(r -> r.getEstado() == EstadoTransaccion.ACTIVA)
            .filter(r -> r.getFechaExpiracion().isAfter(ahora))
            .collect(Collectors.toList());
    }
    
    public List<Reserva> obtenerReservasActivasPorMaterial(String idMaterial) {
        LocalDateTime ahora = LocalDateTime.now();
        return repoReserva.obtenerTodos().stream()
            .filter(r -> r.getIdMaterial().equals(idMaterial))
            .filter(r -> r.getEstado() == EstadoTransaccion.ACTIVA)
            .filter(r -> r.getFechaExpiracion().isAfter(ahora))
            .sorted((r1, r2) -> Integer.compare(r1.getPosicionCola(), r2.getPosicionCola()))
            .collect(Collectors.toList());
    }
    
    public Reserva obtenerSiguienteEnCola(String idMaterial) {
        List<Reserva> activas = obtenerReservasActivasPorMaterial(idMaterial);
        return activas.isEmpty() ? null : activas.get(0);
    }
    
    public void limpiarReservasExpiradas() {
        LocalDateTime ahora = LocalDateTime.now();
        List<Reserva> expiradas = repoReserva.obtenerTodos().stream()
            .filter(r -> r.getEstado() == EstadoTransaccion.ACTIVA)
            .filter(r -> r.getFechaExpiracion().isBefore(ahora))
            .collect(Collectors.toList());
        
        for (Reserva reserva : expiradas) {
            reserva.setEstado(EstadoTransaccion.CANCELADA);
            repoReserva.actualizar(reserva);
            notificador.enviarNotificacion(
                reserva.getIdUsuario(),
                "Su reserva ha expirado por falta de recogida."
            );
        }
        
        expiradas.stream()
            .map(Reserva::getIdMaterial)
            .distinct()
            .forEach(this::actualizarEstadoMaterial);
    }
    
    private Reserva crearReservaSegunTipo(String idUsuario, String idMaterial, String tipoReserva) {
        if ("INTERBIBLIOTECARIA".equalsIgnoreCase(tipoReserva)) {
            return new ReservaInterbibliotecaria(idUsuario, idMaterial, "Biblioteca Central");
        } else {
            return new ReservaNormal(idUsuario, idMaterial, "Sala de lectura");
        }
    }
    
    private int calcularPosicionCola(String idMaterial) {
        LocalDateTime ahora = LocalDateTime.now();
        return (int) repoReserva.obtenerTodos().stream()
            .filter(r -> r.getIdMaterial().equals(idMaterial))
            .filter(r -> r.getEstado() == EstadoTransaccion.ACTIVA)
            .filter(r -> r.getFechaExpiracion().isAfter(ahora))
            .count() + 1;
    }
    
    private void actualizarPosicionesCola(String idMaterial) {
        List<Reserva> activas = obtenerReservasActivasPorMaterial(idMaterial);
        for (int i = 0; i < activas.size(); i++) {
            activas.get(i).setPosicionCola(i + 1);
            repoReserva.actualizar(activas.get(i));
        }
    }
    
    private void actualizarEstadoMaterial(String idMaterial) {
        List<Reserva> activas = obtenerReservasActivasPorMaterial(idMaterial);
        Material material = repoMaterial.obtenerPorId(idMaterial);
        
        if (material != null) {
            if (activas.isEmpty() && material.getEstado() != EstadoMaterial.PRESTADO) {
                material.setEstado(EstadoMaterial.DISPONIBLE);
                repoMaterial.actualizar(material);
            } else if (!activas.isEmpty() && material.getEstado() == EstadoMaterial.DISPONIBLE) {
                material.setEstado(EstadoMaterial.RESERVADO);
                repoMaterial.actualizar(material);
            }
        }
    }
    
    private boolean tieneReservaActiva(String idUsuario, String idMaterial) {
        LocalDateTime ahora = LocalDateTime.now();
        return repoReserva.obtenerTodos().stream()
            .anyMatch(r -> r.getIdUsuario().equals(idUsuario) &&
                          r.getIdMaterial().equals(idMaterial) &&
                          r.getEstado() == EstadoTransaccion.ACTIVA &&
                          r.getFechaExpiracion().isAfter(ahora));
    }
}