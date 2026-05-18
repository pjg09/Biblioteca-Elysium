package com.biblioteca.usuarios.infraestructura.inicializacion;

import com.biblioteca.usuarios.infraestructura.persistencia.UsuarioEntity;
import com.biblioteca.usuarios.infraestructura.persistencia.UsuarioJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
public class UsuarioDataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(UsuarioDataInitializer.class);

    private final UsuarioJpaRepository usuarioRepository;

    public UsuarioDataInitializer(UsuarioJpaRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (usuarioRepository.count() == 0) {
            log.info("Cargando datos de prueba de usuarios...");
            cargarDatosPrueba();
        } else {
            log.info("Usuarios ya cargados. Saltando inicialización.");
        }
    }

    private void cargarDatosPrueba() {
        UsuarioEntity[] usuarios = {
            crearUsuario("EST-001", "Juan Pérez", "juan.perez@university.edu", "ESTUDIANTE", "ACTIVO", 5),
            crearUsuario("EST-002", "María García", "maria.garcia@university.edu", "ESTUDIANTE", "ACTIVO", 5),
            crearUsuario("EST-003", "Carlos López", "carlos.lopez@university.edu", "ESTUDIANTE", "ACTIVO", 5),
            crearUsuario("EST-004", "Ana Martínez", "ana.martinez@university.edu", "ESTUDIANTE", "ACTIVO", 5),
            crearUsuario("DOC-001", "Dr. Profesor Silva", "silva@university.edu", "DOCENTE", "ACTIVO", 10),
            crearUsuario("DOC-002", "Dra. Consuelo Ramírez", "ramirez@university.edu", "DOCENTE", "ACTIVO", 10),
            crearUsuario("ADMIN-001", "Administrador Biblioteca", "admin@library.edu", "ADMINISTRADOR", "ACTIVO", 20),
        };

        for (UsuarioEntity usuario : usuarios) {
            usuarioRepository.save(usuario);
            log.debug("Usuario guardado: {} - {}", usuario.getId(), usuario.getNombre());
        }

        log.info("✓ {} usuarios cargados exitosamente", usuarios.length);
    }

    private UsuarioEntity crearUsuario(String id, String nombre, String email, String tipoUsuario, 
                                        String estadoUsuario, int limiteMaximoPrestamos) {
        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setId(id);
        usuario.setNombre(nombre);
        usuario.setEmail(email);
        usuario.setTipoUsuario(tipoUsuario);
        usuario.setEstadoUsuario(estadoUsuario);
        usuario.setLimiteMaximoPrestamos(limiteMaximoPrestamos);
        usuario.setFechaCreacion(LocalDateTime.now());
        return usuario;
    }
}
