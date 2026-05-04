package com.biblioteca.circulacion.infraestructura.clientes;

import com.biblioteca.circulacion.aplicacion.dto.EstadoUsuarioDTO;
import com.biblioteca.circulacion.aplicacion.dto.LimitePrestamoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "usuarios-service")
public interface UsuariosClient {

    @GetMapping("/usuarios/{id}/estado")
    EstadoUsuarioDTO consultarEstado(@PathVariable String id);

    @GetMapping("/usuarios/{id}/limite-prestamos")
    LimitePrestamoDTO consultarLimite(@PathVariable String id);
}
