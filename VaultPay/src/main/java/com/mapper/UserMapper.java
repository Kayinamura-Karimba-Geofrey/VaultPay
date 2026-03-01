package com.vaultpay.mapper;

import com.vaultpay.user.User;
import com.vaultpay.user.dto.UserResponse;

public class UserMapper {

    public static UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .enabled(user.isEnabled())
                .accountNonLocked(user.isAccountNonLocked())
                .build();
    }
}