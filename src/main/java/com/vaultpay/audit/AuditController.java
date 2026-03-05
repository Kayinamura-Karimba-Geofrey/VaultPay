package com.vaultpay.audit;

import com.vaultpay.audit.dto.AuditResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/audits")
@RequiredArgsConstructor
public class AuditController {

    private final AuditRepository auditRepository;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','AUDITOR')")
    public Page<AuditResponse> getAudits(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("performedAt").descending()
        );

        return auditRepository.findAll(pageable)
                .map(log -> new AuditResponse(
                        log.getAction(),
                        log.getPerformedBy().getEmail(),
                        log.getPerformedAt(),
                        log.getDetails()
                ));
    }
}
