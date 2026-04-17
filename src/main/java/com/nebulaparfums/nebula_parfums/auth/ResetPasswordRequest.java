package com.nebulaparfums.nebula_parfums.auth;

import lombok.Data;

@Data
public class ResetPasswordRequest {
    private String token;
    private String nuevaPassword;
}