package com.pagoflex.autenticacion.config;

import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfigurationSource;

import static org.junit.jupiter.api.Assertions.*;

class ConfigTest {

    @Test
    void openApiPasswordYSecurityConfig_funcionanCorrectamente() {
        OpenApiConfig openApiConfig = new OpenApiConfig();
        OpenAPI api = openApiConfig.customOpenAPI();
        assertNotNull(api);
        assertEquals("Microservicio de Autenticación", api.getInfo().getTitle());

        PasswordConfig passwordConfig = new PasswordConfig();
        PasswordEncoder encoder = passwordConfig.passwordEncoder();
        String hash = encoder.encode("Test1234!");
        assertTrue(encoder.matches("Test1234!", hash));

        SecurityConfig securityConfig = new SecurityConfig();
        CorsConfigurationSource cors = securityConfig.corsConfigurationSource();
        assertNotNull(cors);
    }
}
