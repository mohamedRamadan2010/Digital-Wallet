package com.wallet.fraudservice.repository;

import com.wallet.fraudservice.entity.FraudCheckHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface FraudCheckHistoryRepository extends JpaRepository<FraudCheckHistory, Long> {
    
    @Query("SELECT COUNT(f) FROM FraudCheckHistory f WHERE f.userId = :userId AND f.createdAt > :oneMinuteAgo")
    long countRecentTransactions(@Param("userId") Long userId, @Param("oneMinuteAgo") LocalDateTime oneMinuteAgo);
}
