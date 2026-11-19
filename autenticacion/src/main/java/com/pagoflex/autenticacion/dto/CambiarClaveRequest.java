package com.pagoflex.autenticacion.dto;

import lombok.Data;

@Data
public class CambiarClaveRequest {
    private String contrasenaActual;
    private String contrasenaNueva;
}