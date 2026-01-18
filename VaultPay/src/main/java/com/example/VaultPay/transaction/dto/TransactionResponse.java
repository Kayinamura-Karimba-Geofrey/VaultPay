package com.example.VaultPay.transaction.dto;

import com.vaultpay.transaction.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class TransactionResponse {

    private TransactionType type;
    private BigDecimal amount;
    private String reference;
    private LocalDateTime createdAt;
}
