package com.pagoflex.pagos.repository;

import com.pagoflex.pagos.model.Compromiso;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CompromisoRepository extends JpaRepository<Compromiso, Long> {
    List<Compromiso> findByUsuarioIdAndActivoTrue(Long usuarioId);
}