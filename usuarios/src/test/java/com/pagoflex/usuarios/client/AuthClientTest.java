package com.pagoflex.usuarios.client;

import com.pagoflex.usuarios.dto.UsuarioDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthClientTest {

    @Mock
    private RestTemplate restTemplate;

    private AuthClient authClient;

    @BeforeEach
    void setUp() {
        authClient = new AuthClient();
        ReflectionTestUtils.setField(authClient, "authUrl", "http://localhost:8081/api/auth");
        ReflectionTestUtils.setField(authClient, "rest", restTemplate);
    }

    @Test
    @SuppressWarnings("unchecked")
    void listarObtenerYActualizarUsuarios_delegaEnRestTemplate() {
        UsuarioDto dto = new UsuarioDto(1L, "Ana", "ana@mail.com", "1-9", "Santiago", 3, true);

        when(restTemplate.exchange(
                eq("http://localhost:8081/api/auth/usuarios"),
                eq(HttpMethod.GET),
                isNull(),
                any(ParameterizedTypeReference.class)
        )).thenReturn(ResponseEntity.ok(List.of(dto)));

        List<UsuarioDto> lista = authClient.listarUsuarios();
        assertEquals(1, lista.size());
        assertEquals("Ana", lista.get(0).getNombre());

        when(restTemplate.getForObject("http://localhost:8081/api/auth/usuarios/1", UsuarioDto.class))
                .thenReturn(dto);
        assertEquals("ana@mail.com", authClient.obtenerUsuario(1L).getCorreo());

        when(restTemplate.exchange(
                eq("http://localhost:8081/api/auth/usuarios/1"),
                eq(HttpMethod.PUT),
                any(HttpEntity.class),
                eq(UsuarioDto.class)
        )).thenReturn(ResponseEntity.ok(dto));
        UsuarioDto actualizado = authClient.actualizarUsuario(1L, Map.of("nombre", "Ana"));
        assertEquals(1L, actualizado.getId());
    }
}
