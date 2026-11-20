package com.pagoflex.usuarios.controller;

import com.pagoflex.usuarios.model.Ciudad;
import com.pagoflex.usuarios.repository.CiudadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ciudades")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CiudadController {

    private final CiudadRepository repo;

    @GetMapping
    public ResponseEntity<List<Ciudad>> listar() {
        return ResponseEntity.ok(repo.findAll());
    }

    @PostMapping
    public ResponseEntity<Ciudad> crear(@RequestBody Ciudad ciudad) {
        return ResponseEntity.ok(repo.save(ciudad));
    }

    @GetMapping("/health")
    public ResponseEntity<?> health() {
        return ResponseEntity.ok(Map.of("status", "UP", "service", "usuarios-service"));
    }
}