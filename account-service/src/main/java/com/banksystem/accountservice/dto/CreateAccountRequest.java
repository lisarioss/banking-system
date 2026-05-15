package com.banksystem.accountservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateAccountRequest {
    private Long userId;
    private String accountType; // CHECKING, SAVINGS
    private BigDecimal initialBalance;
    private BigDecimal limit;
}
