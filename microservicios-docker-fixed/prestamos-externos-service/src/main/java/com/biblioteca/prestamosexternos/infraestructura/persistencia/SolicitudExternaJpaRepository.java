package com.biblioteca.prestamosexternos.infraestructura.persistencia;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SolicitudExternaJpaRepository extends JpaRepository<SolicitudExternaEntity, String> {

    /**
     * Busca la solicitud asociada a un préstamo de circulacion-service.
     */
    Optional<SolicitudExternaEntity> findByPrestamoId(String prestamoId);

    /**
     * Lista todas las solicitudes de un usuario (cualquier estado).
     */
    List<SolicitudExternaEntity> findByIdUsuario(String idUsuario);

    /**
     * Lista todas las solicitudes en un estado determinado.
     */
    List<SolicitudExternaEntity> findByEstado(String estado);
}
