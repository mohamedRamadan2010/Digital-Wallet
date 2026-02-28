package com.wallet.fraudservice.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KafkaEvent {
    private String eventType;
    private String transactionId;
    private Long fromUserId;
    private Long toUserId;
    private BigDecimal amount;
    private String reason;
}
