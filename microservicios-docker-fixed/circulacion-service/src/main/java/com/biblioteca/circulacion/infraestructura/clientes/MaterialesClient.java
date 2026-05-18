package com.biblioteca.circulacion.infraestructura.clientes;

import com.biblioteca.circulacion.aplicacion.dto.DisponibilidadDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "materiales-service")
public interface MaterialesClient {

    @GetMapping("/materiales/{id}/disponibilidad")
    DisponibilidadDTO consultarDisponibilidad(@PathVariable String id);

    @PutMapping("/materiales/{id}/estado")
    void actualizarEstado(@PathVariable String id, @RequestBody Map<String, String> body);
}
