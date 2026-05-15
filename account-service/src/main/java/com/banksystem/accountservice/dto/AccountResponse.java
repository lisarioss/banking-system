package com.banksystem.accountservice.dto;

import com.banksystem.accountservice.entity.Account;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountResponse {
    private Long id;
    private Long userId;
    private String accountNumber;
    private String accountType;
    private BigDecimal balance;
    private BigDecimal limit;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static AccountResponse fromEntity(Account account) {
        return new AccountResponse(
            account.getId(),
            account.getUserId(),
            account.getAccountNumber(),
            account.getAccountType(),
            account.getBalance(),
            account.getLimit(),
            account.getActive(),
            account.getCreatedAt(),
            account.getUpdatedAt()
        );
    }
}
