package com.example.VaultPay.transaction;

import com.example.VaultPay.transaction.dto.TransactionResponse;
import com.example.VaultPay.wallet.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;

    @PreAuthorize("#userId == authentication.principal.id")
    public Page<TransactionResponse> getUserTransactions(
            Long userId,
            Pageable pageable
    ) {

        Wallet wallet = walletRepository.findByOwnerId(userId)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        return transactionRepository
                .findByWallet(wallet, pageable)
                .map(tx -> new TransactionResponse(
                        tx.getType(),
                        tx.getAmount(),
                        tx.getReference(),
                        tx.getCreatedAt()
                ));
    }

    /**
     * ADMIN / AUDITOR: Read-only access to ALL transactions
     */
    @PreAuthorize("hasAnyRole('ADMIN','AUDITOR')")
    public Page<TransactionResponse> getAllTransactions(Pageable pageable) {

        return transactionRepository
                .findAll(pageable)
                .map(tx -> new TransactionResponse(
                        tx.getType(),
                        tx.getAmount(),
                        tx.getReference(),
                        tx.getCreatedAt()
                ));
    }
}
