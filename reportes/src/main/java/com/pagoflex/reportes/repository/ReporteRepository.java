package com.pagoflex.reportes.repository;

import com.pagoflex.reportes.model.Reporte;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReporteRepository extends JpaRepository<Reporte, Long> {
}