package com.nebulaparfums.nebula_parfums.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter @Setter
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_usuario;
    private String nombre;
    private String email;
    private String password;
    private Boolean estado;
    private LocalDate fecha_creacion;
    
    @OneToOne
    @JoinColumn(name = "id_rol", referencedColumnName = "id_rol")
    private Rol rol;

    @OneToOne
    @JoinColumn(name = "carrito", referencedColumnName = "id_carrito")
    private Carrito carrito;

    @OneToOne
    @JoinColumn(name = "direccion", referencedColumnName = "id_direccion")
    private DireccionEnvio direccionEnvio;
    public Usuario() {
    }
}
