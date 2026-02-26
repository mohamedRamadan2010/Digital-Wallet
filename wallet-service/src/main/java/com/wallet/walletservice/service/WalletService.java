package com.wallet.walletservice.service;

import com.wallet.walletservice.dto.TransferDto;
import com.wallet.walletservice.dto.WalletDto;
import com.wallet.walletservice.entity.TransactionType;
import com.wallet.walletservice.entity.Wallet;
import com.wallet.walletservice.entity.WalletTransaction;
import com.wallet.walletservice.repository.WalletRepository;
import com.wallet.walletservice.repository.WalletTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;
    private final WalletTransactionRepository walletTransactionRepository;

    @Transactional
    public WalletDto createWallet(Long userId) {
        if (walletRepository.findByUserId(userId).isPresent()) {
            throw new RuntimeException("Wallet already exists for user");
        }
        
        Wallet wallet = Wallet.builder()
                .userId(userId)
                .balance(BigDecimal.ZERO)
                .build();
        wallet = walletRepository.save(wallet);

        return mapToDto(wallet);
    }

    public WalletDto getWalletByUserId(Long userId) {
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));
        return mapToDto(wallet);
    }

    @Transactional
    public WalletDto credit(Long userId, TransferDto transferDto) {
        // Idempotency check
        Optional<WalletTransaction> existingTx = walletTransactionRepository.findByReferenceId(transferDto.getReferenceId());
        if (existingTx.isPresent()) {
            Wallet wallet = walletRepository.findById(existingTx.get().getWalletId())
                    .orElseThrow(() -> new RuntimeException("Wallet not found"));
            return mapToDto(wallet);
        }

        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        wallet.setBalance(wallet.getBalance().add(transferDto.getAmount()));
        walletRepository.save(wallet);

        createTransaction(wallet.getId(), transferDto.getAmount(), TransactionType.CREDIT, transferDto.getReferenceId());

        return mapToDto(wallet);
    }

    @Transactional
    public WalletDto debit(Long userId, TransferDto transferDto) {
        // Idempotency check
        Optional<WalletTransaction> existingTx = walletTransactionRepository.findByReferenceId(transferDto.getReferenceId());
        if (existingTx.isPresent()) {
            Wallet wallet = walletRepository.findById(existingTx.get().getWalletId())
                    .orElseThrow(() -> new RuntimeException("Wallet not found"));
            return mapToDto(wallet);
        }

        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        if (wallet.getBalance().compareTo(transferDto.getAmount()) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        wallet.setBalance(wallet.getBalance().subtract(transferDto.getAmount()));
        walletRepository.save(wallet); // Optimistic locking will handle concurrent modifications

        createTransaction(wallet.getId(), transferDto.getAmount(), TransactionType.DEBIT, transferDto.getReferenceId());

        return mapToDto(wallet);
    }

    private void createTransaction(Long walletId, BigDecimal amount, TransactionType type, String referenceId) {
        WalletTransaction transaction = WalletTransaction.builder()
                .walletId(walletId)
                .amount(amount)
                .type(type)
                .referenceId(referenceId)
                .build();
        walletTransactionRepository.save(transaction);
    }

    private WalletDto mapToDto(Wallet wallet) {
        return WalletDto.builder()
                .id(wallet.getId())
                .userId(wallet.getUserId())
                .balance(wallet.getBalance())
                .build();
    }
}
