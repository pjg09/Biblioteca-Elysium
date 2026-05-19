package com.biblioteca.cli.cliente;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class CobrosClient {

    private final RestTemplate rest = new RestTemplate();

    @Value("${servicios.cobros}")
    private String base;

    public Map<String, Object> registrarPago(String multaId, String usuarioId, double monto) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, Object> body = Map.of(
                "multaId", multaId,
                "usuarioId", usuarioId,
                "monto", monto
        );
        return rest.postForObject(base + "/pagos", new HttpEntity<>(body, headers), Map.class);
    }
}
