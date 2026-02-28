package com.wallet.walletservice.service;

import com.wallet.walletservice.dto.TransferDto;
import com.wallet.walletservice.event.KafkaEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class WalletEventConsumer {

    private final WalletService walletService;
    private final KafkaTemplate<String, KafkaEvent> kafkaTemplate;

    @KafkaListener(topics = "fraud-events", groupId = "wallet-group")
    public void consumeFraudEvents(KafkaEvent event) {
        log.info("Received Fraud Event in Wallet Service: {}", event);

        if ("FRAUD_CHECK_PASSED".equals(event.getEventType())) {

            try {
                // Step 1: Debit Sender
                walletService.debit(event.getFromUserId(),
                        new TransferDto(event.getAmount(), event.getTransactionId() + "-DEBIT"));
            } catch (Exception e) {
                log.error("Debit failed for transaction {}", event.getTransactionId(), e);
                publishWalletEvent("WALLET_DEBIT_FAILED", event, e.getMessage());
                return;
            }

            try {
                // Step 2: Credit Receiver
                walletService.credit(event.getToUserId(),
                        new TransferDto(event.getAmount(), event.getTransactionId() + "-CREDIT"));
            } catch (Exception e) {
                log.error("Credit failed for transaction {}, compensating debit...", event.getTransactionId(), e);

                // Compensating Transaction
                try {
                    walletService.credit(event.getFromUserId(),
                            new TransferDto(event.getAmount(), event.getTransactionId() + "-REVERT"));
                } catch (Exception revertEx) {
                    log.error("CRITICAL: Failed to revert debit for transaction {}", event.getTransactionId(),
                            revertEx);
                }

                publishWalletEvent("WALLET_CREDIT_FAILED", event, e.getMessage());
                return;
            }

            // Both Debit and Credit succeeded
            publishWalletEvent("WALLET_TRANSFER_COMPLETED", event, "Transfer successful");
        }
    }

    private void publishWalletEvent(String eventType, KafkaEvent originalEvent, String reason) {
        KafkaEvent resultEvent = KafkaEvent.builder()
                .eventType(eventType)
                .transactionId(originalEvent.getTransactionId())
                .fromUserId(originalEvent.getFromUserId())
                .toUserId(originalEvent.getToUserId())
                .amount(originalEvent.getAmount())
                .reason(reason)
                .build();
        kafkaTemplate.send("wallet-events", originalEvent.getTransactionId(), resultEvent);
        log.info("Published Wallet Event: {}", resultEvent);
    }
}
