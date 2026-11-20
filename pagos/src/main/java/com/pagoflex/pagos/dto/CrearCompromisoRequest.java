package com.pagoflex.pagos.dto;

import lombok.Data;

@Data
public class CrearCompromisoRequest {
    private Long usuarioId;
    private String empresa;
    private String concepto;
    private Integer montoTotal;
    private Integer numeroCuotas;
    private String fechaInicio; // Formato "dd-MM-yyyy"
}