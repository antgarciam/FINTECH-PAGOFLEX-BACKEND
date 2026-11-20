package com.pagoflex.notificaciones.controller;

import com.pagoflex.notificaciones.config.OpenApiConfig;
import com.pagoflex.notificaciones.dto.NotificacionDto;
import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class NotificacionControllerTest {

    private final NotificacionController controller = new NotificacionController();

    @Test
    void listar_filtraNotificacionesPorUsuarioId() {
        ResponseEntity<List<NotificacionDto>> respUser1 = controller.listar(1L);
        assertEquals(HttpStatus.OK, respUser1.getStatusCode());
        assertNotNull(respUser1.getBody());
        assertEquals(2, respUser1.getBody().size());
        assertEquals("Tu pago de $45.000 fue recibido", respUser1.getBody().get(0).getMensaje());

        ResponseEntity<List<NotificacionDto>> respUser2 = controller.listar(2L);
        assertEquals(1, respUser2.getBody().size());

        ResponseEntity<List<NotificacionDto>> respSinDatos = controller.listar(999L);
        assertTrue(respSinDatos.getBody().isEmpty());
    }

    @Test
    void healthYOpenApi_respondenCorrectamente() {
        ResponseEntity<?> health = controller.health();
        assertEquals(HttpStatus.OK, health.getStatusCode());
        assertEquals("UP", ((Map<?, ?>) health.getBody()).get("status"));

        OpenAPI api = new OpenApiConfig().customOpenAPI();
        assertEquals("Microservicio de Notificaciones", api.getInfo().getTitle());
    }
}
