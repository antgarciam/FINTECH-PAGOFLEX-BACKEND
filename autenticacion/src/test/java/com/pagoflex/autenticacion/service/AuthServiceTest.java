package com.pagoflex.autenticacion.service;

import com.pagoflex.autenticacion.dto.*;
import com.pagoflex.autenticacion.model.Usuario;
import com.pagoflex.autenticacion.repository.UsuarioRepository;
import com.pagoflex.autenticacion.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioRepository repo;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private PasswordEncoder encoder;

    @InjectMocks
    private AuthService authService;

    private Usuario usuarioBase;

    @BeforeEach
    void setUp() {
        usuarioBase = new Usuario();
        usuarioBase.setId(1L);
        usuarioBase.setNombre("Anthuan");
        usuarioBase.setCorreo("anthuan@pagoflex.cl");
        usuarioBase.setRut("27044812-7");
        usuarioBase.setContrasena("$2a$10$hashedPassword");
        usuarioBase.setCiudad("Santiago");
        usuarioBase.setRolId(3);
        usuarioBase.setActivo(true);
        usuarioBase.setPreguntaSeguridad("¿Mascota?");
        usuarioBase.setRespuestaSeguridad("$2a$10$hashedAnswer");
    }

    @Test
    void loginExitoso_retornaLoginResponseConToken() {
        LoginRequest req = new LoginRequest();
        req.setRut("27044812-7");
        req.setContrasena("ClaveSegura1!");

        when(repo.findByRut("27044812-7")).thenReturn(Optional.of(usuarioBase));
        when(encoder.matches("ClaveSegura1!", usuarioBase.getContrasena())).thenReturn(true);
        when(jwtUtil.generarToken(1L, "anthuan@pagoflex.cl", 3)).thenReturn("jwt-token-123");

        LoginResponse resp = authService.login(req);

        assertNotNull(resp);
        assertEquals("jwt-token-123", resp.getToken());
        assertEquals(1L, resp.getId());
        assertEquals("Anthuan", resp.getNombre());
        assertEquals("anthuan@pagoflex.cl", resp.getCorreo());
        assertEquals(3, resp.getRolId());
        assertEquals("Santiago", resp.getCiudad());
    }

    @Test
    void login_rutNoEncontrado_lanzaExcepcion() {
        LoginRequest req = new LoginRequest();
        req.setRut("11111111-1");
        req.setContrasena("ClaveSegura1!");

        when(repo.findByRut("11111111-1")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.login(req));
        assertEquals("RUT o contraseña incorrectos", ex.getMessage());
    }

    @Test
    void login_cuentaDesactivada_lanzaExcepcion() {
        usuarioBase.setActivo(false);
        LoginRequest req = new LoginRequest();
        req.setRut("27044812-7");
        req.setContrasena("ClaveSegura1!");

        when(repo.findByRut("27044812-7")).thenReturn(Optional.of(usuarioBase));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.login(req));
        assertEquals("Cuenta desactivada. Contacta al soporte.", ex.getMessage());
    }

    @Test
    void login_contrasenaIncorrecta_lanzaExcepcion() {
        LoginRequest req = new LoginRequest();
        req.setRut("27044812-7");
        req.setContrasena("Incorrecta1!");

        when(repo.findByRut("27044812-7")).thenReturn(Optional.of(usuarioBase));
        when(encoder.matches("Incorrecta1!", usuarioBase.getContrasena())).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.login(req));
        assertEquals("RUT o contraseña incorrectos", ex.getMessage());
    }

    @Test
    void registroExitoso_guardaUsuarioYRetornaToken() {
        RegistroRequest req = new RegistroRequest();
        req.setNombre("Anthuan");
        req.setCorreo("anthuan@pagoflex.cl");
        req.setRut("27044812-7");
        req.setContrasena("ClaveSegura1!");
        req.setCiudad("Santiago");
        req.setPreguntaSeguridad("¿Mascota?");
        req.setRespuestaSeguridad("Firulais");

        when(repo.existsByCorreo("anthuan@pagoflex.cl")).thenReturn(false);
        when(repo.existsByRut("27044812-7")).thenReturn(false);
        when(encoder.encode("ClaveSegura1!")).thenReturn("encodedPass");
        when(encoder.encode("Firulais")).thenReturn("encodedAnswer");
        when(repo.save(any(Usuario.class))).thenAnswer(inv -> {
            Usuario u = inv.getArgument(0);
            u.setId(5L);
            return u;
        });
        when(jwtUtil.generarToken(5L, "anthuan@pagoflex.cl", 3)).thenReturn("token-nuevo");

        LoginResponse resp = authService.registro(req);

        assertEquals("token-nuevo", resp.getToken());
        assertEquals(5L, resp.getId());
        assertEquals(3, resp.getRolId());
        verify(repo).save(any(Usuario.class));
    }

    @Test
    void registro_correoDuplicado_lanzaExcepcion() {
        RegistroRequest req = new RegistroRequest();
        req.setCorreo("anthuan@pagoflex.cl");
        when(repo.existsByCorreo("anthuan@pagoflex.cl")).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.registro(req));
        assertEquals("El correo ya está registrado", ex.getMessage());
    }

    @Test
    void registro_rutDuplicado_lanzaExcepcion() {
        RegistroRequest req = new RegistroRequest();
        req.setCorreo("nuevo@pagoflex.cl");
        req.setRut("27044812-7");
        when(repo.existsByCorreo("nuevo@pagoflex.cl")).thenReturn(false);
        when(repo.existsByRut("27044812-7")).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.registro(req));
        assertEquals("El RUT ya está registrado", ex.getMessage());
    }

    @Test
    void registro_validacionContrasena_cubreTodasLasReglas() {
        RegistroRequest req = new RegistroRequest();
        req.setCorreo("nuevo@pagoflex.cl");
        req.setRut("12345678-9");
        when(repo.existsByCorreo(anyString())).thenReturn(false);
        when(repo.existsByRut(anyString())).thenReturn(false);

        // 1. Corta (< 8)
        req.setContrasena("Ab1!");
        assertEquals("La contraseña debe tener al menos 8 caracteres",
                assertThrows(RuntimeException.class, () -> authService.registro(req)).getMessage());

        // 2. Sin mayúscula
        req.setContrasena("clavesegura1!");
        assertEquals("La contraseña debe tener al menos una mayúscula",
                assertThrows(RuntimeException.class, () -> authService.registro(req)).getMessage());

        // 3. Sin minúscula
        req.setContrasena("CLAVESEGURA1!");
        assertEquals("La contraseña debe tener al menos una minúscula",
                assertThrows(RuntimeException.class, () -> authService.registro(req)).getMessage());

        // 4. Sin número
        req.setContrasena("ClaveSegura!");
        assertEquals("La contraseña debe tener al menos un número",
                assertThrows(RuntimeException.class, () -> authService.registro(req)).getMessage());

        // 5. Sin carácter especial
        req.setContrasena("ClaveSegura1");
        assertEquals("La contraseña debe tener al menos un carácter especial",
                assertThrows(RuntimeException.class, () -> authService.registro(req)).getMessage());
    }

    @Test
    void cambiarClave_exitoso_actualizaContrasena() {
        CambiarClaveRequest req = new CambiarClaveRequest();
        req.setContrasenaActual("ClaveActual1!");
        req.setContrasenaNueva("ClaveNueva2@");

        when(repo.findByCorreo("anthuan@pagoflex.cl")).thenReturn(Optional.of(usuarioBase));
        when(encoder.matches("ClaveActual1!", usuarioBase.getContrasena())).thenReturn(true);
        when(encoder.encode("ClaveNueva2@")).thenReturn("newEncodedPass");

        authService.cambiarClave("anthuan@pagoflex.cl", req);

        assertEquals("newEncodedPass", usuarioBase.getContrasena());
        verify(repo).save(usuarioBase);
    }

    @Test
    void cambiarClave_usuarioNoEncontrado_lanzaExcepcion() {
        CambiarClaveRequest req = new CambiarClaveRequest();
        when(repo.findByCorreo("noexiste@pagoflex.cl")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> authService.cambiarClave("noexiste@pagoflex.cl", req));
        assertEquals("Usuario no encontrado", ex.getMessage());
    }

    @Test
    void cambiarClave_contrasenaActualIncorrecta_lanzaExcepcion() {
        CambiarClaveRequest req = new CambiarClaveRequest();
        req.setContrasenaActual("Mala1!");
        req.setContrasenaNueva("ClaveNueva2@");

        when(repo.findByCorreo("anthuan@pagoflex.cl")).thenReturn(Optional.of(usuarioBase));
        when(encoder.matches("Mala1!", usuarioBase.getContrasena())).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> authService.cambiarClave("anthuan@pagoflex.cl", req));
        assertEquals("La contraseña actual no es correcta", ex.getMessage());
    }

    @Test
    void recuperar_exitoso_restableceContrasena() {
        RecuperarRequest req = new RecuperarRequest();
        req.setRut("27044812-7");
        req.setRespuestaSeguridad("Firulais");
        req.setNuevaContrasena("NuevaClave1#");

        when(repo.findByRut("27044812-7")).thenReturn(Optional.of(usuarioBase));
        when(encoder.matches("Firulais", usuarioBase.getRespuestaSeguridad())).thenReturn(true);
        when(encoder.encode("NuevaClave1#")).thenReturn("encodedRecovered");

        authService.recuperar(req);

        assertEquals("encodedRecovered", usuarioBase.getContrasena());
        verify(repo).save(usuarioBase);
    }

    @Test
    void recuperar_rutNoRegistrado_lanzaExcepcion() {
        RecuperarRequest req = new RecuperarRequest();
        req.setRut("99999999-9");
        when(repo.findByRut("99999999-9")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.recuperar(req));
        assertEquals("RUT no registrado", ex.getMessage());
    }

    @Test
    void recuperar_respuestaIncorrecta_lanzaExcepcion() {
        RecuperarRequest req = new RecuperarRequest();
        req.setRut("27044812-7");
        req.setRespuestaSeguridad("Incorrecta");
        req.setNuevaContrasena("NuevaClave1#");

        when(repo.findByRut("27044812-7")).thenReturn(Optional.of(usuarioBase));
        when(encoder.matches("Incorrecta", usuarioBase.getRespuestaSeguridad())).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.recuperar(req));
        assertEquals("Respuesta de seguridad incorrecta", ex.getMessage());
    }

    @Test
    void listarUsuarios_filtraSoloActivos() {
        Usuario inactivo = new Usuario();
        inactivo.setId(2L);
        inactivo.setNombre("Inactivo");
        inactivo.setActivo(false);

        when(repo.findAll()).thenReturn(List.of(usuarioBase, inactivo));

        List<UsuarioDto> lista = authService.listarUsuarios();

        assertEquals(1, lista.size());
        assertEquals("Anthuan", lista.get(0).getNombre());
    }

    @Test
    void obtenerUsuario_exitosoYNoEncontrado() {
        when(repo.findById(1L)).thenReturn(Optional.of(usuarioBase));
        when(repo.findById(99L)).thenReturn(Optional.empty());

        UsuarioDto dto = authService.obtenerUsuario(1L);
        assertEquals("anthuan@pagoflex.cl", dto.getCorreo());

        assertThrows(RuntimeException.class, () -> authService.obtenerUsuario(99L));
    }

    @Test
    void actualizarUsuario_actualizaCamposNoNulosYConservaNulos() {
        ActualizarUsuarioRequest req = new ActualizarUsuarioRequest();
        req.setNombre("Anthuan Actualizado");
        req.setCorreo("nuevo@pagoflex.cl");
        req.setCiudad("Valparaíso");

        when(repo.findById(1L)).thenReturn(Optional.of(usuarioBase));

        UsuarioDto actualizado = authService.actualizarUsuario(1L, req);
        assertEquals("Anthuan Actualizado", actualizado.getNombre());
        assertEquals("nuevo@pagoflex.cl", actualizado.getCorreo());
        assertEquals("Valparaíso", actualizado.getCiudad());

        // Campos nulos no sobreescriben
        ActualizarUsuarioRequest reqNulo = new ActualizarUsuarioRequest();
        UsuarioDto sinCambios = authService.actualizarUsuario(1L, reqNulo);
        assertEquals("Anthuan Actualizado", sinCambios.getNombre());
    }

    @Test
    void desactivarUsuario_marcaActivoFalse() {
        when(repo.findById(1L)).thenReturn(Optional.of(usuarioBase));

        authService.desactivarUsuario(1L);

        assertFalse(usuarioBase.getActivo());
        verify(repo).save(usuarioBase);
    }
}
