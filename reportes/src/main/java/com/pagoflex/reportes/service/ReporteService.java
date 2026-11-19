package com.pagoflex.reportes.service;

import com.pagoflex.reportes.model.Reporte;
import com.pagoflex.reportes.repository.ReporteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReporteService {
    private final ReporteRepository repository;

    public List<Reporte> listarTodos() {
        return repository.findAll();
    }

    public Reporte crear(Reporte reporte) {
        reporte.setEstado("Abierto");
        reporte.setFechaHora(LocalDateTime.now());
        return repository.save(reporte);
    }

    public Reporte actualizarEstadoYRespuesta(Long id, String nuevoEstado, String respuesta) {
        Reporte reporte = repository.findById(id).orElseThrow(() -> new RuntimeException("Reporte no encontrado"));
        if (nuevoEstado != null) reporte.setEstado(nuevoEstado);
        if (respuesta != null) reporte.setRespuestaAgente(respuesta);
        return repository.save(reporte);
    }
}