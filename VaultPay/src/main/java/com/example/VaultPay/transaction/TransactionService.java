package com.vaultpay.transaction;

import com.vaultpay.transaction.dto.*;
import com.vaultpay.wallet.*;
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

    @Transactional
    @PreAuthorize("#senderId == authentication.principal.id")
    public TransferResponse transfer(
            Long senderId,
            TransferRequest request
    ) {

        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Invalid amount");
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

        // 🔻 DEBIT
        senderWallet.setBalance(senderWallet.getBalance().subtract(amount));
        transactionRepository.save(
                Transaction.builder()
                        .wallet(senderWallet)
                        .type(TransactionType.DEBIT)
                        .amount(amount)
                        .createdAt(LocalDateTime.now())
                        .reference(reference)
                        .build()
        );

        // 🔺 CREDIT
        receiverWallet.setBalance(receiverWallet.getBalance().add(amount));
        transactionRepository.save(
                Transaction.builder()
                        .wallet(receiverWallet)
                        .type(TransactionType.CREDIT)
                        .amount(amount)
                        .createdAt(LocalDateTime.now())
                        .reference(reference)
                        .build()
        );

        return new TransferResponse("SUCCESS", reference);
    }
}
