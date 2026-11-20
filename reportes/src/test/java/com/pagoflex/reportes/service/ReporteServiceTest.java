package com.pagoflex.reportes.service;

import com.pagoflex.reportes.model.Reporte;
import com.pagoflex.reportes.repository.ReporteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReporteServiceTest {

    @Mock
    private ReporteRepository repository;

    @InjectMocks
    private ReporteService service;

    @Test
    void listarTodos_retornaListaDeReportes() {
        Reporte r = new Reporte();
        r.setId(1L);
        when(repository.findAll()).thenReturn(List.of(r));

        List<Reporte> res = service.listarTodos();
        assertEquals(1, res.size());
    }

    @Test
    void crear_asignaEstadoAbiertoYFechaHora() {
        Reporte r = new Reporte();
        r.setClienteNombre("Carlos");
        r.setMotivo("Cobro duplicado");

        when(repository.save(any(Reporte.class))).thenAnswer(inv -> inv.getArgument(0));

        Reporte guardado = service.crear(r);

        assertEquals("Abierto", guardado.getEstado());
        assertNotNull(guardado.getFechaHora());
        verify(repository).save(r);
    }

    @Test
    void actualizarEstadoYRespuesta_cubreTodosLosCasos() {
        Reporte existente = new Reporte();
        existente.setId(1L);
        existente.setEstado("Abierto");

        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        when(repository.save(any(Reporte.class))).thenAnswer(inv -> inv.getArgument(0));

        // Actualiza ambos
        Reporte act1 = service.actualizarEstadoYRespuesta(1L, "Resuelto", "Reembolso procesado");
        assertEquals("Resuelto", act1.getEstado());
        assertEquals("Reembolso procesado", act1.getRespuestaAgente());

        // Con parámetros nulos mantiene los anteriores
        Reporte act2 = service.actualizarEstadoYRespuesta(1L, null, null);
        assertEquals("Resuelto", act2.getEstado());
        assertEquals("Reembolso procesado", act2.getRespuestaAgente());

        // No encontrado
        when(repository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class,
                () -> service.actualizarEstadoYRespuesta(99L, "Cerrado", "Ok"));
    }
}
