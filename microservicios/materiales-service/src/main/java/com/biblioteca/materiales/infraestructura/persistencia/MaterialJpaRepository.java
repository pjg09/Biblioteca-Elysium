package com.biblioteca.materiales.infraestructura.persistencia;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaterialJpaRepository extends JpaRepository<MaterialEntity, String> {

    List<MaterialEntity> findByEstado(String estado);

    List<MaterialEntity> findByTipo(String tipo);

    List<MaterialEntity> findByTituloContainingIgnoreCase(String titulo);
}
