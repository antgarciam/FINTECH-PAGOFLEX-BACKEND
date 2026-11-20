package com.pagoflex.usuarios.client;

import com.pagoflex.usuarios.dto.UsuarioDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Component
public class AuthClient {

    @Value("${auth.service.url}")
    private String authUrl;

    private final RestTemplate rest = new RestTemplate();

    public List<UsuarioDto> listarUsuarios() {
        ResponseEntity<List<UsuarioDto>> resp = rest.exchange(
                authUrl + "/usuarios",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<UsuarioDto>>() {}
        );
        return resp.getBody();
    }

    public UsuarioDto obtenerUsuario(Long id) {
        return rest.getForObject(authUrl + "/usuarios/" + id, UsuarioDto.class);
    }

    public UsuarioDto actualizarUsuario(Long id, Map<String, Object> body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        return rest.exchange(authUrl + "/usuarios/" + id,
                              HttpMethod.PUT, entity, UsuarioDto.class).getBody();
    }

    public void desactivarUsuario(Long id) {
    rest.delete(authUrl + "/usuarios/" + id);
}
}