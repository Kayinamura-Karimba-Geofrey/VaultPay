package com.vaultpay.transfer;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class TransferResult {

    private Long transactionId;
    private BigDecimal amount;
    private String status;
}3