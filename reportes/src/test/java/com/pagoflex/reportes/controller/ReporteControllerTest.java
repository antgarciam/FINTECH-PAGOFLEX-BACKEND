package com.pagoflex.reportes.controller;

import com.pagoflex.reportes.model.Reporte;
import com.pagoflex.reportes.service.ReporteService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReporteControllerTest {

    @Mock
    private ReporteService service;

    @InjectMocks
    private ReporteController controller;

    @Test
    void listarCrearYActualizar_responden200() {
        Reporte r = new Reporte();
        r.setId(1L);
        when(service.listarTodos()).thenReturn(List.of(r));
        when(service.crear(r)).thenReturn(r);
        when(service.actualizarEstadoYRespuesta(1L, "Cerrado", "Solucionado")).thenReturn(r);

        assertEquals(HttpStatus.OK, controller.listar().getStatusCode());
        assertEquals(HttpStatus.OK, controller.crear(r).getStatusCode());

        ResponseEntity<?> upd = controller.actualizar(1L, Map.of(
                "estado", "Cerrado",
                "respuestaAgente", "Solucionado"
        ));
        assertEquals(HttpStatus.OK, upd.getStatusCode());
    }
}
