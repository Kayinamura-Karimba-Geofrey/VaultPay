package com.example.VaultPay.audit.dto;

import com.example.VaultPay.audit.AuditAction;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class AuditResponse {

    private AuditAction action;
    private String performedBy;
    private LocalDateTime performedAt;
    private String details;
}
