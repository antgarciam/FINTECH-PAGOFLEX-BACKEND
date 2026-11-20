package com.pagoflex.pagos.service;

import com.pagoflex.pagos.dto.CrearCompromisoRequest;
import com.pagoflex.pagos.model.Compromiso;
import com.pagoflex.pagos.model.Cuota;
import com.pagoflex.pagos.repository.CompromisoRepository;
import com.pagoflex.pagos.repository.CuotaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PagoServiceTest {

    @Mock
    private CompromisoRepository compromisoRepo;

    @Mock
    private CuotaRepository cuotaRepo;

    @InjectMocks
    private PagoService pagoService;

    @Test
    void crearCompromiso_generaCuotasMensualesCorrectamente() {
        CrearCompromisoRequest req = new CrearCompromisoRequest();
        req.setUsuarioId(1L);
        req.setEmpresa("Enel");
        req.setConcepto("Cuenta de luz");
        req.setMontoTotal(90000);
        req.setNumeroCuotas(3);
        req.setFechaInicio("10-10-2026");

        when(compromisoRepo.save(any(Compromiso.class))).thenAnswer(inv -> {
            Compromiso c = inv.getArgument(0);
            c.setId(10L);
            return c;
        });

        Compromiso resultado = pagoService.crearCompromiso(req);

        assertEquals(10L, resultado.getId());
        assertEquals("Enel", resultado.getEmpresa());
        assertTrue(resultado.getActivo());

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<Cuota>> cuotasCaptor = ArgumentCaptor.forClass(List.class);
        verify(cuotaRepo).saveAll(cuotasCaptor.capture());

        List<Cuota> cuotasGuardadas = cuotasCaptor.getValue();
        assertEquals(3, cuotasGuardadas.size());
        assertEquals(30000, cuotasGuardadas.get(0).getMonto());
        assertEquals("10-11-2026", cuotasGuardadas.get(0).getFechaVencimiento());
        assertEquals("10-12-2026", cuotasGuardadas.get(1).getFechaVencimiento());
        assertEquals("10-01-2027", cuotasGuardadas.get(2).getFechaVencimiento());
        assertEquals("Pendiente", cuotasGuardadas.get(0).getEstado());
    }

    @Test
    void listarCuotasPorUsuario_retornaDetalleDeCuotasActivas() {
        Compromiso comp = new Compromiso();
        comp.setId(10L);
        comp.setUsuarioId(1L);
        comp.setEmpresa("Movistar");
        comp.setConcepto("Plan Fibra");
        comp.setMontoTotal(40000);
        comp.setNumeroCuotas(2);
        comp.setActivo(true);

        Cuota c1 = new Cuota();
        c1.setId(101L);
        c1.setCompromisoId(10L);
        c1.setNumeroCuota(1);
        c1.setMonto(20000);
        c1.setFechaVencimiento("01-12-2026");
        c1.setEstado("Pendiente");

        when(compromisoRepo.findByUsuarioIdAndActivoTrue(1L)).thenReturn(List.of(comp));
        when(cuotaRepo.findByCompromisoIdOrderByNumeroCuotaAsc(10L)).thenReturn(List.of(c1));

        List<Map<String, Object>> lista = pagoService.listarCuotasPorUsuario(1L);

        assertEquals(1, lista.size());
        assertEquals(101L, lista.get(0).get("idCuota"));
        assertEquals("Movistar", lista.get(0).get("empresa"));
        assertEquals(20000, lista.get(0).get("monto"));
        assertEquals("Pendiente", lista.get(0).get("estado"));
    }

    @Test
    void pagarCuota_exitosoYNoEncontrada() {
        Cuota cuota = new Cuota();
        cuota.setId(5L);
        cuota.setEstado("Pendiente");

        when(cuotaRepo.findById(5L)).thenReturn(Optional.of(cuota));
        pagoService.pagarCuota(5L);
        assertEquals("Pagado", cuota.getEstado());
        verify(cuotaRepo).save(cuota);

        when(cuotaRepo.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> pagoService.pagarCuota(99L));
    }

    @Test
    void desactivarCompromiso_exitosoYNoEncontrado() {
        Compromiso comp = new Compromiso();
        comp.setId(7L);
        comp.setActivo(true);

        when(compromisoRepo.findById(7L)).thenReturn(Optional.of(comp));
        pagoService.desactivarCompromiso(7L);
        assertFalse(comp.getActivo());
        verify(compromisoRepo).save(comp);

        when(compromisoRepo.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> pagoService.desactivarCompromiso(99L));
    }
}
