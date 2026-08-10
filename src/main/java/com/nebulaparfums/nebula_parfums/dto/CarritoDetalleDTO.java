package com.nebulaparfums.nebula_parfums.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CarritoDetalleDTO {
    private Integer id_carrito_detalle;
    private Integer cantidad;
    private Double precio;
    private Integer id_producto;
    private Integer id_carrito;
}
