package com.pagoflex.pagos.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "cuotas")
public class Cuota {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long compromisoId;

    @Column(nullable = false)
    private Integer numeroCuota;

    @Column(nullable = false)
    private Integer monto;

    @Column(nullable = false)
    private String fechaVencimiento;

    @Column(nullable = false)
    private String estado;
}