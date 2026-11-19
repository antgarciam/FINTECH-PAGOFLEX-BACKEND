package com.pagoflex.pagos.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "compromisos")
public class Compromiso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long usuarioId;

    @Column(nullable = false)
    private String empresa;

    @Column(nullable = false)
    private String concepto;

    @Column(nullable = false)
    private Integer montoTotal;

    @Column(nullable = false)
    private Integer numeroCuotas;

    @Column(nullable = false)
    private String fechaInicio;

    @Column(nullable = false)
    private Boolean activo = true;
}