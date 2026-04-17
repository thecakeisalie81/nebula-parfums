package com.nebulaparfums.nebula_parfums.dto;

import lombok.Data;

@Data
public class PayPalCaptureOrderRequest {
    private String orderId;
}