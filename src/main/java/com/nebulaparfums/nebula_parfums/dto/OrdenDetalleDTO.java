package com.nebulaparfums.nebula_parfums.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrdenDetalleDTO {
    private Integer id_orden_detalle;
    private Integer cantidad;
    private Double precio;
    private Integer id_orden;
    private Integer id_producto;
}
