package com.biblioteca.cli.cliente;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Component
public class MaterialesClient {

    private final RestTemplate rest = new RestTemplate();

    @Value("${servicios.materiales}")
    private String base;

    public List<Map<String, Object>> listar() {
        return rest.exchange(base + "/materiales", HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Map<String, Object>>>() {}).getBody();
    }

    public Map<String, Object> obtenerPorId(String id) {
        try {
            return rest.getForObject(base + "/materiales/" + id, Map.class);
        } catch (Exception e) {
            return null;
        }
    }

    public Map<String, Object> consultarDisponibilidad(String id) {
        try {
            return rest.getForObject(base + "/materiales/" + id + "/disponibilidad", Map.class);
        } catch (Exception e) {
            return null;
        }
    }

    public Map<String, Object> crear(Map<String, Object> request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);
        return rest.postForObject(base + "/materiales", entity, Map.class);
    }

    public Map<String, Object> actualizarEstado(String id, String nuevoEstado) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, Object> body = Map.of("estado", nuevoEstado);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        return rest.exchange(base + "/materiales/" + id + "/estado", HttpMethod.PUT, entity, Map.class).getBody();
    }
}
