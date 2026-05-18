package com.biblioteca.multas.aplicacion;

import com.biblioteca.multas.aplicacion.dto.DeudaPendienteDTO;
import com.biblioteca.multas.infraestructura.persistencia.MultaEntity;
import com.biblioteca.multas.infraestructura.persistencia.MultaJpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MultaService {

    private final MultaJpaRepository multaJpaRepository;

    public MultaService(MultaJpaRepository multaJpaRepository) {
        this.multaJpaRepository = multaJpaRepository;
    }

    public Optional<MultaEntity> obtenerPorId(String id) {
        return multaJpaRepository.findById(id);
    }

    public List<MultaEntity> obtenerPorUsuario(String idUsuario) {
        return multaJpaRepository.findByIdUsuario(idUsuario);
    }

    public DeudaPendienteDTO consultarDeudaPendiente(String idUsuario) {
        List<MultaEntity> multasPendientes = multaJpaRepository.findByIdUsuarioAndEstado(idUsuario, "PENDIENTE");

        double totalDeuda = multasPendientes.stream()
            .mapToDouble(MultaEntity::getMontoTotal)
            .sum();

        return new DeudaPendienteDTO(idUsuario, totalDeuda, multasPendientes.size());
    }

    public List<MultaEntity> obtenerPorPrestamo(String idPrestamo) {
        return multaJpaRepository.findByIdPrestamo(idPrestamo);
    }

    public List<MultaEntity> obtenerPorUsuarioYEstado(String idUsuario, String estado) {
        return multaJpaRepository.findByIdUsuarioAndEstado(idUsuario, estado);
    }
}
