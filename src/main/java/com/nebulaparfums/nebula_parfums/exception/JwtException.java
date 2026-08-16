package com.nebulaparfums.nebula_parfums.exception;

public class JwtException extends RuntimeException {
    public JwtException(String message, Throwable cause) {
        super(message, cause);
    }
}
