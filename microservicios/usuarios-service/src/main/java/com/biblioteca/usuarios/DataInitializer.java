package com.biblioteca.usuarios;

import com.biblioteca.usuarios.infraestructura.persistencia.UsuarioEntity;
import com.biblioteca.usuarios.infraestructura.persistencia.UsuarioJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);
    private final UsuarioJpaRepository repo;

    public DataInitializer(UsuarioJpaRepository repo) {
        this.repo = repo;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (repo.count() > 0) return;

        log.info("Cargando datos mock de usuarios...");

        repo.save(usuario("USR-000001", "Juan Pérez",        "juan@email.com",    "ESTUDIANTE",       5, "ACTIVO"));
        repo.save(usuario("USR-000002", "María García",      "maria@email.com",   "ESTUDIANTE",       5, "ACTIVO"));
        repo.save(usuario("USR-000003", "Carlos Rodríguez",  "carlos@email.com",  "PROFESOR",        10, "ACTIVO"));
        repo.save(usuario("USR-000004", "Ana Martínez",      "ana@email.com",     "PROFESOR",        10, "ACTIVO"));
        repo.save(usuario("USR-000005", "Luis Fernández",    "luis@email.com",    "INVESTIGADOR",    15, "ACTIVO"));
        repo.save(usuario("USR-000006", "Sofía López",       "sofia@email.com",   "PUBLICO_GENERAL",  3, "ACTIVO"));
        // Usuario bloqueado por deuda (para probar bloqueo)
        repo.save(usuario("USR-000007", "Pedro Morales",     "pedro@email.com",   "ESTUDIANTE",       5, "BLOQUEADO"));
        // Usuario con límite al máximo (3 préstamos activos para PUBLICO_GENERAL)
        repo.save(usuario("USR-000008", "Laura Castillo",    "laura@email.com",   "PUBLICO_GENERAL",  3, "ACTIVO"));

        log.info("8 usuarios mock cargados.");
    }

    private UsuarioEntity usuario(String id, String nombre, String email, String tipo, int limite, String estado) {
        UsuarioEntity e = new UsuarioEntity();
        e.setId(id);
        e.setNombre(nombre);
        e.setEmail(email);
        e.setTipoUsuario(tipo);
        e.setEstadoUsuario(estado);
        e.setLimiteMaximoPrestamos(limite);
        e.setFechaCreacion(LocalDateTime.now());
        return e;
    }
}
