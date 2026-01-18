package com.vaultpay.auth.dto;

import lombok.Getter;

@Getter
public class RegisterRequest {
    private String email;
    private String password;
}
