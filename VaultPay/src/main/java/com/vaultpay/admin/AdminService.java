package com.vaultpay.admin;

import com.vaultpay.audit.AuditLog;
import com.vaultpay.audit.AuditRepository;
import com.vaultpay.audit.AuditAction;
import com.vaultpay.audit.AuditService;
import com.vaultpay.user.User;
import com.vaultpay.user.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final AuditRepository auditRepository;
    private final AuditService auditService;

    // ==============================
    // 1️⃣ View All Users
    // ==============================
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // ==============================
    // 2️⃣ Lock User Account
    // ==============================
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

    // ==============================
    // 3️⃣ Unlock User Account
    // ==============================
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

    // ==============================
    // 4️⃣ View Audit Logs
    // ==============================
    public List<AuditLog> getAllAuditLogs() {
        return auditRepository.findAll();
    }
}