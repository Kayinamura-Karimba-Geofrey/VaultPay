package com.vaultpay.transaction;

import com.vaultpay.wallet.Wallet;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Page<Transaction> findByWallet(Wallet wallet, Pageable pageable);
}
