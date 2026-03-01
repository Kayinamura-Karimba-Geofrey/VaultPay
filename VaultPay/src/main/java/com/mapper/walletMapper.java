package com.vaultpay.mapper;

import com.vaultpay.wallet.Wallet;
import com.vaultpay.wallet.dto.WalletResponse;

public class WalletMapper {

    public static WalletResponse toResponse(Wallet wallet) {
        return WalletResponse.builder()
                .id(wallet.getId())
                .balance(wallet.getBalance())
                .currency(wallet.getCurrency())
                .build();
    }
}