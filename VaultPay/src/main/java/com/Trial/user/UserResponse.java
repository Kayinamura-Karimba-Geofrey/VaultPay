package com.vaultpay.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponse {

    private Long id;
    private String email;
    private boolean enabled;
    private boolean accountNonLocked;
}