package com.wallet.fraudservice.service;

import com.wallet.fraudservice.dto.FraudCheckRequest;
import com.wallet.fraudservice.dto.FraudCheckResponse;
import com.wallet.fraudservice.event.KafkaEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class FraudEventConsumer {

    private final FraudService fraudService;
    private final KafkaTemplate<String, KafkaEvent> kafkaTemplate;

    @KafkaListener(topics = "transaction-events", groupId = "fraud-group")
    public void consumeTransactionEvents(KafkaEvent event) {
        log.info("Received Transaction Event: {}", event);

        if ("TRANSACTION_CREATED".equals(event.getEventType())) {
            FraudCheckRequest request = FraudCheckRequest.builder()
                    .userId(event.getFromUserId())
                    .amount(event.getAmount())
                    .build();

            FraudCheckResponse response = fraudService.isFraudulentTransaction(request);

            KafkaEvent resultEvent = KafkaEvent.builder()
                    .eventType(response.getIsFraudulent() ? "FRAUD_CHECK_FAILED" : "FRAUD_CHECK_PASSED")
                    .transactionId(event.getTransactionId())
                    .fromUserId(event.getFromUserId())
                    .toUserId(event.getToUserId())
                    .amount(event.getAmount())
                    .reason(response.getReason())
                    .build();

            kafkaTemplate.send("fraud-events", event.getTransactionId(), resultEvent);
            log.info("Published Fraud Event: {}", resultEvent);
        }
    }
}
