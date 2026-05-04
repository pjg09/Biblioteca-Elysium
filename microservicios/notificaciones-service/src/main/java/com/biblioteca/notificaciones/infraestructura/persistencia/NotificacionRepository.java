package com.biblioteca.notificaciones.infraestructura.persistencia;

import com.biblioteca.notificaciones.dominio.Notificacion;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificacionRepository extends MongoRepository<Notificacion, String> {

    List<Notificacion> findByUsuarioIdOrderByFechaCreacionDesc(String usuarioId);

    List<Notificacion> findByUsuarioIdAndLeida(String usuarioId, boolean leida);
}
