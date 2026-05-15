package com.banksystem.transactionservice.repository;

import com.banksystem.transactionservice.entity.Transaction;
import com.banksystem.transactionservice.enums.TransactionStatus;
import com.banksystem.transactionservice.enums.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Page<Transaction> findByFromAccountId(Long fromAccountId, Pageable pageable);
    Page<Transaction> findByToAccountId(Long toAccountId, Pageable pageable);
    List<Transaction> findByStatus(TransactionStatus status);
    List<Transaction> findByType(TransactionType type);
    List<Transaction> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}
