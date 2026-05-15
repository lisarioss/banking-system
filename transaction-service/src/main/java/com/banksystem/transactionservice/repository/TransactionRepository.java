package com.banksystem.transactionservice.repository;

import com.banksystem.transactionservice.entity.Transaction;
import com.banksystem.transactionservice.entity.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByFromAccountIdOrderByCreatedAtDesc(Long accountId);
    List<Transaction> findByToAccountIdOrderByCreatedAtDesc(Long accountId);
    Optional<Transaction> findByReferenceCode(String referenceCode);
    List<Transaction> findByStatusOrderByCreatedAtDesc(TransactionStatus status);
    
    @Query("SELECT t FROM Transaction t WHERE (t.fromAccountId = :accountId OR t.toAccountId = :accountId) ORDER BY t.createdAt DESC")
    List<Transaction> findAccountTransactionHistory(Long accountId);
}
