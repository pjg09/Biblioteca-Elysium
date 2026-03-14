package com.biblioteca.servicios.implementaciones;

import java.time.LocalDateTime;

import com.biblioteca.dominio.entidades.Material;
import com.biblioteca.dominio.entidades.Multa;
import com.biblioteca.dominio.entidades.Prestamo;
import com.biblioteca.dominio.entidades.Usuario;
import com.biblioteca.dominio.enumeraciones.EstadoMaterial;
import com.biblioteca.dominio.enumeraciones.EstadoMulta;
import com.biblioteca.dominio.enumeraciones.EstadoTransaccion;
import com.biblioteca.dominio.enumeraciones.TipoMulta;
import com.biblioteca.dominio.objetosvalor.ContextoMulta;
import com.biblioteca.dominio.objetosvalor.Evaluacion;
import com.biblioteca.dominio.objetosvalor.Resultado;
import com.biblioteca.repositorios.IRepositorio;
import com.biblioteca.servicios.interfaces.IDevolucionService;
import com.biblioteca.servicios.interfaces.IGestorBloqueoService;
import com.biblioteca.servicios.interfaces.IInspeccionMaterialService;
import com.biblioteca.servicios.interfaces.INotificacionService;
import com.biblioteca.servicios.interfaces.IReservaService;

public class DevolucionService implements IDevolucionService {
    
    private final IInspeccionMaterialService inspeccion;
    private final GestorMultasService gestorMultas;
    private final IRepositorio<Prestamo> repoPrestamo;
    private final IRepositorio<Material> repoMaterial;
    private final IRepositorio<Usuario> repoUsuario;
    private final IRepositorio<Multa> repoMulta;
    private final IReservaService reservas;
    private final INotificacionService notificador;
    private final IGestorBloqueoService gestorBloqueo;
    
    public DevolucionService(
            IInspeccionMaterialService inspeccion,
            GestorMultasService gestorMultas,
            IRepositorio<Prestamo> repoPrestamo,
            IRepositorio<Material> repoMaterial,
            IRepositorio<Usuario> repoUsuario,
            IRepositorio<Multa> repoMulta,
            IReservaService reservas,
            INotificacionService notificador,
            IGestorBloqueoService gestorBloqueo) {
        
        this.inspeccion = inspeccion;
        this.gestorMultas = gestorMultas;
        this.repoPrestamo = repoPrestamo;
        this.repoMaterial = repoMaterial;
        this.repoUsuario = repoUsuario;
        this.repoMulta = repoMulta;
        this.reservas = reservas;
        this.notificador = notificador;
        this.gestorBloqueo = gestorBloqueo;
    }
    
    @Override
    public Resultado registrarDevolucion(String idPrestamo, Evaluacion evaluacion) {
        try {
            // 1. Obtener el préstamo
            Prestamo prestamo = repoPrestamo.obtenerPorId(idPrestamo);
            if (prestamo == null) {
                return Resultado.Fallido("Préstamo no encontrado");
            }
            
            if (prestamo.getFechaDevolucionReal() != null) {
                return Resultado.Fallido("Este préstamo ya fue devuelto");
            }
            
            // 2. Obtener material y usuario
            Material material = repoMaterial.obtenerPorId(prestamo.getIdMaterial().getValor());
            Usuario usuario = repoUsuario.obtenerPorId(prestamo.getIdUsuario().getValor());
            
            if (material == null) {
                return Resultado.Fallido("Material no encontrado");
            }
            
            // 3. Registrar fecha de devolución
            LocalDateTime fechaDevolucion = LocalDateTime.now();
            prestamo.setFechaDevolucionReal(fechaDevolucion);
            prestamo.setEstado(EstadoTransaccion.COMPLETADA);
            repoPrestamo.actualizar(prestamo);
            
            // 4. Procesar multas
            StringBuilder mensajeMultas = new StringBuilder();
            double totalMultasCalculado = 0; // ✅ Cambio de nombre
            
            // Multa por retraso
            if (fechaDevolucion.isAfter(prestamo.getFechaDevolucionEsperada())) {
                ContextoMulta contextoRetraso = new ContextoMulta.Builder()
                    .conPrestamo(idPrestamo)
                    .conUsuario(usuario.getId().getValor())
                    .conMaterial(material.getId().getValor())
                    .conFechaActual(fechaDevolucion)
                    .deTipo(TipoMulta.POR_RETRASO)
                    .build();
                
                Multa multaRetraso = gestorMultas.calcularMulta(contextoRetraso);
                if (multaRetraso != null) {
                    repoMulta.agregar(multaRetraso);
                    totalMultasCalculado += multaRetraso.calcularMontoTotal();
                    mensajeMultas.append("• Multa por retraso: $")
                                .append(multaRetraso.calcularMontoTotal())
                                .append("\n");
                }
            }
            
            // 5. Inspeccionar material si hay evaluación
            if (evaluacion != null) {
                if (!evaluacion.esUsable() && evaluacion.tieneDanos()) {
                    ContextoMulta contextoDano = new ContextoMulta.Builder()
                        .conPrestamo(idPrestamo)
                        .conUsuario(usuario.getId().getValor())
                        .conMaterial(material.getId().getValor())
                        .conEvaluacion(evaluacion)
                        .deTipo(TipoMulta.POR_DANO)
                        .build();
                    
                    Multa multaDano = gestorMultas.calcularMulta(contextoDano);
                    if (multaDano != null) {
                        repoMulta.agregar(multaDano);
                        totalMultasCalculado += multaDano.calcularMontoTotal();
                        mensajeMultas.append("• Multa por daños: $")
                                    .append(multaDano.calcularMontoTotal())
                                    .append("\n");
                    }
                }
                
                // Actualizar estado del material según evaluación
                if (evaluacion.esUsable()) {
                    material.marcarComoDisponible();
                } else {
                    material.marcarComoEnReparacion("Daños detectados en inspección");
                }
            } else {
                material.marcarComoDisponible();
            }
            
            repoMaterial.actualizar(material);
            
            // 6. Verificar si debe bloquearse por multas
            if (totalMultasCalculado > 0) {
                double totalMultasUsuario = calcularTotalMultasPendientes(usuario.getId().getValor());
                if (totalMultasUsuario >= 50000) {
                    gestorBloqueo.bloquearUsuario(usuario.getId().getValor(), 
                        "Bloqueado por multas pendientes: $" + totalMultasUsuario);
                }
            }
            
            // 7. Notificar al usuario
            String mensajeUsuario = construirMensajeDevolucion(
                material.getTitulo(), 
                fechaDevolucion, 
                mensajeMultas.toString(), 
                totalMultasCalculado // ✅ Usar la variable con otro nombre
            );
            notificador.enviarNotificacion(usuario.getId().getValor(), mensajeUsuario);
            
            // 8. Preparar resultado - usando nombres diferentes para evitar self-reference
            final double totalMultasFinal = totalMultasCalculado; // ✅ Variable final para el objeto anónimo
            final String detalleMultasFinal = mensajeMultas.toString();
            final boolean materialUsableFinal = evaluacion != null ? evaluacion.esUsable() : true;
            
            Object data = new Object() {
                public final Prestamo prestamoDevuelto = prestamo;
                public final double totalMultas = totalMultasFinal; // ✅ Ya no hay conflicto
                public final String detalleMultas = detalleMultasFinal;
                public final boolean materialUsable = materialUsableFinal;
            };
            
            String mensajeExito = totalMultasCalculado > 0 
                ? "Devolución registrada con multas. Total: $" + totalMultasCalculado
                : "Devolución registrada exitosamente. Material en buen estado.";
            
            return Resultado.Exitoso(mensajeExito, data);
            
        } catch (Exception e) {
            return Resultado.Fallido("Error al registrar devolución: " + e.getMessage());
        }
    }
    
    public Resultado registrarDevolucionSimple(String idPrestamo) {
        return registrarDevolucion(idPrestamo, null);
    }
    
    public Resultado registrarDevolucionConInspeccion(String idPrestamo) {
        Prestamo prestamo = repoPrestamo.obtenerPorId(idPrestamo);
        if (prestamo == null) {
            return Resultado.Fallido("Préstamo no encontrado");
        }
        
        Evaluacion evaluacion = inspeccion.inspeccionarMaterial(prestamo.getIdMaterial().getValor());
        return registrarDevolucion(idPrestamo, evaluacion);
    }
    
    private double calcularTotalMultasPendientes(String idUsuario) {
        return repoMulta.obtenerTodos().stream()
            .filter(m -> m.getIdUsuario().equals(idUsuario))
            .filter(m -> m.getEstado() == EstadoMulta.PENDIENTE)
            .mapToDouble(Multa::calcularMontoTotal)
            .sum();
    }
    
    private String construirMensajeDevolucion(
            String tituloMaterial, 
            LocalDateTime fecha, 
            String detalleMultas,
            double totalMultas) {
        
        StringBuilder mensaje = new StringBuilder();
        mensaje.append("📚 Devolución registrada\n");
        mensaje.append("Material: ").append(tituloMaterial).append("\n");
        mensaje.append("Fecha: ").append(fecha.toLocalDate()).append("\n");
        
        if (totalMultas > 0) {
            mensaje.append("\n💰 MULTAS GENERADAS:\n");
            mensaje.append(detalleMultas);
            mensaje.append("TOTAL: $").append(totalMultas).append("\n");
            mensaje.append("⚠️ Debe pagar las multas para evitar bloqueos.");
        } else {
            mensaje.append("\n✅ Material devuelto en buen estado. ¡Gracias!");
        }
        
        return mensaje.toString();
    }
    
    public long contarDevolucionesPorUsuario(String idUsuario) {
        return repoPrestamo.obtenerTodos().stream()
            .filter(p -> p.getIdUsuario().equals(idUsuario))
            .filter(p -> p.getFechaDevolucionReal() != null)
            .count();
    }
    
    public long contarDevolucionesEnRango(LocalDateTime inicio, LocalDateTime fin) {
        return repoPrestamo.obtenerTodos().stream()
            .filter(p -> p.getFechaDevolucionReal() != null)
            .filter(p -> !p.getFechaDevolucionReal().isBefore(inicio))
            .filter(p -> !p.getFechaDevolucionReal().isAfter(fin))
            .count();
    }
}