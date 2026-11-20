package com.pagoflex.autenticacion.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Microservicio de Autenticación")
                        .version("1.0")
                        .description("Documentación de endpoints de autenticación"))
                .servers(List.of(
                        new Server().url("/").description("API Gateway")
                ));
    }
}