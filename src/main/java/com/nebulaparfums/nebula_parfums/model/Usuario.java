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

    public Usuario(Integer id_usuario, String nombre, String email, String password, Boolean estado, LocalDate fecha_creacion, Rol rol, Carrito carrito, DireccionEnvio direccionEnvio) {
        this.id_usuario = id_usuario;
        this.nombre = nombre;
        this.email = email;
        this.password = password;
        this.estado = estado;
        this.fecha_creacion = fecha_creacion;
        this.rol = rol;
        this.carrito = carrito;
        this.direccionEnvio = direccionEnvio;
    }
}
