package com.nebulaparfums.nebula_parfums.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductoDTO {
    private String nombre;
    private String descripcion;
    private Double precio;
    private int stock_actual;
    private int stock_minimo;
    private Integer categoria;
    private Integer proveedor;
    private String imagen;
}
