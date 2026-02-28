package com.wallet.transactionservice.service;

import com.wallet.transactionservice.dto.TransferRequest;
import com.wallet.transactionservice.dto.TransferResponse;
import com.wallet.transactionservice.entity.Transaction;
import com.wallet.transactionservice.entity.TransactionStatus;
import com.wallet.transactionservice.repository.TransactionRepository;
import com.wallet.transactionservice.event.KafkaEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final KafkaTemplate<String, KafkaEvent> kafkaTemplate;

    public List<Transaction> getTransactionHistory(Long userId) {
        return transactionRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public TransferResponse transferAmount(TransferRequest request) {

        // Initial state
        Transaction transaction = Transaction.builder()
                .fromUserId(request.getFromUserId())
                .toUserId(request.getToUserId())
                .amount(request.getAmount())
                .status(TransactionStatus.PENDING)
                .build();

        transaction = transactionRepository.save(transaction);

        // Publish event to Kafka instead of synchronous saga orchestrator call
        KafkaEvent event = KafkaEvent.builder()
                .eventType("TRANSACTION_CREATED")
                .transactionId(transaction.getId().toString())
                .fromUserId(transaction.getFromUserId())
                .toUserId(transaction.getToUserId())
                .amount(transaction.getAmount())
                .build();

        kafkaTemplate.send("transaction-events", transaction.getId().toString(), event);

        return TransferResponse.builder()
                .transactionId(transaction.getId().toString())
                .status(transaction.getStatus())
                .message(transaction.getStatus() == TransactionStatus.COMPLETED ? "Transfer successful"
                        : transaction.getFailureReason())
                .build();
    }
}
