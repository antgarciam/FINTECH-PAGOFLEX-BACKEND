package com.pagoflex.usuarios.repository;

import com.pagoflex.usuarios.model.Ciudad;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CiudadRepository extends JpaRepository<Ciudad, Long> {
}