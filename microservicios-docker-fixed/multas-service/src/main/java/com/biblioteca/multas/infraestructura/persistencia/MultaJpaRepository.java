package com.biblioteca.multas.infraestructura.persistencia;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MultaJpaRepository extends JpaRepository<MultaEntity, String> {

    List<MultaEntity> findByIdUsuario(String idUsuario);

    List<MultaEntity> findByIdUsuarioAndEstado(String idUsuario, String estado);

    List<MultaEntity> findByIdPrestamo(String idPrestamo);
}
