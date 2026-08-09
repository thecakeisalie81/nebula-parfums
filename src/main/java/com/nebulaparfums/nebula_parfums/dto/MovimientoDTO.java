package com.nebulaparfums.nebula_parfums.dto;

import com.nebulaparfums.nebula_parfums.model.TipoMovimiento;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MovimientoDTO {
    @Enumerated(EnumType.STRING)
    private TipoMovimiento tipo_movimiento;
    private Integer cantidad;
    private LocalDateTime fecha_movimiento;
    private Integer id_producto;
    private Integer id_usuario;
}
