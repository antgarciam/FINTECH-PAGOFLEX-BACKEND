package com.pagoflex.autenticacion.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false, unique = true)
    private String correo;

    @Column(nullable = false, unique = true)
    private String rut;

    @Column(nullable = false)
    private String contrasena;

    @Column(nullable = false)
    private String ciudad;

    @Column(name = "id_rol", nullable = false)
    private Integer rolId = 1;

    @Column(nullable = false)
    private Boolean activo = true;

    private String preguntaSeguridad;
    private String respuestaSeguridad;
}