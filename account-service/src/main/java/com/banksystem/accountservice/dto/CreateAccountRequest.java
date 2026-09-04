package com.banksystem.accountservice.dto;

import com.banksystem.accountservice.entity.AccountType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateAccountRequest {

    @NotNull(message = "User ID é obrigatório")
    private Long userId;

    @NotNull(message = "Tipo de conta é obrigatório")
    private AccountType accountType;

    @NotNull(message = "Limite de crédito é obrigatório")
    @Positive(message = "Limite de crédito deve ser positivo")
    private BigDecimal creditLimit;
}
