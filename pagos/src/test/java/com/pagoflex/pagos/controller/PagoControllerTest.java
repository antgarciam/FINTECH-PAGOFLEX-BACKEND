package com.pagoflex.pagos.controller;

import com.pagoflex.pagos.dto.CrearCompromisoRequest;
import com.pagoflex.pagos.model.Compromiso;
import com.pagoflex.pagos.service.PagoService;
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
class PagoControllerTest {

    @Mock
    private PagoService service;

    @InjectMocks
    private PagoController controller;

    @Test
    void todosLosEndpointsResponden200() {
        CrearCompromisoRequest req = new CrearCompromisoRequest();
        Compromiso comp = new Compromiso();
        comp.setId(1L);
        when(service.crearCompromiso(req)).thenReturn(comp);

        ResponseEntity<Compromiso> rCrear = controller.crear(req);
        assertEquals(HttpStatus.OK, rCrear.getStatusCode());
        assertEquals(1L, rCrear.getBody().getId());

        when(service.listarCuotasPorUsuario(1L)).thenReturn(List.of(Map.of("idCuota", 10L)));
        ResponseEntity<List<Map<String, Object>>> rListar = controller.listar(1L);
        assertEquals(HttpStatus.OK, rListar.getStatusCode());
        assertEquals(1, rListar.getBody().size());

        ResponseEntity<?> rPagar = controller.pagar(10L);
        assertEquals(HttpStatus.OK, rPagar.getStatusCode());
        verify(service).pagarCuota(10L);

        ResponseEntity<?> rEliminar = controller.eliminar(1L);
        assertEquals(HttpStatus.OK, rEliminar.getStatusCode());
        verify(service).desactivarCompromiso(1L);

        ResponseEntity<?> rHealth = controller.health();
        assertEquals(HttpStatus.OK, rHealth.getStatusCode());
        assertEquals("UP", ((Map<?, ?>) rHealth.getBody()).get("status"));
    }
}
