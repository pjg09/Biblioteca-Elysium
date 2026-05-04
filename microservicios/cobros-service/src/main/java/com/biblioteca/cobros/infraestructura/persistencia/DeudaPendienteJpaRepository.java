package com.biblioteca.cobros.infraestructura.persistencia;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeudaPendienteJpaRepository extends JpaRepository<DeudaPendienteEntity, Long> {

    List<DeudaPendienteEntity> findByUsuarioIdAndPagado(String usuarioId, boolean pagado);

    Optional<DeudaPendienteEntity> findByMultaId(String multaId);
}
