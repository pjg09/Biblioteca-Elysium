package com.biblioteca.reportes.infraestructura.persistencia;

import com.biblioteca.reportes.dominio.RegistroEstadistica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EstadisticaRepository extends JpaRepository<RegistroEstadistica, Long> {

    List<RegistroEstadistica> findByTipo(String tipo);

    List<RegistroEstadistica> findByFechaBetween(LocalDateTime inicio, LocalDateTime fin);

    List<RegistroEstadistica> findByTipoAndFechaBetween(String tipo, LocalDateTime inicio, LocalDateTime fin);
}
