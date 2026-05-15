package com.banksystem.transactionservice.dto;

import com.banksystem.transactionservice.entity.Transaction;
import com.banksystem.transactionservice.enums.TransactionStatus;
import com.banksystem.transactionservice.enums.TransactionType;
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
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static TransactionResponse fromEntity(Transaction transaction) {
        return new TransactionResponse(
            transaction.getId(),
            transaction.getFromAccountId(),
            transaction.getToAccountId(),
            transaction.getAmount(),
            transaction.getType(),
            transaction.getStatus(),
            transaction.getDescription(),
            transaction.getCreatedAt(),
            transaction.getUpdatedAt()
        );
    }
}
