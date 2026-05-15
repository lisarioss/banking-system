package com.banksystem.accountservice.dto;

import com.banksystem.accountservice.entity.Account;
import com.banksystem.accountservice.entity.AccountType;
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
    private String accountNumber;
    private String accountDigit;
    private Long userId;
    private AccountType accountType;
    private BigDecimal balance;
    private BigDecimal creditLimit;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static AccountResponse fromEntity(Account account) {
        return new AccountResponse(
            account.getId(),
            account.getAccountNumber(),
            account.getAccountDigit(),
            account.getUserId(),
            account.getAccountType(),
            account.getBalance(),
            account.getCreditLimit(),
            account.getActive(),
            account.getCreatedAt(),
            account.getUpdatedAt()
        );
    }
}
