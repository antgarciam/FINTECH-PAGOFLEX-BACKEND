package com.pagoflex.autenticacion.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UsuarioDto {
    private Long id;
    private String nombre;
    private String correo;
    private String rut;
    private String ciudad;
    private Integer rolId;
    private Boolean activo;
}