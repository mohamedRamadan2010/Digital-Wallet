package com.wallet.fraudservice.service;

import com.wallet.fraudservice.dto.FraudCheckRequest;
import com.wallet.fraudservice.dto.FraudCheckResponse;
import com.wallet.fraudservice.entity.FraudCheckHistory;
import com.wallet.fraudservice.repository.FraudCheckHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class FraudService {

    private final FraudCheckHistoryRepository repository;
    private static final BigDecimal AMOUNT_THRESHOLD = new BigDecimal("10000");
    private static final long MAX_TRANSFERS_PER_MINUTE = 3;

    public FraudCheckResponse isFraudulentTransaction(FraudCheckRequest request) {
        
        // Rule 1: Amount exceeds threshold
        if (request.getAmount().compareTo(AMOUNT_THRESHOLD) > 0) {
            saveHistory(request.getUserId(), true);
            return new FraudCheckResponse(true, "Amount exceeds allowed threshold");
        }

        // Rule 2: High frequency of transactions (more than 3 per minute)
        LocalDateTime oneMinuteAgo = LocalDateTime.now().minusMinutes(1);
        long recentTransfers = repository.countRecentTransactions(request.getUserId(), oneMinuteAgo);
        
        if (recentTransfers >= MAX_TRANSFERS_PER_MINUTE) {
            saveHistory(request.getUserId(), true);
            return new FraudCheckResponse(true, "Velocity check failed: Too many transactions in a minute");
        }

        saveHistory(request.getUserId(), false);
        return new FraudCheckResponse(false, "OK");
    }

    private void saveHistory(Long userId, boolean isFraudster) {
        FraudCheckHistory history = FraudCheckHistory.builder()
                .userId(userId)
                .isFraudster(isFraudster)
                .build();
        repository.save(history);
    }
}
