package com.nebulaparfums.nebula_parfums.exception;

import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class CustomMessageException {
    private int status;
    private String message;
    private long timestamp;

    public CustomMessageException() {
    }

    public CustomMessageException(int status, String message, long timestamp) {
        this.status = status;
        this.message = message;
        this.timestamp = timestamp;
    }
}
