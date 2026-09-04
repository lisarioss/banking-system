package com.banksystem.accountservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountOperationResponse {

    private String message;
    private AccountResponse account;
    private BigDecimal previousBalance;
    private BigDecimal newBalance;
    private LocalDateTime timestamp;
}
