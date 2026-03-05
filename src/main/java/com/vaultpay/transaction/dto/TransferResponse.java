package com.vaultpay.transaction.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TransferResponse {
    private String status;
    private String reference;
}
