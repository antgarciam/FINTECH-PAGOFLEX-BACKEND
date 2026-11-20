package com.pagoflex.pagos.controller;

import com.pagoflex.pagos.dto.CrearCompromisoRequest;
import com.pagoflex.pagos.model.Compromiso;
import com.pagoflex.pagos.service.PagoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pagos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PagoController {

    private final PagoService service;

    @PostMapping("/compromisos")
    public ResponseEntity<Compromiso> crear(@RequestBody CrearCompromisoRequest req) {
        return ResponseEntity.ok(service.crearCompromiso(req));
    }

    @GetMapping("/cuotas/{usuarioId}")
    public ResponseEntity<List<Map<String, Object>>> listar(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(service.listarCuotasPorUsuario(usuarioId));
    }

    @PostMapping("/pagar/{cuotaId}")
    public ResponseEntity<?> pagar(@PathVariable Long cuotaId) {
        service.pagarCuota(cuotaId);
        return ResponseEntity.ok(Map.of("mensaje", "Pago exitoso"));
    }

    @DeleteMapping("/compromisos/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        service.desactivarCompromiso(id);
        return ResponseEntity.ok(Map.of("mensaje", "Compromiso desactivado"));
    }

    @GetMapping("/health")
    public ResponseEntity<?> health() {
        return ResponseEntity.ok(Map.of("status", "UP", "service", "pagos-service"));
    }
}