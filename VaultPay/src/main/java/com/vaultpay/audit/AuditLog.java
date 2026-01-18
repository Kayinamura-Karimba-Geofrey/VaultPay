package com.vaultpay.audit;

import com.vaultpay.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuditAction action;

    @ManyToOne(optional = false)
    private User performedBy;

    @Column(nullable = false)
    private LocalDateTime performedAt;

    @Column(length = 500)
    private String details;
}
