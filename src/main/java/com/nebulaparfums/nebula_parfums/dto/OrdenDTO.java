package com.nebulaparfums.nebula_parfums.dto;

import com.nebulaparfums.nebula_parfums.model.EstadoOrden;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrdenDTO {
    private Integer id_orden;
    private Integer id_cliente;
    private String direccion;
    private EstadoOrden estado;
    private LocalDateTime fecha_orden;
    private Double total;
    private List<OrdenDetalleDTO> ordenDetalles;
}
