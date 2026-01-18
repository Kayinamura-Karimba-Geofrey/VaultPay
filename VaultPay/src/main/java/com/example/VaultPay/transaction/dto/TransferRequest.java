package com.example.VaultPay.transaction.dto;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class TransferRequest {
    private Long recipientUserId;
    private BigDecimal amount;
}
