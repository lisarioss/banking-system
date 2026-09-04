package com.banksystem.transactionservice.service;

import com.banksystem.transactionservice.dto.TransactionRequest;
import com.banksystem.transactionservice.dto.TransactionResponse;
import com.banksystem.transactionservice.entity.Transaction;
import com.banksystem.transactionservice.enums.TransactionStatus;
import com.banksystem.transactionservice.exception.TransactionNotFoundException;
import com.banksystem.transactionservice.repository.TransactionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class TransactionService {

    private static final String TRANSACTION_TOPIC = "transaction-events";

    private final TransactionRepository transactionRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public TransactionResponse createTransaction(TransactionRequest request) {

        Objects.requireNonNull(
            request,
            "Dados da transação são obrigatórios"
        );

        Objects.requireNonNull(
            request.getFromAccountId(),
            "Conta de origem é obrigatória"
        );

        Objects.requireNonNull(
            request.getToAccountId(),
            "Conta de destino é obrigatória"
        );

        BigDecimal amount = Objects.requireNonNull(
            request.getAmount(),
            "Valor da transação é obrigatório"
        );

        Objects.requireNonNull(
            request.getType(),
            "Tipo da transação é obrigatório"
        );

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                "O valor da transação deve ser maior que zero"
            );
        }

        if (request.getFromAccountId()
                .equals(request.getToAccountId())) {
            throw new IllegalArgumentException(
                "Conta de origem e conta de destino devem ser diferentes"
            );
        }

        log.info(
            "Criando nova transação: {} -> {}",
            request.getFromAccountId(),
            request.getToAccountId()
        );

        Transaction transaction = Transaction.builder()
            .fromAccountId(request.getFromAccountId())
            .toAccountId(request.getToAccountId())
            .amount(amount)
            .type(request.getType())
            .status(TransactionStatus.PENDING)
            .description(request.getDescription())
            .build();

        Transaction savedTransaction =
            transactionRepository.save(transaction);

        kafkaTemplate.send(
            TRANSACTION_TOPIC,
            "Transação criada: " + savedTransaction.getId()
        );

        log.info(
            "Transação criada com sucesso: {}",
            savedTransaction.getId()
        );

        return TransactionResponse.fromEntity(savedTransaction);
    }

    @Transactional(readOnly = true)
    public TransactionResponse getTransactionById(Long id) {

        Objects.requireNonNull(
            id,
            "ID da transação é obrigatório"
        );

        Transaction transaction = findTransactionById(id);

        return TransactionResponse.fromEntity(transaction);
    }

    @Transactional(readOnly = true)
    public Page<TransactionResponse> getTransactionsByFromAccount(
        Long fromAccountId,
        Pageable pageable
    ) {

        Objects.requireNonNull(
            fromAccountId,
            "ID da conta de origem é obrigatório"
        );

        Objects.requireNonNull(
            pageable,
            "Paginação é obrigatória"
        );

        return transactionRepository
            .findByFromAccountId(fromAccountId, pageable)
            .map(TransactionResponse::fromEntity);
    }

    @Transactional(readOnly = true)
    public Page<TransactionResponse> getTransactionsByToAccount(
        Long toAccountId,
        Pageable pageable
    ) {

        Objects.requireNonNull(
            toAccountId,
            "ID da conta de destino é obrigatório"
        );

        Objects.requireNonNull(
            pageable,
            "Paginação é obrigatória"
        );

        return transactionRepository
            .findByToAccountId(toAccountId, pageable)
            .map(TransactionResponse::fromEntity);
    }

    public TransactionResponse completeTransaction(Long id) {

        Objects.requireNonNull(
            id,
            "ID da transação é obrigatório"
        );

        Transaction transaction = findTransactionById(id);

        if (transaction.getStatus() != TransactionStatus.PENDING) {
            throw new IllegalStateException(
                "Apenas transações pendentes podem ser concluídas"
            );
        }

        transaction.setStatus(TransactionStatus.COMPLETED);

        Transaction updatedTransaction =
            transactionRepository.save(transaction);

        kafkaTemplate.send(
            TRANSACTION_TOPIC,
            "Transação completada: " + updatedTransaction.getId()
        );

        log.info(
            "Transação completada: {}",
            updatedTransaction.getId()
        );

        return TransactionResponse.fromEntity(updatedTransaction);
    }

    public TransactionResponse cancelTransaction(Long id) {

        Objects.requireNonNull(
            id,
            "ID da transação é obrigatório"
        );

        Transaction transaction = findTransactionById(id);

        if (transaction.getStatus() != TransactionStatus.PENDING) {
            throw new IllegalStateException(
                "Apenas transações pendentes podem ser canceladas"
            );
        }

        transaction.setStatus(TransactionStatus.CANCELLED);

        Transaction updatedTransaction =
            transactionRepository.save(transaction);

        kafkaTemplate.send(
            TRANSACTION_TOPIC,
            "Transação cancelada: " + updatedTransaction.getId()
        );

        log.info(
            "Transação cancelada: {}",
            updatedTransaction.getId()
        );

        return TransactionResponse.fromEntity(updatedTransaction);
    }

    private Transaction findTransactionById(Long id) {

        return transactionRepository.findById(id)
            .orElseThrow(() ->
                new TransactionNotFoundException(
                    "Transação não encontrada"
                )
            );
    }
}