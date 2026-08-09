package com.nebulaparfums.nebula_parfums.exception;

public class InvalidQuantityException extends RuntimeException {
    public InvalidQuantityException() {
    }
    public InvalidQuantityException(String message) {
        super(message);
    }
}
