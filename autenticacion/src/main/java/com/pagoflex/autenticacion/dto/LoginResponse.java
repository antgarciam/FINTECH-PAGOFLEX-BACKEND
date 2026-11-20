package com.pagoflex.autenticacion.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private Long id;
    private String nombre;
    private String correo;
    private Integer rolId;
    private String ciudad;
}