package com.banksystem.transactionservice.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferRequest {

    @NotNull(message = "Conta de origem é obrigatória")
    private Long fromAccountId;

    @NotNull(message = "Conta de destino é obrigatória")
    private Long toAccountId;

    @NotNull(message = "Valor é obrigatório")
    @DecimalMin(value = "0.01", message = "Valor deve ser maior que 0")
    private BigDecimal amount;

    private String description;
}
