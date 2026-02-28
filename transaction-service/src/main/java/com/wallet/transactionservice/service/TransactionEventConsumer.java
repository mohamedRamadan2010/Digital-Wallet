package com.wallet.transactionservice.service;

import com.wallet.transactionservice.event.KafkaEvent;
import com.wallet.transactionservice.entity.Transaction;
import com.wallet.transactionservice.entity.TransactionStatus;
import com.wallet.transactionservice.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionEventConsumer {

    private final TransactionRepository transactionRepository;

    @KafkaListener(topics = "fraud-events", groupId = "transaction-group")
    @Transactional
    public void consumeFraudEvents(KafkaEvent event) {
        log.info("Received Fraud Event: {}", event);
        if ("FRAUD_CHECK_FAILED".equals(event.getEventType())) {
            updateTransactionStatus(event.getTransactionId(), TransactionStatus.FAILED, event.getReason());
        }
    }

    @KafkaListener(topics = "wallet-events", groupId = "transaction-group")
    @Transactional
    public void consumeWalletEvents(KafkaEvent event) {
        log.info("Received Wallet Event: {}", event);
        if ("WALLET_TRANSFER_COMPLETED".equals(event.getEventType())) {
            updateTransactionStatus(event.getTransactionId(), TransactionStatus.COMPLETED, null);
        } else if ("WALLET_DEBIT_FAILED".equals(event.getEventType())
                || "WALLET_CREDIT_FAILED".equals(event.getEventType())) {
            updateTransactionStatus(event.getTransactionId(), TransactionStatus.FAILED, event.getReason());
        }
    }

    private void updateTransactionStatus(String transactionId, TransactionStatus status, String reason) {
        transactionRepository.findById(Long.parseLong(transactionId)).ifPresent(transaction -> {
            transaction.setStatus(status);
            if (reason != null) {
                transaction.setFailureReason(reason);
            }
            transactionRepository.save(transaction);
            log.info("Updated transaction {} status to {}", transactionId, status);
        });
    }
}
