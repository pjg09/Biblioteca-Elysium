package com.biblioteca.servicios.implementaciones;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.biblioteca.dominio.entidades.Material;
import com.biblioteca.dominio.entidades.Prestamo;
import com.biblioteca.dominio.entidades.Reserva;
import com.biblioteca.dominio.entidades.Usuario;
import com.biblioteca.dominio.enumeraciones.EstadoTransaccion;
import com.biblioteca.dominio.enumeraciones.TipoUsuario;
import com.biblioteca.dominio.objetosvalor.Resultado;
import com.biblioteca.dominio.objetosvalor.ResultadoValidacion;
import com.biblioteca.repositorios.IRepositorio;
import com.biblioteca.servicios.interfaces.IPoliticaTiempoService;
import com.biblioteca.servicios.interfaces.IRenovacionService;

/**
 * Implementación del servicio de renovaciones.
 * 
 * Respeta SRP: Solo maneja lógica de renovación de préstamos.
 * Respeta OCP: Límites de renovaciones configurables por tipo de usuario.
 * Respeta DIP: Depende de abstracciones (interfaces).
 */
public class RenovacionService implements IRenovacionService {
    
    private final IRepositorio<Prestamo> repositorioPrestamo;
    private final IRepositorio<Reserva> repositorioReserva;
    private final IRepositorio<Material> repositorioMaterial;
    private final IRepositorio<Usuario> repositorioUsuario;
    private final IPoliticaTiempoService politicaTiempoService;
    private final Map<TipoUsuario, Integer> maximoRenovaciones;

    public RenovacionService(
            IRepositorio<Prestamo> repositorioPrestamo,
            IRepositorio<Reserva> repositorioReserva,
            IRepositorio<Material> repositorioMaterial,
            IRepositorio<Usuario> repositorioUsuario,
            IPoliticaTiempoService politicaTiempoService) {
        
        if (repositorioPrestamo == null || repositorioReserva == null || 
            repositorioMaterial == null || repositorioUsuario == null ||
            politicaTiempoService == null) {
            throw new IllegalArgumentException("Ningún repositorio o servicio puede ser nulo");
        }
        
        this.repositorioPrestamo = repositorioPrestamo;
        this.repositorioReserva = repositorioReserva;
        this.repositorioMaterial = repositorioMaterial;
        this.repositorioUsuario = repositorioUsuario;
        this.politicaTiempoService = politicaTiempoService;
        this.maximoRenovaciones = inicializarMaximoRenovaciones();
    }

    /**
     * Configura el máximo de renovaciones permitidas por tipo de usuario.
     */
    private Map<TipoUsuario, Integer> inicializarMaximoRenovaciones() {
        Map<TipoUsuario, Integer> maximos = new HashMap<>();
        maximos.put(TipoUsuario.ESTUDIANTE, 2);      // 2 renovaciones
        maximos.put(TipoUsuario.PROFESOR, 3);        // 3 renovaciones
        maximos.put(TipoUsuario.INVESTIGADOR, 4);    // 4 renovaciones
        maximos.put(TipoUsuario.PUBLICO_GENERAL, 1); // 1 renovación
        return maximos;
    }

    @Override
    public ResultadoValidacion validarRenovacion(String idPrestamo) {
        List<String> errores = new ArrayList<>();

        // VALIDACIÓN 1: El préstamo existe
        Prestamo prestamo = repositorioPrestamo.obtenerPorId(idPrestamo);
        
        if (prestamo == null) {
            errores.add("El préstamo con ID '" + idPrestamo + "' no existe");
            return ResultadoValidacion.Invalido(errores);
        }

        // VALIDACIÓN 2: El préstamo está activo
        if (prestamo.getEstado() != EstadoTransaccion.ACTIVA) {
            errores.add("El préstamo no está activo. Estado actual: " + prestamo.getEstado());
            return ResultadoValidacion.Invalido(errores);
        }

        // VALIDACIÓN 3: Obtener el usuario
        Usuario usuario = repositorioUsuario.obtenerPorId(prestamo.getIdUsuario());
        
        if (usuario == null) {
            errores.add("Usuario asociado al préstamo no encontrado");
            return ResultadoValidacion.Invalido(errores);
        }

        // VALIDACIÓN 4: No ha excedido el máximo de renovaciones
        int maximoPermitido = maximoRenovaciones.getOrDefault(usuario.getTipo(), 2);
        int renovacionesUsadas = prestamo.getRenovacionesUsadas();

        if (renovacionesUsadas >= maximoPermitido) {
            errores.add("Ha excedido el número máximo de renovaciones permitidas");
            errores.add("Tipo de usuario: " + usuario.getTipo());
            errores.add("Máximo permitido: " + maximoPermitido);
            errores.add("Renovaciones ya usadas: " + renovacionesUsadas);
            return ResultadoValidacion.Invalido(errores);
        }

        // VALIDACIÓN 5: No hay reservas pendientes sobre el material
        boolean hayReservas = repositorioReserva.obtenerTodos().stream()
                .anyMatch(r -> r.getIdMaterial().equals(prestamo.getIdMaterial()) && 
                              r.getEstado() == EstadoTransaccion.ACTIVA);

        if (hayReservas) {
            errores.add("No se puede renovar: hay reservas pendientes sobre este material");
            return ResultadoValidacion.Invalido(errores);
        }

        // VALIDACIÓN 6: El préstamo no está vencido por mucho tiempo (opcional, pero buena práctica)
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime fechaDevolucionEsperada = prestamo.getFechaDevolucionEsperada();
        
        if (fechaDevolucionEsperada != null && ahora.isAfter(fechaDevolucionEsperada.plusDays(7))) {
            errores.add("El préstamo está vencido hace más de 7 días. Debe devolver el material primero");
            return ResultadoValidacion.Invalido(errores);
        }

        // Todas las validaciones pasaron
        return ResultadoValidacion.Valido();
    }

    @Override
    public Resultado renovarPrestamo(String idPrestamo) {
        try {
            // PASO 1: Validar que se puede renovar
            ResultadoValidacion validacion = validarRenovacion(idPrestamo);
            
            if (!validacion.esValido()) {
                return Resultado.Fallido(
                    "No se puede renovar el préstamo: " + 
                    String.join(", ", validacion.getErrores())
                );
            }

            // PASO 2: Obtener el préstamo
            Prestamo prestamo = repositorioPrestamo.obtenerPorId(idPrestamo);
            
            if (prestamo == null) {
                return Resultado.Fallido("Préstamo no encontrado");
            }

            // PASO 3: Obtener datos necesarios para calcular nueva fecha
            Material material = repositorioMaterial.obtenerPorId(prestamo.getIdMaterial());
            Usuario usuario = repositorioUsuario.obtenerPorId(prestamo.getIdUsuario());

            if (material == null || usuario == null) {
                return Resultado.Fallido("No se encontraron los datos del material o usuario");
            }

            // PASO 4: Calcular nueva fecha de devolución desde HOY
            LocalDateTime fechaActual = LocalDateTime.now();
            LocalDateTime nuevaFechaDevolucion = politicaTiempoService.obtenerFechaDevolucion(
                fechaActual,
                material.getTipo(),
                usuario.getTipo()
            );

            // PASO 5: Actualizar el préstamo
            prestamo.setFechaDevolucionEsperada(nuevaFechaDevolucion);
            prestamo.incrementarRenovaciones();

            // PASO 6: Guardar cambios
            Resultado resultadoActualizacion = repositorioPrestamo.actualizar(prestamo);
            
            if (!resultadoActualizacion.getExito()) {
                return Resultado.Fallido("Error al actualizar el préstamo: " + resultadoActualizacion.getMensaje());
            }

            // PASO 7: Mensaje de éxito
            int renovacionesRestantes = maximoRenovaciones.getOrDefault(usuario.getTipo(), 2) - prestamo.getRenovacionesUsadas();
            
            String mensaje = String.format(
                "Préstamo renovado exitosamente. Nueva fecha de devolución: %s. Renovaciones restantes: %d",
                nuevaFechaDevolucion.toLocalDate(),
                renovacionesRestantes
            );

            return Resultado.Exitoso(mensaje, prestamo);
        } catch (Exception e) {
            return Resultado.Fallido("Error inesperado al renovar préstamo: " + e.getMessage());
        }
    }

    @Override
    public int obtenerRenovacionesDisponibles(String idPrestamo, TipoUsuario tipoUsuario) {
        Prestamo prestamo = repositorioPrestamo.obtenerPorId(idPrestamo);
        
        if (prestamo == null) {
            return 0;
        }

        int maximoPermitido = maximoRenovaciones.getOrDefault(tipoUsuario, 2);
        int renovacionesUsadas = prestamo.getRenovacionesUsadas();
        
        int disponibles = maximoPermitido - renovacionesUsadas;
        
        return Math.max(0, disponibles); // No retornar números negativos
    }

    /**
     * Permite configurar un límite personalizado de renovaciones para un tipo de usuario.
     * Útil para casos especiales o configuraciones dinámicas.
     */
    public void configurarLimiteRenovaciones(TipoUsuario tipoUsuario, int limite) {
        if (limite < 0) {
            throw new IllegalArgumentException("El límite de renovaciones no puede ser negativo");
        }
        maximoRenovaciones.put(tipoUsuario, limite);
    }

    /**
     * Obtiene el máximo de renovaciones configurado para un tipo de usuario.
     */
    public int obtenerMaximoRenovaciones(TipoUsuario tipoUsuario) {
        return maximoRenovaciones.getOrDefault(tipoUsuario, 2);
    }
}