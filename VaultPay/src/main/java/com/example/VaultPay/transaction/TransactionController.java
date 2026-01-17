package com.vaultpay.transaction;

import com.vaultpay.transaction.dto.*;
import com.vaultpay.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transfers")
@RequiredArgsConstructor
public class TransferController {

    private final TransferService transferService;

    @PostMapping
    public TransferResponse transfer(
            @AuthenticationPrincipal User user,
            @RequestBody TransferRequest request
    ) {
        return transferService.transfer(user.getId(), request);
    }
}
