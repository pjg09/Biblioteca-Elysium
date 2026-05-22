package com.biblioteca.reportes.api;

import com.biblioteca.reportes.dominio.RegistroEstadistica;
import com.biblioteca.reportes.infraestructura.persistencia.EstadisticaRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/reportes")
public class ReportesController {

    private final EstadisticaRepository estadisticaRepository;

    public ReportesController(EstadisticaRepository estadisticaRepository) {
        this.estadisticaRepository = estadisticaRepository;
    }

    @GetMapping("/prestamos")
    public ResponseEntity<Map<String, Object>> contarPrestamos(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta) {

        List<RegistroEstadistica> registros;
        if (desde != null && hasta != null) {
            registros = estadisticaRepository.findByTipoAndFechaBetween("PRESTAMO", desde, hasta);
        } else {
            registros = estadisticaRepository.findByTipo("PRESTAMO");
        }

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("total", registros.size());
        respuesta.put("desde", desde);
        respuesta.put("hasta", hasta);
        respuesta.put("registros", registros);
        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/multas-pendientes")
    public ResponseEntity<Map<String, Object>> multasPendientes() {
        List<RegistroEstadistica> multasGeneradas = estadisticaRepository.findByTipo("MULTA_GENERADA");
        List<RegistroEstadistica> multasPagadas = estadisticaRepository.findByTipo("MULTA_PAGADA");

        double totalGenerado = multasGeneradas.stream()
                .mapToDouble(RegistroEstadistica::getValor)
                .sum();
        double totalPagado = multasPagadas.stream()
                .mapToDouble(RegistroEstadistica::getValor)
                .sum();

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("cantidadMultasGeneradas", multasGeneradas.size());
        respuesta.put("cantidadMultasPagadas", multasPagadas.size());
        respuesta.put("cantidadMultasPendientes", multasGeneradas.size() - multasPagadas.size());
        respuesta.put("montoTotalGenerado", totalGenerado);
        respuesta.put("montoTotalPagado", totalPagado);
        respuesta.put("montoPendiente", totalGenerado - totalPagado);
        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/estadisticas")
    public ResponseEntity<List<RegistroEstadistica>> obtenerEstadisticas(
            @RequestParam String tipo) {
        List<RegistroEstadistica> registros = estadisticaRepository.findByTipo(tipo);
        return ResponseEntity.ok(registros);
    }

    @GetMapping("/resumen")
    public ResponseEntity<Map<String, Long>> obtenerResumen() {
        List<RegistroEstadistica> todos = estadisticaRepository.findAll();

        Set<String> tipos = todos.stream()
                .map(RegistroEstadistica::getTipo)
                .collect(Collectors.toSet());

        Map<String, Long> resumen = new HashMap<>();
        for (String tipo : tipos) {
            long count = todos.stream()
                    .filter(r -> tipo.equals(r.getTipo()))
                    .count();
            resumen.put(tipo, count);
        }
        return ResponseEntity.ok(resumen);
    }
}
