package com.nebulaparfums.nebula_parfums.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nebulaparfums.nebula_parfums.model.Carrito;
import com.nebulaparfums.nebula_parfums.model.DireccionEnvio;
import com.nebulaparfums.nebula_parfums.model.Rol;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UsuarioDTO implements UserDetails {
    private Integer id;
    private String nombre;
    private String email;
    @JsonIgnore
    private String password;
    private Boolean estado;
    private LocalDate fecha_creacion;
    private Rol rol;
    private DireccionEnvio direccionEnvio;
    private Carrito carrito;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_"+rol.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}
