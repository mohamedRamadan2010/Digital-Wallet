package com.wallet.transactionservice.controller;

import com.wallet.transactionservice.dto.TransferRequest;
import com.wallet.transactionservice.dto.TransferResponse;
import com.wallet.transactionservice.entity.Transaction;
import com.wallet.transactionservice.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@Slf4j
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/transfer")
    public ResponseEntity<TransferResponse> transfer(@Valid @RequestBody TransferRequest request) {
        log.info("Processing transfer from wallet: {} to wallet: {} for amount: {}",
                request.getFromUserId(), request.getToUserId(), request.getAmount());
        return ResponseEntity.ok(transactionService.transferAmount(request));
    }

    @GetMapping("/history/{userId}")
    public ResponseEntity<List<Transaction>> getHistory(@PathVariable("userId") Long userId) {
        log.info("Fetching transaction history for userId: {}", userId);
        return ResponseEntity.ok(transactionService.getTransactionHistory(userId));
    }
}
