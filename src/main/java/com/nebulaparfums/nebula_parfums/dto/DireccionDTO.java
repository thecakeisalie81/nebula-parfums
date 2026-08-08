package com.nebulaparfums.nebula_parfums.dto;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DireccionDTO {
    private Integer id_direccion;
    private String direccion;
    private String ciudad;
    private String provincia;
    private String codigo_postal;
    private String telefono;
}
