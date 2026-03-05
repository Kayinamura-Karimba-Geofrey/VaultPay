package com.vaultpay.audit.dto;

import com.vaultpay.audit.AuditAction;
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
