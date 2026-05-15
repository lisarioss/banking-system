package com.banksystem.transactionservice.dto;

import com.banksystem.transactionservice.entity.Transaction;
import com.banksystem.transactionservice.entity.TransactionStatus;
import com.banksystem.transactionservice.entity.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {

    private Long id;
    private Long fromAccountId;
    private Long toAccountId;
    private BigDecimal amount;
    private TransactionType type;
    private TransactionStatus status;
    private String description;
    private String referenceCode;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;

    public static TransactionResponse fromEntity(Transaction transaction) {
        return new TransactionResponse(
            transaction.getId(),
            transaction.getFromAccountId(),
            transaction.getToAccountId(),
            transaction.getAmount(),
            transaction.getType(),
            transaction.getStatus(),
            transaction.getDescription(),
            transaction.getReferenceCode(),
            transaction.getCreatedAt(),
            transaction.getCompletedAt()
        );
    }
}
