package com.pagoflex.usuarios.config;

import com.pagoflex.usuarios.repository.CiudadRepository;
import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DataInitializerTest {

    @Mock
    private CiudadRepository repo;

    @InjectMocks
    private DataInitializer initializer;

    @Test
    void run_inserta12CiudadesCuandoTablaEstaVacia() {
        when(repo.count()).thenReturn(0L);
        initializer.run();
        verify(repo).saveAll(argThat(iterable -> {
            int count = 0;
            for (Object ignored : iterable) count++;
            return count == 12;
        }));
    }

    @Test
    void run_noInsertaSiYaExistenCiudades() {
        when(repo.count()).thenReturn(12L);
        initializer.run();
        verify(repo, never()).saveAll(anyList());
    }

    @Test
    void openApiConfig_configuraTitulo() {
        OpenAPI api = new OpenApiConfig().customOpenAPI();
        assertEquals("Microservicio de Usuarios", api.getInfo().getTitle());
    }
}
