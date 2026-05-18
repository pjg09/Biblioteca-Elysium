package com.biblioteca.circulacion.infraestructura.clientes;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "prestamos-externos-service")
public interface PrestamosExternosClient {

    @PostMapping("/solicitudes-externas")
    Object crearSolicitud(@RequestBody Map<String, Object> body);
}
