package com.vaultpay.wallet;

import com.vaultpay.wallet.dto.WalletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @GetMapping("/balance")
    public WalletResponse getBalance(@AuthenticationPrincipal com.vaultpay.user.User user) {
        return walletService.getWalletBalance(user.getId());
    }
}
