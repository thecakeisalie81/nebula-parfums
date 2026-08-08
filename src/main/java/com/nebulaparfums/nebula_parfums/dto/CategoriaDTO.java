package com.nebulaparfums.nebula_parfums.dto;

import lombok.*;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoriaDTO {
    Integer id;
    String nombre;
    String descripcion;
}
