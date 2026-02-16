package com.biblioteca.servicios.implementaciones;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.biblioteca.dominio.entidades.Material;
import com.biblioteca.dominio.entidades.Prestamo;
import com.biblioteca.dominio.entidades.PrestamoInterbibliotecario;
import com.biblioteca.dominio.entidades.PrestamoNormal;
import com.biblioteca.dominio.entidades.Usuario;
import com.biblioteca.dominio.enumeraciones.EstadoMaterial;
import com.biblioteca.dominio.enumeraciones.EstadoTransaccion;
import com.biblioteca.dominio.objetosvalor.Resultado;
import com.biblioteca.dominio.objetosvalor.ResultadoValidacion;
import com.biblioteca.repositorios.IRepositorio;
import com.biblioteca.servicios.interfaces.IDisponibilidadService;
import com.biblioteca.servicios.interfaces.INotificacionService;
import com.biblioteca.servicios.interfaces.IPoliticaTiempoService;
import com.biblioteca.servicios.interfaces.IPrestamoService;
import com.biblioteca.servicios.interfaces.IValidadorReglasService;

public class PrestamoService implements IPrestamoService {
    
    private final IValidadorReglasService validadorReglas;
    private final IDisponibilidadService disponibilidadService;
    private final IPoliticaTiempoService politicaTiempoService;
    private final IRepositorio<Prestamo> repositorioPrestamo;
    private final IRepositorio<Material> repositorioMaterial;
    private final IRepositorio<Usuario> repositorioUsuario;
    private final INotificacionService notificacionService;

    public PrestamoService(
            IValidadorReglasService validadorReglas,
            IDisponibilidadService disponibilidadService,
            IPoliticaTiempoService politicaTiempoService,
            IRepositorio<Prestamo> repositorioPrestamo,
            IRepositorio<Material> repositorioMaterial,
            IRepositorio<Usuario> repositorioUsuario,
            INotificacionService notificacionService) {
        
        if (validadorReglas == null || disponibilidadService == null || 
            politicaTiempoService == null || repositorioPrestamo == null ||
            repositorioMaterial == null || repositorioUsuario == null ||
            notificacionService == null) {
            throw new IllegalArgumentException("Ningún servicio o repositorio puede ser nulo");
        }
        
        this.validadorReglas = validadorReglas;
        this.disponibilidadService = disponibilidadService;
        this.politicaTiempoService = politicaTiempoService;
        this.repositorioPrestamo = repositorioPrestamo;
        this.repositorioMaterial = repositorioMaterial;
        this.repositorioUsuario = repositorioUsuario;
        this.notificacionService = notificacionService;
    }

    @Override
    public Resultado registrarPrestamo(String idUsuario, String idMaterial, String tipoPrestamo) {
        try {
            // PASO 1: Validar todas las reglas de negocio
            ResultadoValidacion validacion = validadorReglas.validarPrestamo(idUsuario, idMaterial);
            
            if (!validacion.esValido()) {
                return Resultado.Fallido(
                    "No se puede realizar el préstamo: " + 
                    String.join(", ", validacion.getErrores())
                );
            }

            // PASO 2: Obtener entidades necesarias
            Usuario usuario = repositorioUsuario.obtenerPorId(idUsuario);
            Material material = repositorioMaterial.obtenerPorId(idMaterial);

            if (usuario == null) {
                return Resultado.Fallido("Usuario no encontrado");
            }
            if (material == null) {
                return Resultado.Fallido("Material no encontrado");
            }

            // PASO 3: Verificar que el material sea prestable según su tipo
            if (!disponibilidadService.materialEsPrestable(idMaterial, material.getTipo())) {
                return Resultado.Fallido(
                    "El material de tipo " + material.getTipo() + " no es prestable"
                );
            }

            // PASO 4: Calcular fecha de devolución esperada
            LocalDateTime fechaPrestamo = LocalDateTime.now();
            LocalDateTime fechaDevolucionEsperada = politicaTiempoService.obtenerFechaDevolucion(
                fechaPrestamo,
                material.getTipo(),
                usuario.getTipo()
            );

            // PASO 5: Crear el préstamo según el tipo solicitado
            Prestamo prestamo = crearPrestamoSegunTipo(
                tipoPrestamo,
                idUsuario,
                idMaterial,
                fechaPrestamo,
                fechaDevolucionEsperada
            );

            // PASO 6: Guardar el préstamo
            Resultado resultadoGuardado = repositorioPrestamo.agregar(prestamo);
            
            if (!resultadoGuardado.getExito()) {
                return Resultado.Fallido("Error al guardar el préstamo: " + resultadoGuardado.getMensaje());
            }

            // PASO 7: Actualizar estado del material a PRESTADO
            material.setEstado(EstadoMaterial.PRESTADO);
            repositorioMaterial.actualizar(material);

            // PASO 8: Notificar al usuario
            notificacionService.enviarNotificacion(
                idUsuario,
                "Préstamo registrado exitosamente. Fecha de devolución: " + 
                fechaDevolucionEsperada.toLocalDate()
            );

            return Resultado.Exitoso(
                "Préstamo registrado exitosamente",
                prestamo
            );
        } catch (Exception e) {
            return Resultado.Fallido("Error inesperado al registrar préstamo: " + e.getMessage());
        }
    }

    @Override
    public List<Prestamo> obtenerPrestamosActivos(String idUsuario) {
        return repositorioPrestamo.obtenerTodos().stream()
                .filter(p -> p.getIdUsuario().equals(idUsuario))
                .filter(p -> p.getEstado() == EstadoTransaccion.ACTIVA)
                .collect(Collectors.toList());
    }

    @Override
    public Prestamo obtenerPrestamoPorId(String idPrestamo) {
        return repositorioPrestamo.obtenerPorId(idPrestamo);
    }

    private Prestamo crearPrestamoSegunTipo(
            String tipoPrestamo,
            String idUsuario,
            String idMaterial,
            LocalDateTime fechaPrestamo,
            LocalDateTime fechaDevolucionEsperada) {
        
        String idPrestamo = UUID.randomUUID().toString();

        switch (tipoPrestamo.toUpperCase()) {
            case "NORMAL":
                return new PrestamoNormal(
                    idUsuario,
                    idMaterial,
                    fechaPrestamo,
                    fechaDevolucionEsperada,
                    "Biblioteca Central" // ubicacionBiblioteca
                );

            case "INTERBIBLIOTECARIO":
                return new PrestamoInterbibliotecario(
                    idUsuario,
                    idMaterial,
                    fechaPrestamo,
                    fechaDevolucionEsperada,
                    "Biblioteca Central",      // bibliotecaOrigen
                    "Biblioteca Secundaria",   // bibliotecaDestino
                    5000.0                     // costoTransferencia
                );

            default:
                throw new IllegalArgumentException(
                    "Tipo de préstamo no válido: " + tipoPrestamo + 
                    ". Valores permitidos: NORMAL, INTERBIBLIOTECARIO"
                );
        }
    }
}