package com.wallet.fraudservice.controller;

import com.wallet.fraudservice.dto.FraudCheckRequest;
import com.wallet.fraudservice.dto.FraudCheckResponse;
import com.wallet.fraudservice.service.FraudService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/fraud")
@RequiredArgsConstructor
@Slf4j
public class FraudController {

    private final FraudService fraudService;

    @PostMapping("/check")
    public ResponseEntity<FraudCheckResponse> checkFraud(@RequestBody FraudCheckRequest request) {
        log.info("Checking transaction for fraud - userID: {} , amount: {}",
                request.getUserId(), request.getAmount());
        return ResponseEntity.ok(fraudService.isFraudulentTransaction(request));
    }
}
