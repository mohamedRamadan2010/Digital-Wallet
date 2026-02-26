package com.wallet.transactionservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@FeignClient(name = "wallet-service")
public interface WalletClient {

    @PostMapping("/api/wallets/{userId}/debit")
    WalletDto debit(@PathVariable("userId") Long userId, @RequestBody TransferDto transferDto);

    @PostMapping("/api/wallets/{userId}/credit")
    WalletDto credit(@PathVariable("userId") Long userId, @RequestBody TransferDto transferDto);

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    class TransferDto {
        private BigDecimal amount;
        private String referenceId;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    class WalletDto {
        private Long id;
        private Long userId;
        private BigDecimal balance;
    }
}
