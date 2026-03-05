package com.vaultpay.transaction;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository repository;

    public Transaction createPendingTransaction(
            Transaction transaction
    ) {
        transaction.setStatus(TransactionStatus.PENDING);
        transaction.setCreatedAt(LocalDateTime.now());
        return repository.save(transaction);
    }

    public Transaction markProcessing(Long id) {
        Transaction tx = repository.findById(id).orElseThrow();
        tx.setStatus(TransactionStatus.PROCESSING);
        return repository.save(tx);
    }

    public Transaction markSuccess(Long id) {
        Transaction tx = repository.findById(id).orElseThrow();
        tx.setStatus(TransactionStatus.SUCCESS);
        return repository.save(tx);
    }

    public Transaction markFailed(Long id) {
        Transaction tx = repository.findById(id).orElseThrow();
        tx.setStatus(TransactionStatus.FAILED);
        return repository.save(tx);
    }
}