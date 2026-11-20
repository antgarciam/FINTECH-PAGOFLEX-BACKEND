package com.pagoflex.reportes.controller;

import com.pagoflex.reportes.model.Reporte;
import com.pagoflex.reportes.service.ReporteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ReporteController {

    private final ReporteService service;

    @GetMapping
    public ResponseEntity<?> listar() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Reporte reporte) {
        return ResponseEntity.ok(service.crear(reporte));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String estado = body.get("estado");
        String respuesta = body.get("respuestaAgente");
        return ResponseEntity.ok(service.actualizarEstadoYRespuesta(id, estado, respuesta));
    }
}