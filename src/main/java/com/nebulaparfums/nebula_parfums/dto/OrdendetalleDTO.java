package com.nebulaparfums.nebula_parfums.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrdendetalleDTO {
    private int cantidad;
    private Double precio;
    private Integer id_orden;
    private Integer id_producto;
}
