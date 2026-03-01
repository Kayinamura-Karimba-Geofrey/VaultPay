package com.vaultpay.admin;

import com.vaultpay.audit.AuditLog;
import com.vaultpay.audit.AuditRepository;
import com.vaultpay.audit.AuditAction;
import com.vaultpay.audit.AuditService;
import com.vaultpay.user.User;
import com.vaultpay.user.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final AuditRepository auditRepository;
    private final AuditService auditService;

    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public Page<AuditLog> getAllAuditLogs(Pageable pageable) {
        return auditRepository.findAll(pageable);
    }

    public void lockUser(Long userId, User admin) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setAccountNonLocked(false);
        userRepository.save(user);

        auditService.log(
                AuditAction.ADMIN_LOCKED_USER,
                admin,
                "Admin locked userId=" + userId
        );
    }

    public void unlockUser(Long userId, User admin) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setAccountNonLocked(true);
        userRepository.save(user);

        auditService.log(
                AuditAction.ADMIN_UNLOCKED_USER,
                admin,
                "Admin unlocked userId=" + userId
        );
    }

    public List<AuditLog> getAllAuditLogs() {
        return auditRepository.findAll();
    }
}