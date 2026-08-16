package com.nebulaparfums.nebula_parfums.auth;

import com.nebulaparfums.nebula_parfums.model.Rol;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterRequest {
    private String nombre;
    private String email;
    private String password;
    private Rol rol;
}
