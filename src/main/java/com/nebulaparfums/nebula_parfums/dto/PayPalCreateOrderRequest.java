package com.nebulaparfums.nebula_parfums.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PayPalCreateOrderRequest {
    private BigDecimal total;
}