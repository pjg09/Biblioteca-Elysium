package com.biblioteca.cli.client;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class BibliotecaRestClient {

    private final RestTemplate restTemplate;

    @Value("${materiales.service.url:http://localhost:8082}")
    private String materialesServiceUrl;

    @Value("${usuarios.service.url:http://localhost:8083}")
    private String usuariosServiceUrl;

    @Value("${multas.service.url:http://localhost:8084}")
    private String multasServiceUrl;

    @Value("${reservas.service.url:http://localhost:8088}")
    private String reservasServiceUrl;

    @Value("${circulacion.service.url:http://localhost:8081}")
    private String circulacionServiceUrl;

    public BibliotecaRestClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // ============ MATERIALES ============

    public List<?> listarMateriales() {
        try {
            Object[] result = restTemplate.getForObject(
                    materialesServiceUrl + "/materiales",
                    Object[].class);
            return result != null ? Arrays.asList(result) : Collections.emptyList();
        } catch (Exception e) {
            System.err.println("Error al listar materiales: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public Object obtenerMaterial(String id) {
        try {
            return restTemplate.getForObject(
                    materialesServiceUrl + "/materiales/" + id,
                    Object.class);
        } catch (Exception e) {
            return null;
        }
    }

    public Object crearMaterial(Object material) {
        try {
            return restTemplate.postForObject(
                    materialesServiceUrl + "/materiales",
                    material,
                    Object.class);
        } catch (Exception e) {
            System.err.println("Error al crear material: " + e.getMessage());
            return null;
        }
    }

    // ============ USUARIOS ============

    public List<?> listarUsuarios() {
        try {
            Object[] result = restTemplate.getForObject(
                    usuariosServiceUrl + "/usuarios",
                    Object[].class);
            return result != null ? Arrays.asList(result) : Collections.emptyList();
        } catch (Exception e) {
            System.err.println("Error al listar usuarios: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public Object obtenerUsuario(String id) {
        try {
            return restTemplate.getForObject(
                    usuariosServiceUrl + "/usuarios/" + id,
                    Object.class);
        } catch (Exception e) {
            return null;
        }
    }

    public Object crearUsuario(Object usuario) {
        try {
            return restTemplate.postForObject(
                    usuariosServiceUrl + "/usuarios",
                    usuario,
                    Object.class);
        } catch (Exception e) {
            System.err.println("Error al crear usuario: " + e.getMessage());
            return null;
        }
    }

    // ============ PRÉSTAMOS ============

    public List<?> listarPrestamos() {
        try {
            Object[] result = restTemplate.getForObject(
                    circulacionServiceUrl + "/prestamos",
                    Object[].class);
            return result != null ? Arrays.asList(result) : Collections.emptyList();
        } catch (Exception e) {
            System.err.println("Error al listar préstamos: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public Object obtenerPrestamo(String id) {
        try {
            return restTemplate.getForObject(
                    circulacionServiceUrl + "/prestamos/" + id,
                    Object.class);
        } catch (Exception e) {
            return null;
        }
    }

    public Object registrarPrestamo(String idUsuario, String idMaterial, String tipo) {
        try {
            PrestamoDtoRequest request = new PrestamoDtoRequest(idUsuario, idMaterial, tipo);
            return restTemplate.postForObject(
                    circulacionServiceUrl + "/prestamos",
                    request,
                    Object.class);
        } catch (Exception e) {
            System.err.println("Error al registrar préstamo: " + e.getMessage());
            return null;
        }
    }

    public Object procesarDevolucion(String idPrestamo) {
        try {
            return restTemplate.getForObject(
                    circulacionServiceUrl + "/prestamos/" + idPrestamo + "/devolucion",
                    Object.class);
        } catch (Exception e) {
            System.err.println("Error al procesar devolución: " + e.getMessage());
            return null;
        }
    }

    public List<?> listarPrestamosActivos(String idUsuario) {
        try {
            Object[] result = restTemplate.getForObject(
                    circulacionServiceUrl + "/prestamos/usuario/" + idUsuario + "/activos",
                    Object[].class);
            return result != null ? Arrays.asList(result) : Collections.emptyList();
        } catch (Exception e) {
            System.err.println("Error al listar préstamos activos: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public Object renovarPrestamo(String idPrestamo) {
        try {
            return restTemplate.getForObject(
                    circulacionServiceUrl + "/prestamos/" + idPrestamo + "/renovacion",
                    Object.class);
        } catch (Exception e) {
            System.err.println("Error al renovar préstamo: " + e.getMessage());
            return null;
        }
    }

    // ============ MULTAS ============

    public List<?> listarMultas() {
        try {
            // El endpoint /multas requiere usuarioId, pero listarMultas sin filtro
            // no es soportado. Retornamos lista vacía para cumplir con interfaz
            return Collections.emptyList();
        } catch (Exception e) {
            System.err.println("Error al listar multas: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public Object obtenerMulta(String id) {
        try {
            return restTemplate.getForObject(
                    multasServiceUrl + "/multas/" + id,
                    Object.class);
        } catch (Exception e) {
            return null;
        }
    }

    public Object pagarMulta(String idMulta) {
        try {
            return restTemplate.postForObject(
                    multasServiceUrl + "/multas/" + idMulta + "/pagar",
                    null,
                    Object.class);
        } catch (Exception e) {
            System.err.println("Error al pagar multa: " + e.getMessage());
            return null;
        }
    }

    public List<?> listarMultasPorUsuario(String idUsuario) {
        try {
            Object[] result = restTemplate.getForObject(
                    multasServiceUrl + "/multas/usuario/" + idUsuario,
                    Object[].class);
            return result != null ? Arrays.asList(result) : Collections.emptyList();
        } catch (Exception e) {
            System.err.println("Error al listar multas del usuario: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    // ============ RESERVAS ============

    public List<?> listarReservas() {
        // Nota: El endpoint /reservas requiere ?materialId o ?usuarioId
        // Para listar todas las reservas, usamos ?materialId con parámetro vacío 
        // que retorna lista vacía en lugar de error
        try {
            // No hay forma de listar todas sin parámetros
            return Collections.emptyList();
        } catch (Exception e) {
            System.err.println("Error al listar reservas: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public List<?> listarReservasPorMaterial(String materialId) {
        try {
            Object[] result = restTemplate.getForObject(
                    reservasServiceUrl + "/reservas?materialId=" + materialId,
                    Object[].class);
            return result != null ? Arrays.asList(result) : Collections.emptyList();
        } catch (Exception e) {
            System.err.println("Error al listar reservas: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public List<?> listarReservasPorUsuario(String usuarioId) {
        try {
            Object[] result = restTemplate.getForObject(
                    reservasServiceUrl + "/reservas?usuarioId=" + usuarioId,
                    Object[].class);
            return result != null ? Arrays.asList(result) : Collections.emptyList();
        } catch (Exception e) {
            System.err.println("Error al listar reservas: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public Object crearReserva(String idUsuario, String idMaterial) {
        try {
            ReservaDtoRequest request = new ReservaDtoRequest(idUsuario, idMaterial);
            return restTemplate.postForObject(
                    reservasServiceUrl + "/reservas",
                    request,
                    Object.class);
        } catch (Exception e) {
            System.err.println("Error al crear reserva: " + e.getMessage());
            return null;
        }
    }

    public Object cancelarReserva(String idReserva) {
        try {
            restTemplate.delete(reservasServiceUrl + "/reservas/" + idReserva);
            return new Object();
        } catch (Exception e) {
            System.err.println("Error al cancelar reserva: " + e.getMessage());
            return null;
        }
    }

    // DTOs internos
    static class PrestamoDtoRequest {
        public String usuarioId;
        public String materialId;
        public String tipo;

        public PrestamoDtoRequest(String usuarioId, String materialId, String tipo) {
            this.usuarioId = usuarioId;
            this.materialId = materialId;
            this.tipo = tipo;
        }
    }

    static class ReservaDtoRequest {
        public String usuarioId;
        public String materialId;

        public ReservaDtoRequest(String usuarioId, String materialId) {
            this.usuarioId = usuarioId;
            this.materialId = materialId;
        }
    }
}
