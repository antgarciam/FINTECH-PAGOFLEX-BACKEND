package com.pagoflex.autenticacion.dto;

import lombok.Data;

@Data
public class RecuperarRequest {
    private String rut;
    private String respuestaSeguridad;
    private String nuevaContrasena;
}