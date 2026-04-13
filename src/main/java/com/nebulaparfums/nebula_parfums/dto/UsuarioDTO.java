package com.nebulaparfums.nebula_parfums.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioDTO {
    private Integer id_usuario;
    private String nombre;
    private String email;
    private Boolean estado;
    private LocalDate fecha_creacion;
    private String rol;
}
