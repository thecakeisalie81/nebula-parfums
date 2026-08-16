package com.nebulaparfums.nebula_parfums.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;


@Entity
@Getter @Setter
@Builder
public class Usuario{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_usuario;
    private String nombre;

    @Email(message = "El correo debe tener una estructura valida")
    @Column(nullable = false, unique = true)
    private String email;

    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    @Column(nullable = false)
    private String password;

    private Boolean estado;
    private LocalDate fecha_creacion;

    @Enumerated(EnumType.STRING)
    private Rol rol;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "carrito", referencedColumnName = "id_carrito")
    @JsonManagedReference
    private Carrito carrito;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
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
