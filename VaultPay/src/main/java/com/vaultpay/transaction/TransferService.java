package com.vaultpay.transfer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaultpay.audit.AuditAction;
import com.vaultpay.audit.AuditService;
import com.vaultpay.wallet.Wallet;
import com.vaultpay.wallet.WalletRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TransferService {

    private final WalletRepository walletRepository;
    private final IdempotencyRepository idempotencyRepository;
    private final AuditService auditService;
    private final ObjectMapper objectMapper;

    public TransferResult transfer(
            Wallet senderWallet,
            Wallet recipientWallet,
            BigDecimal amount,
            String idempotencyKey
    ) throws Exception {

        // 1️⃣ Check idempotency
        var existing = idempotencyRepository.findByIdempotencyKey(idempotencyKey);
        if (existing.isPresent()) {

            // Deserialize stored response
            return objectMapper.readValue(
                    existing.get().getResponseHash(),
                    TransferResult.class
            );
        }

        // 2️⃣ Perform transfer
        if (senderWallet.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        senderWallet.setBalance(senderWallet.getBalance().subtract(amount));
        recipientWallet.setBalance(recipientWallet.getBalance().add(amount));

        walletRepository.save(senderWallet);
        walletRepository.save(recipientWallet);

        // 3️⃣ Build result
        TransferResult result = TransferResult.builder()
                .transactionId(System.currentTimeMillis())
                .amount(amount)
                .status("SUCCESS")
                .build();

        // 4️⃣ Store idempotency key
        String serialized = objectMapper.writeValueAsString(result);

        idempotencyRepository.save(
                IdempotencyKey.builder()
                        .idempotencyKey(idempotencyKey)
                        .responseHash(serialized)
                        .createdAt(LocalDateTime.now())
                        .build()
        );

        // 5️⃣ Audit after success
        auditService.log(
                AuditAction.MONEY_TRANSFERRED,
                senderWallet.getOwner(),
                "Transferred " + amount + " to walletId=" + recipientWallet.getId()
        );

        return result;
    }
}