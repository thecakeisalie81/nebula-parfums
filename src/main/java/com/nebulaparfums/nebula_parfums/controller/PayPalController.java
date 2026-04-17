package com.nebulaparfums.nebula_parfums.controller;

import com.nebulaparfums.nebula_parfums.dto.PayPalCaptureOrderRequest;
import com.nebulaparfums.nebula_parfums.dto.PayPalCreateOrderRequest;
import com.nebulaparfums.nebula_parfums.service.PayPalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tools.jackson.databind.JsonNode;

import java.util.Map;

@RestController
@RequestMapping("/paypal")
@RequiredArgsConstructor
public class PayPalController {

    private final PayPalService payPalService;

    @PostMapping("/create-order")
    public ResponseEntity<?> createOrder(@RequestBody PayPalCreateOrderRequest request) {
        try {
            String orderId = payPalService.crearOrden(request.getTotal());
            return ResponseEntity.ok(Map.of("orderId", orderId));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "message", "No se pudo crear la orden PayPal"
            ));
        }
    }

    @PostMapping("/capture-order")
    public ResponseEntity<?> captureOrder(@RequestBody PayPalCaptureOrderRequest request) {
        try {
            JsonNode result = payPalService.capturarOrden(request.getOrderId());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "message", "No se pudo capturar la orden PayPal"
            ));
        }
    }
}