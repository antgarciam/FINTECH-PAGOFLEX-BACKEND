package com.pagoflex.autenticacion.dto;

import lombok.Data;

@Data
public class RegistroRequest {
    private String nombre;
    private String correo;
    private String rut;
    private String contrasena;
    private String ciudad;
    private String preguntaSeguridad;
    private String respuestaSeguridad;
}