package com.biblioteca.cli.cliente;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Component
public class UsuariosClient {

    private final RestTemplate rest = new RestTemplate();

    @Value("${servicios.usuarios}")
    private String base;

    public List<Map<String, Object>> listar() {
        return rest.exchange(base + "/usuarios", HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Map<String, Object>>>() {}).getBody();
    }

    public Map<String, Object> obtenerPorId(String id) {
        try {
            return rest.getForObject(base + "/usuarios/" + id, Map.class);
        } catch (Exception e) {
            return null;
        }
    }

    public Map<String, Object> consultarEstado(String id) {
        try {
            return rest.getForObject(base + "/usuarios/" + id + "/estado", Map.class);
        } catch (Exception e) {
            return null;
        }
    }

    public Map<String, Object> crear(Map<String, Object> request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);
        return rest.postForObject(base + "/usuarios", entity, Map.class);
    }
}
