package com.wallet.transactionservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@FeignClient(name = "fraud-service")
public interface FraudClient {

    @PostMapping("/api/fraud/check")
    FraudCheckResponse checkFraud(@RequestBody FraudCheckRequest request);

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    class FraudCheckRequest {
        private Long userId;
        private BigDecimal amount;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    class FraudCheckResponse {
        private Boolean isFraudulent;
        private String reason;
    }
}
