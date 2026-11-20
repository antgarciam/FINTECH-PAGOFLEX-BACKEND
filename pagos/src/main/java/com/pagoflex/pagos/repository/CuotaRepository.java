package com.pagoflex.pagos.repository;

import com.pagoflex.pagos.model.Cuota;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CuotaRepository extends JpaRepository<Cuota, Long> {
    List<Cuota> findByCompromisoIdOrderByNumeroCuotaAsc(Long compromisoId);
}