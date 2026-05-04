package com.biblioteca.usuarios.infraestructura.persistencia;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioJpaRepository extends JpaRepository<UsuarioEntity, String> {

    Optional<UsuarioEntity> findByEmail(String email);

    List<UsuarioEntity> findByTipoUsuario(String tipo);

    List<UsuarioEntity> findByEstadoUsuario(String estado);
}
