package com.wallet.transactionservice.service;

import com.wallet.transactionservice.client.FraudClient;
import com.wallet.transactionservice.client.WalletClient;
import com.wallet.transactionservice.entity.Transaction;
import com.wallet.transactionservice.entity.TransactionStatus;
import com.wallet.transactionservice.repository.TransactionRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SagaOrchestrator {

    private static final Logger log = LoggerFactory.getLogger(SagaOrchestrator.class);

    private final WalletClient walletClient;
    private final FraudClient fraudClient;
    private final TransactionRepository transactionRepository;

    @CircuitBreaker(name = "fraudService", fallbackMethod = "fallbackFraudCheck")
    @Retry(name = "fraudService")
    public void executeTransferSaga(Transaction transaction) {
        String referenceId = transaction.getId().toString();
        
        try {
            // Step 1: Fraud Check
            FraudClient.FraudCheckResponse fraudResponse = fraudClient.checkFraud(
                    new FraudClient.FraudCheckRequest(transaction.getFromUserId(), transaction.getAmount())
            );

            if (fraudResponse.getIsFraudulent()) {
                throw new RuntimeException("Fraud detected: " + fraudResponse.getReason());
            }

            // Step 2: Debit Sender
            walletClient.debit(transaction.getFromUserId(), 
                    new WalletClient.TransferDto(transaction.getAmount(), referenceId + "-DEBIT"));

            // Step 3: Credit Receiver
            try {
                walletClient.credit(transaction.getToUserId(), 
                        new WalletClient.TransferDto(transaction.getAmount(), referenceId + "-CREDIT"));
            } catch (Exception e) {
                // Compensating Transaction
                log.error("Credit failed, compensating debit...", e);
                walletClient.credit(transaction.getFromUserId(), 
                        new WalletClient.TransferDto(transaction.getAmount(), referenceId + "-REVERT"));
                throw new RuntimeException("Credit failed, transaction reverted.", e);
            }

            // Saga Completed successfully
            transaction.setStatus(TransactionStatus.COMPLETED);
            transactionRepository.save(transaction);
            
        } catch (Exception e) {
            log.error("Transaction {} failed: {}", transaction.getId(), e.getMessage());
            transaction.setStatus(TransactionStatus.FAILED);
            transaction.setFailureReason(e.getMessage());
            transactionRepository.save(transaction);
        }
    }

    public void fallbackFraudCheck(Transaction transaction, Throwable t) {
        log.error("Circuit breaker triggered for fraud service. Transaction {} failed: {}", transaction.getId(), t.getMessage());
        transaction.setStatus(TransactionStatus.FAILED);
        transaction.setFailureReason("Service unavailable: Fraud check failed. " + t.getMessage());
        transactionRepository.save(transaction);
    }
}
