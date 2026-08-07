package com.nebulaparfums.nebula_parfums.dto;

import com.nebulaparfums.nebula_parfums.model.Rol;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UsuarioDTO {
    private String nombre;
    private String email;
    private String password;
    private Boolean estado;
    private LocalDate fecha_creacion;
    private Rol rol;
}
