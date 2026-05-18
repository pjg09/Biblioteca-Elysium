package com.biblioteca.cobros.infraestructura.persistencia;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegistroPagoJpaRepository extends JpaRepository<RegistroPagoEntity, String> {

    List<RegistroPagoEntity> findByUsuarioId(String usuarioId);

    List<RegistroPagoEntity> findByMultaId(String multaId);

    @Query("SELECT SUM(r.monto) FROM RegistroPagoEntity r WHERE r.usuarioId = :usuarioId")
    Double sumMontoByUsuarioId(@Param("usuarioId") String usuarioId);
}
