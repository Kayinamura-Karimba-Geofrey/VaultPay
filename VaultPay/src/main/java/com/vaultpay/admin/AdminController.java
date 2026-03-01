package com.vaultpay.admin;

import com.vaultpay.audit.AuditLog;
import com.vaultpay.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.vaultpay.user.dto.UserResponse;
import com.vaultpay.mapper.UserMapper;


import lombok.RequiredArgsConstructor;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    // ==============================
    // 1️⃣ View All Users
    // ==============================
    @GetMapping("/users")
    public Page<UserResponse> getUsers(Pageable pageable) {
        return adminService.getAllUsers(pageable)
                .map(UserMapper::toResponse);
    }

    // ==============================
    // 2️⃣ Lock User
    // ==============================
    @PostMapping("/users/{id}/lock")
    public void lockUser(@PathVariable Long id,
                         @AuthenticationPrincipal User admin) {

        adminService.lockUser(id, admin);
    }

    // ==============================
    // 3️⃣ Unlock User
    // ==============================
    @PostMapping("/users/{id}/unlock")
    public void unlockUser(@PathVariable Long id,
                           @AuthenticationPrincipal User admin) {

        adminService.unlockUser(id, admin);
    }

    // ==============================
    // 4️⃣ View Audit Logs
    // ==============================
    @GetMapping("/audits")
    public Page<AuditLog> getAuditLogs(Pageable pageable) {
        return adminService.getAllAuditLogs(pageable);
    }
}