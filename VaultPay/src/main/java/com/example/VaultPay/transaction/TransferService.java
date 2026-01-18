package com.vaultpay.transaction;

import com.example.VaultPay.audit.AuditAction;
import com.example.VaultPay.audit.AuditService;
import com.example.VaultPay.transaction.dto.TransferRequest;
import com.example.VaultPay.transaction.dto.TransferResponse;
import com.example.VaultPay.wallet.Wallet;
import com.example.VaultPay.wallet.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransferService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final AuditService auditService;

    /**
     * MONEY TRANSFER
     * - Atomic
     * - Double-entry ledger
     * - Audited ONLY after success
     */
    @Transactional
    @PreAuthorize("#senderId == authentication.principal.id")
    public TransferResponse transfer(Long senderId, TransferRequest request) {

        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Invalid transfer amount");
        }

        Wallet senderWallet = walletRepository.findByOwnerId(senderId)
                .orElseThrow(() -> new RuntimeException("Sender wallet not found"));

        Wallet receiverWallet = walletRepository.findByOwnerId(request.getRecipientUserId())
                .orElseThrow(() -> new RuntimeException("Receiver wallet not found"));

        if (senderWallet.getBalance().compareTo(request.getAmount()) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        BigDecimal amount = request.getAmount();
        String reference = UUID.randomUUID().toString();



        senderWallet.setBalance(senderWallet.getBalance().subtract(amount));
        transactionRepository.save(
                Transaction.builder()
                        .wallet(senderWallet)
                        .type(TransactionType.DEBIT)
                        .amount(amount)
                        .reference(reference)
                        .createdAt(LocalDateTime.now())
                        .build()
        );

        // 🔺 CREDIT
        receiverWallet.setBalance(receiverWallet.getBalance().add(amount));
        transactionRepository.save(
                Transaction.builder()
                        .wallet(receiverWallet)
                        .type(TransactionType.CREDIT)
                        .amount(amount)
                        .reference(reference)
                        .createdAt(LocalDateTime.now())
                        .build()
        );

        // ✅ AUDIT ONLY AFTER SUCCESSFUL TRANSFER
        auditService.log(
                AuditAction.MONEY_TRANSFERRED,
                senderWallet.getOwner(),
                "Transferred " + amount + " to userId=" + request.getRecipientUserId()
        );

        return new TransferResponse("SUCCESS", reference);
    }
}
