package com.pagoflex.autenticacion.dto;

import lombok.Data;

@Data
public class ActualizarUsuarioRequest {
    private String nombre;
    private String correo;
    private String ciudad;
}