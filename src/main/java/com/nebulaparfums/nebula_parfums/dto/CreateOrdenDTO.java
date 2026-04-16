package com.nebulaparfums.nebula_parfums.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrdenDTO {
    private Integer id_direccion;
    private Integer id_usuario;
    private Double total;
    private Integer id_carrito;
}
