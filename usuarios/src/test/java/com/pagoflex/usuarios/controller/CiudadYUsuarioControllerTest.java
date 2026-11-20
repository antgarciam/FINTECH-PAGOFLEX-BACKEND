package com.pagoflex.usuarios.controller;

import com.pagoflex.usuarios.client.AuthClient;
import com.pagoflex.usuarios.dto.UsuarioDto;
import com.pagoflex.usuarios.model.Ciudad;
import com.pagoflex.usuarios.repository.CiudadRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CiudadYUsuarioControllerTest {

    @Mock
    private CiudadRepository ciudadRepo;

    @Mock
    private AuthClient authClient;

    @InjectMocks
    private CiudadController ciudadController;

    @InjectMocks
    private UsuarioController usuarioController;

    @Test
    void ciudadController_endpointsResponden200() {
        Ciudad c = new Ciudad(1L, "Santiago", "Metropolitana");
        when(ciudadRepo.findAll()).thenReturn(List.of(c));
        when(ciudadRepo.save(c)).thenReturn(c);

        assertEquals(HttpStatus.OK, ciudadController.listar().getStatusCode());
        assertEquals(HttpStatus.OK, ciudadController.crear(c).getStatusCode());
        assertEquals(HttpStatus.OK, ciudadController.health().getStatusCode());
    }

    @Test
    void usuarioController_endpointsResponden200() {
        UsuarioDto dto = new UsuarioDto(1L, "Ana", "ana@mail.com", "1-9", "Santiago", 3, true);
        when(authClient.listarUsuarios()).thenReturn(List.of(dto));
        when(authClient.obtenerUsuario(1L)).thenReturn(dto);
        when(authClient.actualizarUsuario(eq(1L), anyMap())).thenReturn(dto);

        assertEquals(HttpStatus.OK, usuarioController.listar().getStatusCode());
        assertEquals(HttpStatus.OK, usuarioController.obtener(1L).getStatusCode());
        assertEquals(HttpStatus.OK, usuarioController.actualizar(1L, Map.of("nombre", "Ana")).getStatusCode());
    }
}
