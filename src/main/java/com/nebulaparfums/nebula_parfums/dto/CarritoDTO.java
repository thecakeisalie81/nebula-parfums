package com.nebulaparfums.nebula_parfums.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CarritoDTO {
    private Integer id_carrito;
    private LocalDateTime fecha_actualizacion;
    private List<CarritoDetalleDTO> listaCarritoDetalles;
    private Integer id_usuario;
    private Double total;
}
