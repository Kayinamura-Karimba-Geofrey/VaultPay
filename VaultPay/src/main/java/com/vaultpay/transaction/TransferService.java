package com.vaultpay.transaction;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaultpay.audit.AuditAction;
import com.vaultpay.audit.AuditService;
import com.vaultpay.transfer.IdempotencyKey;
import com.vaultpay.transfer.IdempotencyRepository;
import com.vaultpay.transfer.TransferResult;
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


        var existing = idempotencyRepository.findByIdempotencyKey(idempotencyKey);
        if (existing.isPresent()) {


            return objectMapper.readValue(
                    existing.get().getResponseHash(),
                    TransferResult.class
            );
        }


        if (senderWallet.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        senderWallet.setBalance(senderWallet.getBalance().subtract(amount));
        recipientWallet.setBalance(recipientWallet.getBalance().add(amount));

        walletRepository.save(senderWallet);
        walletRepository.save(recipientWallet);


        TransferResult result = TransferResult.builder()
                .transactionId(System.currentTimeMillis())
                .amount(amount)
                .status("SUCCESS")
                .build();


        String serialized = objectMapper.writeValueAsString(result);

        idempotencyRepository.save(
                IdempotencyKey.builder()
                        .idempotencyKey(idempotencyKey)
                        .responseHash(serialized)
                        .createdAt(LocalDateTime.now())
                        .build()
        );


        auditService.log(
                AuditAction.MONEY_TRANSFERRED,
                senderWallet.getOwner(),
                "Transferred " + amount + " to walletId=" + recipientWallet.getId()
        );

        return result;
    }
}