package com.wallet.walletservice.controller;

import com.wallet.walletservice.dto.TransferDto;
import com.wallet.walletservice.dto.WalletDto;
import com.wallet.walletservice.service.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wallets")
@RequiredArgsConstructor
@Slf4j
public class WalletController {

    private final WalletService walletService;

    @PostMapping("/{userId}")
    public ResponseEntity<WalletDto> createWallet(@PathVariable("userId") Long userId) {
        log.info("Creating new wallet for userId: {}", userId);
        return ResponseEntity.ok(walletService.createWallet(userId));
    }

    @GetMapping("/{userId}/balance")
    public ResponseEntity<WalletDto> getBalance(@PathVariable("userId") Long userId) {
        log.info("Fetching balance for userId: {}", userId);
        return ResponseEntity.ok(walletService.getWalletByUserId(userId));
    }

    @PostMapping("/{userId}/credit")
    public ResponseEntity<WalletDto> credit(@PathVariable("userId") Long userId, @RequestBody TransferDto transferDto) {
        log.info("Crediting wallet for userId: {} with amount: {}", userId, transferDto.getAmount());
        return ResponseEntity.ok(walletService.credit(userId, transferDto));
    }

    @PostMapping("/{userId}/debit")
    public ResponseEntity<WalletDto> debit(@PathVariable("userId") Long userId, @RequestBody TransferDto transferDto) {
        log.info("Debiting wallet for userId: {} with amount: {}", userId, transferDto.getAmount());
        return ResponseEntity.ok(walletService.debit(userId, transferDto));
    }
}
