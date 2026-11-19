package com.pagoflex.notificaciones.controller;

import com.pagoflex.notificaciones.dto.NotificacionDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notificaciones")
@CrossOrigin(origins = "*")
public class NotificacionController {

    private final List<NotificacionDto> mock = List.of(
        new NotificacionDto(1L, 1L, "Tu pago de $45.000 fue recibido", true, "2026-11-01 10:30"),
        new NotificacionDto(2L, 1L, "Tienes un compromiso vencido", false, "2026-11-05 08:00"),
        new NotificacionDto(3L, 2L, "Bienvenido a PagoFlex", true, "2026-10-20 12:00")
    );

    @GetMapping("/{usuarioId}")
    public ResponseEntity<List<NotificacionDto>> listar(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(mock.stream()
                .filter(n -> n.getUsuarioId().equals(usuarioId))
                .toList());
    }

    @GetMapping("/health")
    public ResponseEntity<?> health() {
        return ResponseEntity.ok(Map.of("status", "UP", "service", "notificaciones-service"));
    }
}