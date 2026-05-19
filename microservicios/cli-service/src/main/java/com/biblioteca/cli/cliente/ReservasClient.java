package com.biblioteca.cli.cliente;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Component
public class ReservasClient {

    private final RestTemplate rest = new RestTemplate();

    @Value("${servicios.reservas}")
    private String base;

    public List<Map<String, Object>> listar(String usuarioId, String materialId) {
        String url = base + "/reservas";
        if (usuarioId != null) url += "?usuarioId=" + usuarioId;
        else if (materialId != null) url += "?materialId=" + materialId;
        return rest.exchange(url, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Map<String, Object>>>() {}).getBody();
    }

    public Map<String, Object> crear(String idUsuario, String idMaterial, String tipo) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, Object> body = Map.of(
                "idUsuario", idUsuario,
                "idMaterial", idMaterial,
                "tipoReserva", tipo
        );
        return rest.postForObject(base + "/reservas", new HttpEntity<>(body, headers), Map.class);
    }

    public void cancelar(String id) {
        rest.delete(base + "/reservas/" + id);
    }
}
