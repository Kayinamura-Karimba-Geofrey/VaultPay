package com.vaultpay.wallet;

import com.vaultpay.audit.AuditAction;
import com.vaultpay.audit.AuditService;
import com.vaultpay.wallet.dto.WalletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;
    private final AuditService auditService;


    @PreAuthorize("#userId == authentication.principal.id")
    public WalletResponse getWalletBalance(Long userId) {

        Wallet wallet = walletRepository.findByOwnerId(userId)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));


        auditService.log(
                AuditAction.WALLET_BALANCE_VIEWED,
                wallet.getOwner(),
                "Wallet balance viewed"
        );

        return new WalletResponse(wallet.getBalance());
    }
}
