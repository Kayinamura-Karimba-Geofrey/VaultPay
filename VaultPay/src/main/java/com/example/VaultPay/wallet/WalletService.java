package com.vaultpay.wallet;

import com.vaultpay.wallet.dto.WalletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;

    @PreAuthorize("#userId == authentication.principal.id")
    public WalletResponse getWalletBalance(Long userId) {

        Wallet wallet = walletRepository.findByOwnerId(userId)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        return new WalletResponse(wallet.getBalance());
    }
}
