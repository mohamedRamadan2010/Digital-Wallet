package com.wallet.transactionservice.dto;

import com.wallet.transactionservice.entity.TransactionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransferResponse {
    private String transactionId;
    private TransactionStatus status;
    private String message;
}
