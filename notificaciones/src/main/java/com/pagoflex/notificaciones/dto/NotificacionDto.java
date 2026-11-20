package com.pagoflex.notificaciones.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NotificacionDto {
    private Long id;
    private Long usuarioId;
    private String mensaje;
    private Boolean leida;
    private String fechaHora;
}