package com.nebulaparfums.nebula_parfums.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ProveedorDTO {
    private String nombre;
    private String contacto;
    private String telefono;
    private String email;
}
