package com.nebulaparfums.nebula_parfums.exception;

public class QuantityBelowZeroException extends RuntimeException{
    public QuantityBelowZeroException() {
    }

    public QuantityBelowZeroException(String message) {
        super(message);
    }
}
