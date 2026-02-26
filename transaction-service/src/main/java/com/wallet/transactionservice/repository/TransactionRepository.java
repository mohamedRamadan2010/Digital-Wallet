package com.wallet.transactionservice.repository;

import com.wallet.transactionservice.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query("SELECT t FROM Transaction t WHERE t.fromUserId = :userId OR t.toUserId = :userId ORDER BY t.createdAt DESC")
    List<Transaction> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);
}
