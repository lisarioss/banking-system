package com.banksystem.transactionservice.dto;

import com.banksystem.transactionservice.enums.TransactionType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRequest {

    @NotNull(message = "Conta de origem é obrigatória")
    private Long fromAccountId;

    @NotNull(message = "Conta de destino é obrigatória")
    private Long toAccountId;

    @NotNull(message = "Valor é obrigatório")
    @DecimalMin(value = "0.01", message = "Valor deve ser maior que zero")
    private BigDecimal amount;

    @NotNull(message = "Tipo de transação é obrigatório")
    private TransactionType type;

    @Size(max = 500, message = "Descrição não pode exceder 500 caracteres")
    private String description;
}
