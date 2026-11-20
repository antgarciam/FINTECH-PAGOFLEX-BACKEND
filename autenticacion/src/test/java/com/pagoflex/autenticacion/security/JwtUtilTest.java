package com.pagoflex.autenticacion.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", "pagoflex-secreto-super-largo-para-jwt-1234567890");
        ReflectionTestUtils.setField(jwtUtil, "expiration", 86400000L);
    }

    @Test
    void generarYValidarToken_extraeCorreoCorrectamente() {
        String token = jwtUtil.generarToken(1L, "usuario@pagoflex.cl", 3);

        assertNotNull(token);
        assertTrue(jwtUtil.esValido(token));
        assertEquals("usuario@pagoflex.cl", jwtUtil.extraerCorreo(token));
    }

    @Test
    void esValido_tokenInvalidoOExpirado_retornaFalse() {
        assertFalse(jwtUtil.esValido("token.falso.invalido"));
        assertFalse(jwtUtil.esValido(null));

        ReflectionTestUtils.setField(jwtUtil, "expiration", -10000L);
        String tokenExpirado = jwtUtil.generarToken(1L, "usuario@pagoflex.cl", 3);
        assertFalse(jwtUtil.esValido(tokenExpirado));
    }
}
