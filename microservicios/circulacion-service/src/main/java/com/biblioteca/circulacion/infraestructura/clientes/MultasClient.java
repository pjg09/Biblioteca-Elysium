package com.biblioteca.circulacion.infraestructura.clientes;

import com.biblioteca.circulacion.aplicacion.dto.DeudaPendienteDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "multas-service")
public interface MultasClient {

    @GetMapping("/usuarios/{usuarioId}/deuda-pendiente")
    DeudaPendienteDTO consultarDeuda(@PathVariable String usuarioId);
}
