package com.pagoflex.autenticacion.service;

import com.pagoflex.autenticacion.dto.*;
import com.pagoflex.autenticacion.model.Usuario;
import com.pagoflex.autenticacion.repository.UsuarioRepository;
import com.pagoflex.autenticacion.security.JwtUtil;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository repo;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder encoder;

        public LoginResponse login(LoginRequest req) {
        // 👇 CAMBIAR findByCorreo por findByRut
        Usuario u = repo.findByRut(req.getRut())
                .orElseThrow(() -> new RuntimeException("RUT o contraseña incorrectos"));

        if (Boolean.FALSE.equals(u.getActivo())) {
            throw new RuntimeException("Cuenta desactivada. Contacta al soporte.");
        }
        if (!encoder.matches(req.getContrasena(), u.getContrasena())) {
            throw new RuntimeException("RUT o contraseña incorrectos");
        }

        String token = jwtUtil.generarToken(u.getId(), u.getCorreo(), u.getRolId());
        return new LoginResponse(token, u.getId(), u.getNombre(),
                                 u.getCorreo(), u.getRolId(), u.getCiudad());
    }

    public LoginResponse registro(RegistroRequest req) {
        if (repo.existsByCorreo(req.getCorreo())) {
            throw new RuntimeException("El correo ya está registrado");
        }
        if (repo.existsByRut(req.getRut())) {
            throw new RuntimeException("El RUT ya está registrado");
        }
        validarContrasena(req.getContrasena());

        Usuario u = new Usuario();
        u.setNombre(req.getNombre());
        u.setCorreo(req.getCorreo());
        u.setRut(req.getRut());
        u.setContrasena(encoder.encode(req.getContrasena()));
        u.setCiudad(req.getCiudad());
        u.setRolId(3); // Rol por defecto para usuarios registrados
        u.setActivo(true);
        u.setPreguntaSeguridad(req.getPreguntaSeguridad());
        u.setRespuestaSeguridad(encoder.encode(req.getRespuestaSeguridad()));

        Usuario guardado = repo.save(u);
        String token = jwtUtil.generarToken(guardado.getId(),
                                             guardado.getCorreo(),
                                             guardado.getRolId());
        return new LoginResponse(token, guardado.getId(), guardado.getNombre(),
                                 guardado.getCorreo(), guardado.getRolId(),
                                 guardado.getCiudad());
    }

    public void cambiarClave(String correo, CambiarClaveRequest req) {
        Usuario u = repo.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!encoder.matches(req.getContrasenaActual(), u.getContrasena())) {
            throw new RuntimeException("La contraseña actual no es correcta");
        }
        validarContrasena(req.getContrasenaNueva());
        u.setContrasena(encoder.encode(req.getContrasenaNueva()));
        repo.save(u);
    }

    public void recuperar(RecuperarRequest req) {
        Usuario u = repo.findByRut(req.getRut())
                .orElseThrow(() -> new RuntimeException("RUT no registrado"));

        if (!encoder.matches(req.getRespuestaSeguridad(), u.getRespuestaSeguridad())) {
            throw new RuntimeException("Respuesta de seguridad incorrecta");
        }
        validarContrasena(req.getNuevaContrasena());
        u.setContrasena(encoder.encode(req.getNuevaContrasena()));
        repo.save(u);
    }

        public List<UsuarioDto> listarUsuarios() {
        return repo.findAll().stream()
                .filter(Usuario::getActivo)
                .map(u -> new UsuarioDto(u.getId(), u.getNombre(), u.getCorreo(),
                                          u.getRut(), u.getCiudad(), u.getRolId(), u.getActivo()))
                .toList();
    }

    public UsuarioDto obtenerUsuario(Long id) {
        Usuario u = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return new UsuarioDto(u.getId(), u.getNombre(), u.getCorreo(),
                               u.getRut(), u.getCiudad(), u.getRolId(), u.getActivo());
    }

    public UsuarioDto actualizarUsuario(Long id, ActualizarUsuarioRequest req) {
        Usuario u = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        if (req.getNombre() != null) u.setNombre(req.getNombre());
        if (req.getCorreo() != null) u.setCorreo(req.getCorreo());
        if (req.getCiudad() != null) u.setCiudad(req.getCiudad());
        repo.save(u);
        return new UsuarioDto(u.getId(), u.getNombre(), u.getCorreo(),
                               u.getRut(), u.getCiudad(), u.getRolId(), u.getActivo());
    }

    public void desactivarUsuario(Long id) {
        Usuario u = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        u.setActivo(false);
        repo.save(u);
    }

    private void validarContrasena(String c) {
        if (c == null || c.length() < 8) {
            throw new RuntimeException("La contraseña debe tener al menos 8 caracteres");
        }
        if (!c.matches(".*[A-Z].*")) {
            throw new RuntimeException("La contraseña debe tener al menos una mayúscula");
        }
        if (!c.matches(".*[a-z].*")) {
            throw new RuntimeException("La contraseña debe tener al menos una minúscula");
        }
        if (!c.matches(".*\\d.*")) {
            throw new RuntimeException("La contraseña debe tener al menos un número");
        }
        if (!c.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*")) {
            throw new RuntimeException("La contraseña debe tener al menos un carácter especial");
        }
    }
}