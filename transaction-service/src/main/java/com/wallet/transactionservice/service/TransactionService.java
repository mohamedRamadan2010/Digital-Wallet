package com.wallet.transactionservice.service;

import com.wallet.transactionservice.dto.TransferRequest;
import com.wallet.transactionservice.dto.TransferResponse;
import com.wallet.transactionservice.entity.Transaction;
import com.wallet.transactionservice.entity.TransactionStatus;
import com.wallet.transactionservice.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final SagaOrchestrator sagaOrchestrator;

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

        // This could be asynchronous via messaging (Kafka), but keeping it synchronous
        // orchestration for simplicity.
        sagaOrchestrator.executeTransferSaga(transaction);

        return TransferResponse.builder()
                .transactionId(transaction.getId().toString())
                .status(transaction.getStatus())
                .message(transaction.getStatus() == TransactionStatus.COMPLETED ? "Transfer successful"
                        : transaction.getFailureReason())
                .build();
    }
}
