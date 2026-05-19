package com.biblioteca.cli.cliente;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Component
public class CirculacionClient {

    private final RestTemplate rest = new RestTemplate();

    @Value("${servicios.circulacion}")
    private String base;

    public List<Map<String, Object>> listar(String usuarioId) {
        String url = base + "/prestamos";
        if (usuarioId != null && !usuarioId.isBlank()) {
            url += "?usuarioId=" + usuarioId;
        }
        return rest.exchange(url, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Map<String, Object>>>() {}).getBody();
    }

    public Map<String, Object> obtenerPorId(String id) {
        try {
            return rest.getForObject(base + "/prestamos/" + id, Map.class);
        } catch (Exception e) {
            return null;
        }
    }

    public Map<String, Object> registrar(String idUsuario, String idMaterial, String tipo, String sede) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, Object> body = Map.of(
                "idUsuario", idUsuario,
                "idMaterial", idMaterial,
                "tipoPrestamo", tipo,
                "sede", sede
        );
        return rest.postForObject(base + "/prestamos", new HttpEntity<>(body, headers), Map.class);
    }

    public Map<String, Object> devolver(String idPrestamo, boolean esUsable) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, Object> body = Map.of("esUsable", esUsable);
        return rest.postForObject(base + "/prestamos/" + idPrestamo + "/devolucion",
                new HttpEntity<>(body, headers), Map.class);
    }

    public Map<String, Object> renovar(String idPrestamo) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return rest.postForObject(base + "/prestamos/" + idPrestamo + "/renovacion",
                new HttpEntity<>(Map.of(), headers), Map.class);
    }
}
