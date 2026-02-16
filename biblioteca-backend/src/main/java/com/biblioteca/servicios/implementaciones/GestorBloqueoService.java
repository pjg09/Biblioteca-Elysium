package com.biblioteca.servicios.implementaciones;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.biblioteca.dominio.entidades.Multa;
import com.biblioteca.dominio.entidades.Prestamo;
import com.biblioteca.dominio.entidades.Usuario;
import com.biblioteca.dominio.enumeraciones.EstadoMulta;
import com.biblioteca.dominio.enumeraciones.EstadoTransaccion;
import com.biblioteca.dominio.enumeraciones.EstadoUsuario;
import com.biblioteca.dominio.objetosvalor.Resultado;
import com.biblioteca.dominio.objetosvalor.ResultadoValidacion;
import com.biblioteca.repositorios.IRepositorio;
import com.biblioteca.servicios.interfaces.IGestorBloqueoService;

/**
 * Implementación del servicio de gestión de bloqueos.
 * 
 * Decide cuándo bloquear/desbloquear usuarios según reglas de negocio.
 * 
 * Respeta SRP: Solo maneja lógica de bloqueos.
 * Respeta OCP: Umbrales y reglas configurables.
 * Respeta DIP: Depende de abstracciones (IRepositorio).
 */
public class GestorBloqueoService implements IGestorBloqueoService {
    
    private final IRepositorio<Usuario> repositorioUsuario;
    private final IRepositorio<Multa> repositorioMulta;
    private final IRepositorio<Prestamo> repositorioPrestamo;
    
    // Configuraciones de umbrales
    private final BigDecimal UMBRAL_MONTO_MULTA;        // Monto máximo de multas antes de bloquear
    private final int UMBRAL_DIAS_VENCIMIENTO;          // Días de vencimiento antes de bloquear
    private final int UMBRAL_CANTIDAD_MULTAS;           // Número de multas pendientes antes de bloquear

    public GestorBloqueoService(
            IRepositorio<Usuario> repositorioUsuario,
            IRepositorio<Multa> repositorioMulta,
            IRepositorio<Prestamo> repositorioPrestamo) {
        
        this(repositorioUsuario, repositorioMulta, repositorioPrestamo, 
             new BigDecimal("50000"), 30, 3); // Valores por defecto
    }

    public GestorBloqueoService(
            IRepositorio<Usuario> repositorioUsuario,
            IRepositorio<Multa> repositorioMulta,
            IRepositorio<Prestamo> repositorioPrestamo,
            BigDecimal umbralMontoMulta,
            int umbralDiasVencimiento,
            int umbralCantidadMultas) {
        
        if (repositorioUsuario == null || repositorioMulta == null || repositorioPrestamo == null) {
            throw new IllegalArgumentException("Ningún repositorio puede ser nulo");
        }
        
        this.repositorioUsuario = repositorioUsuario;
        this.repositorioMulta = repositorioMulta;
        this.repositorioPrestamo = repositorioPrestamo;
        this.UMBRAL_MONTO_MULTA = umbralMontoMulta;
        this.UMBRAL_DIAS_VENCIMIENTO = umbralDiasVencimiento;
        this.UMBRAL_CANTIDAD_MULTAS = umbralCantidadMultas;
    }

    @Override
    public ResultadoValidacion verificarSiDebeBloquear(String idUsuario) {
        Usuario usuario = repositorioUsuario.obtenerPorId(idUsuario);
        
        if (usuario == null) {
            return ResultadoValidacion.Invalido(
                List.of("El usuario con ID '" + idUsuario + "' no existe")
            );
        }

        List<String> motivosBloqueo = new ArrayList<>();

        // CRITERIO 1: Multas pendientes exceden el umbral de monto
        BigDecimal totalMultasPendientes = calcularTotalMultasPendientes(idUsuario);
        
        if (totalMultasPendientes.compareTo(UMBRAL_MONTO_MULTA) > 0) {
            motivosBloqueo.add(
                String.format("Multas pendientes ($%s) exceden el umbral permitido ($%s)",
                    totalMultasPendientes, UMBRAL_MONTO_MULTA)
            );
        }

        // CRITERIO 2: Número de multas pendientes excede el umbral
        long cantidadMultasPendientes = contarMultasPendientes(idUsuario);
        
        if (cantidadMultasPendientes >= UMBRAL_CANTIDAD_MULTAS) {
            motivosBloqueo.add(
                String.format("Tiene %d multas pendientes (límite: %d)",
                    cantidadMultasPendientes, UMBRAL_CANTIDAD_MULTAS)
            );
        }

        // CRITERIO 3: Tiene multas por pérdida de material
        boolean tieneMultaPorPerdida = tieneMultaPorPerdidaPendiente(idUsuario);
        
        if (tieneMultaPorPerdida) {
            motivosBloqueo.add("Tiene materiales reportados como perdidos sin resolver");
        }

        // CRITERIO 4: Tiene préstamos muy vencidos
        List<Prestamo> prestamosVencidos = obtenerPrestamosVencidos(idUsuario, UMBRAL_DIAS_VENCIMIENTO);
        
        if (!prestamosVencidos.isEmpty()) {
            motivosBloqueo.add(
                String.format("Tiene %d préstamo(s) vencido(s) hace más de %d días",
                    prestamosVencidos.size(), UMBRAL_DIAS_VENCIMIENTO)
            );
        }

        // Si hay motivos de bloqueo, retornar inválido
        if (!motivosBloqueo.isEmpty()) {
            return ResultadoValidacion.Invalido(motivosBloqueo);
        }

        return ResultadoValidacion.Valido();
    }

    @Override
    public Resultado bloquearUsuario(String idUsuario, String motivo) {
        try {
            Usuario usuario = repositorioUsuario.obtenerPorId(idUsuario);
            
            if (usuario == null) {
                return Resultado.Fallido("Usuario no encontrado");
            }

            // Determinar tipo de bloqueo según el motivo
            EstadoUsuario nuevoEstado = determinarTipoBloqueo(idUsuario, motivo);

            // Ya está bloqueado
            if (usuario.getEstado() == nuevoEstado) {
                return Resultado.Exitoso("El usuario ya estaba bloqueado", usuario);
            }

            // Bloquear
            usuario.setEstado(nuevoEstado);
            Resultado resultadoActualizacion = repositorioUsuario.actualizar(usuario);
            
            if (!resultadoActualizacion.getExito()) {
                return Resultado.Fallido("Error al bloquear usuario: " + resultadoActualizacion.getMensaje());
            }

            String mensaje = String.format(
                "Usuario bloqueado exitosamente. Estado: %s. Motivo: %s",
                nuevoEstado, motivo
            );

            return Resultado.Exitoso(mensaje, usuario);

        } catch (Exception e) {
            return Resultado.Fallido("Error inesperado al bloquear usuario: " + e.getMessage());
        }
    }

    @Override
    public Resultado desbloquearUsuario(String idUsuario) {
        try {
            Usuario usuario = repositorioUsuario.obtenerPorId(idUsuario);
            
            if (usuario == null) {
                return Resultado.Fallido("Usuario no encontrado");
            }

            // Ya está activo
            if (usuario.getEstado() == EstadoUsuario.ACTIVO) {
                return Resultado.Exitoso("El usuario ya estaba activo", usuario);
            }

            // Verificar si aún debe estar bloqueado
            ResultadoValidacion verificacion = verificarSiDebeBloquear(idUsuario);
            
            if (!verificacion.esValido()) {
                return Resultado.Fallido(
                    "No se puede desbloquear: " + String.join(", ", verificacion.getErrores())
                );
            }

            // Desbloquear
            usuario.setEstado(EstadoUsuario.ACTIVO);
            Resultado resultadoActualizacion = repositorioUsuario.actualizar(usuario);
            
            if (!resultadoActualizacion.getExito()) {
                return Resultado.Fallido("Error al desbloquear usuario: " + resultadoActualizacion.getMensaje());
            }

            return Resultado.Exitoso("Usuario desbloqueado exitosamente", usuario);

        } catch (Exception e) {
            return Resultado.Fallido("Error inesperado al desbloquear usuario: " + e.getMessage());
        }
    }

    @Override
    public Resultado verificarYBloquearSiNecesario(String idUsuario) {
        ResultadoValidacion verificacion = verificarSiDebeBloquear(idUsuario);
        
        if (!verificacion.esValido()) {
            // Debe bloquearse
            String motivos = String.join("; ", verificacion.getErrores());
            return bloquearUsuario(idUsuario, motivos);
        }

        return Resultado.Exitoso("El usuario no requiere bloqueo", null);
    }

    // ==================== MÉTODOS AUXILIARES ====================

    /**
     * Calcula el monto total de multas pendientes de un usuario.
     */
    private BigDecimal calcularTotalMultasPendientes(String idUsuario) {
        return repositorioMulta.obtenerTodos().stream()
                .filter(m -> m.getIdUsuario().equals(idUsuario))
                .filter(m -> m.getEstado() == EstadoMulta.PENDIENTE)
                .map(m -> BigDecimal.valueOf(m.calcularMontoTotal()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Cuenta cuántas multas pendientes tiene un usuario.
     */
    private long contarMultasPendientes(String idUsuario) {
        return repositorioMulta.obtenerTodos().stream()
                .filter(m -> m.getIdUsuario().equals(idUsuario))
                .filter(m -> m.getEstado() == EstadoMulta.PENDIENTE)
                .count();
    }

    /**
     * Verifica si el usuario tiene multas por pérdida pendientes.
     */
    private boolean tieneMultaPorPerdidaPendiente(String idUsuario) {
        return repositorioMulta.obtenerTodos().stream()
                .filter(m -> m.getIdUsuario().equals(idUsuario))
                .filter(m -> m.getEstado() == EstadoMulta.PENDIENTE)
                .anyMatch(m -> m instanceof com.biblioteca.dominio.entidades.MultaPorPerdida);
    }

    /**
     * Obtiene préstamos activos vencidos hace más de X días.
     */
    private List<Prestamo> obtenerPrestamosVencidos(String idUsuario, int diasUmbral) {
        LocalDateTime fechaLimite = LocalDateTime.now().minusDays(diasUmbral);
        
        return repositorioPrestamo.obtenerTodos().stream()
                .filter(p -> p.getIdUsuario().equals(idUsuario))
                .filter(p -> p.getEstado() == EstadoTransaccion.ACTIVA)
                .filter(p -> p.getFechaDevolucionEsperada() != null)
                .filter(p -> p.getFechaDevolucionEsperada().isBefore(fechaLimite))
                .collect(Collectors.toList());
    }

    /**
     * Determina el tipo de bloqueo según el motivo.
     */
    private EstadoUsuario determinarTipoBloqueo(String idUsuario, String motivo) {
        // Si tiene multa por pérdida, bloqueo por pérdida
        if (tieneMultaPorPerdidaPendiente(idUsuario)) {
            return EstadoUsuario.BLOQUEADO_PERDIDA;
        }
        
        // Si el motivo menciona multas o el análisis detecta multas, bloqueo por multa
        if (motivo.toLowerCase().contains("multa") || 
            calcularTotalMultasPendientes(idUsuario).compareTo(BigDecimal.ZERO) > 0) {
            return EstadoUsuario.BLOQUEADO_MULTA;
        }
        
        // Por defecto, suspensión
        return EstadoUsuario.SUSPENDIDO;
    }

    /**
     * Obtiene información detallada sobre el estado de bloqueo de un usuario.
     * Útil para dashboards o reportes.
     */
    public String obtenerDetalleEstadoUsuario(String idUsuario) {
        Usuario usuario = repositorioUsuario.obtenerPorId(idUsuario);
        
        if (usuario == null) {
            return "Usuario no encontrado";
        }

        StringBuilder detalle = new StringBuilder();
        detalle.append("Estado actual: ").append(usuario.getEstado()).append("\n");
        
        BigDecimal totalMultas = calcularTotalMultasPendientes(idUsuario);
        long cantidadMultas = contarMultasPendientes(idUsuario);
        
        detalle.append("Multas pendientes: ").append(cantidadMultas).append("\n");
        detalle.append("Monto total multas: $").append(totalMultas).append("\n");
        
        List<Prestamo> prestamosVencidos = obtenerPrestamosVencidos(idUsuario, UMBRAL_DIAS_VENCIMIENTO);
        detalle.append("Préstamos vencidos (>").append(UMBRAL_DIAS_VENCIMIENTO).append(" días): ")
               .append(prestamosVencidos.size()).append("\n");
        
        return detalle.toString();
    }

    /**
     * Getters para los umbrales (útil para testing o consultas).
     */
    public BigDecimal getUmbralMontoMulta() {
        return UMBRAL_MONTO_MULTA;
    }

    public int getUmbralDiasVencimiento() {
        return UMBRAL_DIAS_VENCIMIENTO;
    }

    public int getUmbralCantidadMultas() {
        return UMBRAL_CANTIDAD_MULTAS;
    }
}