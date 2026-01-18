package com.vaultpay.audit;

import com.vaultpay.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditRepository auditRepository;

    public void log(
            AuditAction action,
            User user,
            String details
    ) {
        auditRepository.save(
                AuditLog.builder()
                        .action(action)
                        .performedBy(user)
                        .performedAt(LocalDateTime.now())
                        .details(details)
                        .build()
        );
    }
}
