package com.nebulaparfums.nebula_parfums.dto;

import lombok.Getter;

@Getter
public class ProductoCantidadDTO {
    private final String producto;
    private final Long totalUnidades;

    public ProductoCantidadDTO(String producto, Long totalUnidades) {
        this.producto = producto;
        this.totalUnidades = totalUnidades;
    }

}