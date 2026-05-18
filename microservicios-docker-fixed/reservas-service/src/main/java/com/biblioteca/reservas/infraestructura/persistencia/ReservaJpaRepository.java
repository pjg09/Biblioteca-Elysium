package com.biblioteca.reservas.infraestructura.persistencia;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservaJpaRepository extends JpaRepository<ReservaEntity, String> {

    /**
     * Devuelve las reservas de un material filtradas por estado, ordenadas por posición ASC.
     */
    @Query("SELECT r FROM ReservaEntity r WHERE r.idMaterial = :idMaterial AND r.estadoReserva IN :estados ORDER BY r.posicionCola ASC")
    List<ReservaEntity> findByIdMaterialAndEstadoReservaIn(
            @Param("idMaterial") String idMaterial,
            @Param("estados") List<String> estados);

    /**
     * Devuelve las reservas de un usuario filtradas por estado.
     */
    @Query("SELECT r FROM ReservaEntity r WHERE r.idUsuario = :idUsuario AND r.estadoReserva IN :estados ORDER BY r.posicionCola ASC")
    List<ReservaEntity> findByIdUsuarioAndEstadoReservaIn(
            @Param("idUsuario") String idUsuario,
            @Param("estados") List<String> estados);

    /**
     * Devuelve las reservas de un material en un estado específico.
     */
    @Query("SELECT r FROM ReservaEntity r WHERE r.idMaterial = :idMaterial AND r.estadoReserva = :estado ORDER BY r.posicionCola ASC")
    List<ReservaEntity> findByIdMaterialAndEstadoReserva(
            @Param("idMaterial") String idMaterial,
            @Param("estado") String estado);

    /**
     * Devuelve todas las reservas en un estado específico (para uso del scheduler).
     */
    @Query("SELECT r FROM ReservaEntity r WHERE r.estadoReserva = :estado")
    List<ReservaEntity> findAllByEstadoReserva(@Param("estado") String estado);
}
