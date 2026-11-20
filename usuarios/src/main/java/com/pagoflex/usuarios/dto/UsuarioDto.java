package com.pagoflex.usuarios.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
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