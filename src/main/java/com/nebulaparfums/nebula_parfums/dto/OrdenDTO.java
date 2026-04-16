package com.nebulaparfums.nebula_parfums.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrdenDTO {
    private Integer id_cliente;
    private Integer id_direccion;
    private Integer id_orden;
    private String estado;
}
