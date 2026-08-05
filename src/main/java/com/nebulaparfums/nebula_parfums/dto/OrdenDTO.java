package com.nebulaparfums.nebula_parfums.dto;

import com.nebulaparfums.nebula_parfums.model.EstadoOrden;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrdenDTO {
    private Integer id_cliente;
    private String direccion;
    private Integer id_orden;
    private EstadoOrden estado;
    private Double total;
}
