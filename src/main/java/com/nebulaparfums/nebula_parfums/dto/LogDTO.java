package com.nebulaparfums.nebula_parfums.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LogDTO {
    private Integer id_log;
    private String accion;
    private LocalDateTime fecha_actualizacion;
    private String detalle;
    private Integer usuario_id;
}
