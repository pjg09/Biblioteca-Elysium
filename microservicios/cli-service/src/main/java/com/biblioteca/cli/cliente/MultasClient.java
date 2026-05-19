package com.biblioteca.cli.cliente;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Component
public class MultasClient {

    private final RestTemplate rest = new RestTemplate();

    @Value("${servicios.multas}")
    private String base;

    public List<Map<String, Object>> listarPorUsuario(String usuarioId) {
        return rest.exchange(base + "/multas?usuarioId=" + usuarioId, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Map<String, Object>>>() {}).getBody();
    }

    public List<Map<String, Object>> listarTodas() {
        return rest.exchange(base + "/multas", HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Map<String, Object>>>() {}).getBody();
    }

    public Map<String, Object> obtenerPorId(String id) {
        try {
            return rest.getForObject(base + "/multas/" + id, Map.class);
        } catch (Exception e) {
            return null;
        }
    }

    public Map<String, Object> consultarDeuda(String usuarioId) {
        try {
            return rest.getForObject(base + "/usuarios/" + usuarioId + "/deuda-pendiente", Map.class);
        } catch (Exception e) {
            return null;
        }
    }
}
