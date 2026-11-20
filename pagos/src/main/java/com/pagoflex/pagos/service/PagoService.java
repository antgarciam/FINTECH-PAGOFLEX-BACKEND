package com.pagoflex.pagos.service;

import com.pagoflex.pagos.dto.CrearCompromisoRequest;
import com.pagoflex.pagos.model.Compromiso;
import com.pagoflex.pagos.model.Cuota;
import com.pagoflex.pagos.repository.CompromisoRepository;
import com.pagoflex.pagos.repository.CuotaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PagoService {

    private final CompromisoRepository compromisoRepo;
    private final CuotaRepository cuotaRepo;

    public Compromiso crearCompromiso(CrearCompromisoRequest req) {
        // 1. Guardar el compromiso global
        Compromiso c = new Compromiso();
        c.setUsuarioId(req.getUsuarioId());
        c.setEmpresa(req.getEmpresa());
        c.setConcepto(req.getConcepto());
        c.setMontoTotal(req.getMontoTotal());
        c.setNumeroCuotas(req.getNumeroCuotas());
        c.setFechaInicio(req.getFechaInicio());
        c.setActivo(true);
        compromisoRepo.save(c);

        // 2. Generar las cuotas automáticamente
        int montoPorCuota = req.getMontoTotal() / req.getNumeroCuotas();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate fechaBase = LocalDate.parse(req.getFechaInicio(), formatter);

        List<Cuota> cuotas = new ArrayList<>();
        for (int i = 1; i <= req.getNumeroCuotas(); i++) {
            Cuota cuota = new Cuota();
            cuota.setCompromisoId(c.getId());
            cuota.setNumeroCuota(i);
            cuota.setMonto(montoPorCuota);
            // Sumamos 1 mes por cada cuota
            cuota.setFechaVencimiento(fechaBase.plusMonths(i).format(formatter));
            cuota.setEstado("Pendiente");
            cuotas.add(cuota);
        }
        cuotaRepo.saveAll(cuotas);

        return c;
    }

    public List<Map<String, Object>> listarCuotasPorUsuario(Long usuarioId) {
        List<Compromiso> compromisos = compromisoRepo.findByUsuarioIdAndActivoTrue(usuarioId);
        List<Map<String, Object>> respuesta = new ArrayList<>();

        for (Compromiso c : compromisos) {
            List<Cuota> cuotas = cuotaRepo.findByCompromisoIdOrderByNumeroCuotaAsc(c.getId());
            for (Cuota cuota : cuotas) {
                respuesta.add(Map.of(
                        "idCuota", cuota.getId(),
                        "compromisoId", c.getId(),
                        "empresa", c.getEmpresa(),
                        "concepto", c.getConcepto(),
                        "cuotaActual", cuota.getNumeroCuota(),
                        "totalCuotas", c.getNumeroCuotas(),
                        "monto", cuota.getMonto(),
                        "vencimiento", cuota.getFechaVencimiento(),
                        "estado", cuota.getEstado()
                ));
            }
        }
        return respuesta;
    }

    public void pagarCuota(Long cuotaId) {
        Cuota cuota = cuotaRepo.findById(cuotaId)
                .orElseThrow(() -> new RuntimeException("Cuota no encontrada"));
        cuota.setEstado("Pagado");
        cuotaRepo.save(cuota);
    }

    public void desactivarCompromiso(Long compromisoId) {
        Compromiso c = compromisoRepo.findById(compromisoId)
                .orElseThrow(() -> new RuntimeException("Compromiso no encontrado"));
        c.setActivo(false); // Borrado lógico
        compromisoRepo.save(c);
    }
}