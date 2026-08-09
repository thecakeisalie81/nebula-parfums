package com.nebulaparfums.nebula_parfums.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TotalEventosyHoyDTO {
    Long Hoy;
    Long Total;
}
