package com.pagoflex.autenticacion.controller;

import com.pagoflex.autenticacion.dto.*;
import com.pagoflex.autenticacion.security.JwtUtil;
import com.pagoflex.autenticacion.service.AuthService;
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
class AuthControllerTest {

    @Mock
    private AuthService service;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthController controller;

    @Test
    void login_exitosoYError401() {
        LoginRequest req = new LoginRequest();
        LoginResponse respDto = new LoginResponse("tok", 1L, "Ana", "ana@mail.com", 3, "Santiago");
        when(service.login(req)).thenReturn(respDto);

        ResponseEntity<?> ok = controller.login(req);
        assertEquals(HttpStatus.OK, ok.getStatusCode());
        assertEquals(respDto, ok.getBody());

        when(service.login(req)).thenThrow(new RuntimeException("Credenciales inválidas"));
        ResponseEntity<?> unauth = controller.login(req);
        assertEquals(HttpStatus.UNAUTHORIZED, unauth.getStatusCode());
    }

    @Test
    void registro_exitosoYError400() {
        RegistroRequest req = new RegistroRequest();
        LoginResponse respDto = new LoginResponse("tok", 1L, "Ana", "ana@mail.com", 3, "Santiago");
        when(service.registro(req)).thenReturn(respDto);

        ResponseEntity<?> ok = controller.registro(req);
        assertEquals(HttpStatus.OK, ok.getStatusCode());

        when(service.registro(req)).thenThrow(new RuntimeException("Correo duplicado"));
        ResponseEntity<?> bad = controller.registro(req);
        assertEquals(HttpStatus.BAD_REQUEST, bad.getStatusCode());
    }

    @Test
    void cambiarClave_tokenValidoInvalidoYErrorServicio() {
        CambiarClaveRequest req = new CambiarClaveRequest();

        // Token inválido -> 401
        when(jwtUtil.esValido("bad-token")).thenReturn(false);
        ResponseEntity<?> r401 = controller.cambiarClave("Bearer bad-token", req);
        assertEquals(HttpStatus.UNAUTHORIZED, r401.getStatusCode());

        // Token válido -> 200
        when(jwtUtil.esValido("good-token")).thenReturn(true);
        when(jwtUtil.extraerCorreo("good-token")).thenReturn("ana@mail.com");
        ResponseEntity<?> r200 = controller.cambiarClave("Bearer good-token", req);
        assertEquals(HttpStatus.OK, r200.getStatusCode());
        verify(service).cambiarClave("ana@mail.com", req);

        // Excepción en servicio -> 400
        doThrow(new RuntimeException("Clave incorrecta")).when(service).cambiarClave("ana@mail.com", req);
        ResponseEntity<?> r400 = controller.cambiarClave("Bearer good-token", req);
        assertEquals(HttpStatus.BAD_REQUEST, r400.getStatusCode());
    }

    @Test
    void recuperar_exitosoYError400() {
        RecuperarRequest req = new RecuperarRequest();
        ResponseEntity<?> ok = controller.recuperar(req);
        assertEquals(HttpStatus.OK, ok.getStatusCode());

        doThrow(new RuntimeException("RUT no registrado")).when(service).recuperar(req);
        ResponseEntity<?> bad = controller.recuperar(req);
        assertEquals(HttpStatus.BAD_REQUEST, bad.getStatusCode());
    }

    @Test
    void endpointsUsuariosYHealth() {
        UsuarioDto dto = new UsuarioDto(1L, "Ana", "ana@mail.com", "1-9", "Santiago", 3, true);
        when(service.listarUsuarios()).thenReturn(List.of(dto));
        assertEquals(HttpStatus.OK, controller.listarUsuarios().getStatusCode());

        when(service.obtenerUsuario(1L)).thenReturn(dto);
        assertEquals(HttpStatus.OK, controller.obtenerUsuario(1L).getStatusCode());

        when(service.obtenerUsuario(99L)).thenThrow(new RuntimeException("No existe"));
        assertEquals(HttpStatus.NOT_FOUND, controller.obtenerUsuario(99L).getStatusCode());

        ActualizarUsuarioRequest upd = new ActualizarUsuarioRequest();
        when(service.actualizarUsuario(1L, upd)).thenReturn(dto);
        assertEquals(HttpStatus.OK, controller.actualizarUsuario(1L, upd).getStatusCode());

        when(service.actualizarUsuario(99L, upd)).thenThrow(new RuntimeException("Error"));
        assertEquals(HttpStatus.BAD_REQUEST, controller.actualizarUsuario(99L, upd).getStatusCode());

        assertEquals(HttpStatus.OK, controller.desactivarUsuario(1L).getStatusCode());
        doThrow(new RuntimeException("Error")).when(service).desactivarUsuario(99L);
        assertEquals(HttpStatus.BAD_REQUEST, controller.desactivarUsuario(99L).getStatusCode());

        ResponseEntity<?> health = controller.health();
        assertEquals(HttpStatus.OK, health.getStatusCode());
        assertEquals("UP", ((Map<?, ?>) health.getBody()).get("status"));
    }
}
