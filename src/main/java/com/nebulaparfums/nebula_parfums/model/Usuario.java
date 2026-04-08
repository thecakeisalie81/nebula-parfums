package com.nebulaparfums.nebula_parfums.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Entity
@Getter @Setter
public class Usuario implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_usuario;
    private String nombre;
    @Email(message = "El correo debe tener una estructura valida")
    private String email;
    private String password;
    private Boolean estado;
    private LocalDate fecha_creacion;
    
    @ManyToOne
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

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(rol.getNombre_rol()));
    }

    @Override
    public String getUsername() {
        return this.email;
    }


    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
