package com.pagoflex.reportes.config;

import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OpenApiConfigTest {
    @Test
    void customOpenAPI_configuraTituloYServidor() {
        OpenAPI api = new OpenApiConfig().customOpenAPI();
        assertNotNull(api);
        assertEquals("Microservicio de Reportes", api.getInfo().getTitle());
    }
}
