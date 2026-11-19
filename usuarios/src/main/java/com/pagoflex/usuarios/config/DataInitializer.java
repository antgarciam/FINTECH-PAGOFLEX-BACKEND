package com.pagoflex.usuarios.config;

import com.pagoflex.usuarios.model.Ciudad;
import com.pagoflex.usuarios.repository.CiudadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final CiudadRepository repo;

    @Override
    public void run(String... args) {
        if (repo.count() == 0) {
            repo.saveAll(List.of(
                new Ciudad(null, "Santiago", "Metropolitana"),
                new Ciudad(null, "Valparaíso", "Valparaíso"),
                new Ciudad(null, "Concepción", "Biobío"),
                new Ciudad(null, "La Serena", "Coquimbo"),
                new Ciudad(null, "Antofagasta", "Antofagasta"),
                new Ciudad(null, "Temuco", "Araucanía"),
                new Ciudad(null, "Puerto Montt", "Los Lagos"),
                new Ciudad(null, "Iquique", "Tarapacá"),
                new Ciudad(null, "Rancagua", "O'Higgins"),
                new Ciudad(null, "Talca", "Maule"),
                new Ciudad(null, "Maipú", "Metropolitana"),
                new Ciudad(null, "Providencia", "Metropolitana")
            ));
        }
    }
}