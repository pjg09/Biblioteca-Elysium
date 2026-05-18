package com.biblioteca.circulacion.infraestructura.persistencia;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrestamoJpaRepository extends JpaRepository<PrestamoEntity, String> {

    List<PrestamoEntity> findByIdUsuario(String idUsuario);

    List<PrestamoEntity> findByIdUsuarioAndEstado(String idUsuario, String estado);

    List<PrestamoEntity> findByIdMaterialAndEstado(String idMaterial, String estado);

    long countByIdUsuarioAndEstado(String idUsuario, String estado);
}
