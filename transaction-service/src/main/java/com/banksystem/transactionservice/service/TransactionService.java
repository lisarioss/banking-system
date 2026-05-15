package com.banksystem.transactionservice.service;

import com.banksystem.transactionservice.dto.DepositRequest;
import com.banksystem.transactionservice.dto.TransactionResponse;
import com.banksystem.transactionservice.dto.TransferRequest;
import com.banksystem.transactionservice.entity.Transaction;
import com.banksystem.transactionservice.entity.TransactionStatus;
import com.banksystem.transactionservice.entity.TransactionType;
import com.banksystem.transactionservice.exception.TransactionFailedException;
import com.banksystem.transactionservice.exception.TransactionNotFoundException;
import com.banksystem.transactionservice.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final RabbitTemplate rabbitTemplate;

    public TransactionResponse transfer(TransferRequest request) {
        log.info("Iniciando transferência de {} para {}", request.getFromAccountId(), request.getToAccountId());

        if (request.getFromAccountId().equals(request.getToAccountId())) {
            throw new TransactionFailedException("Não é possível transferir para a mesma conta");
        }

        Transaction transaction = Transaction.builder()
            .fromAccountId(request.getFromAccountId())
            .toAccountId(request.getToAccountId())
            .amount(request.getAmount())
            .type(TransactionType.TRANSFER)
            .status(TransactionStatus.PROCESSING)
            .description(request.getDescription())
            .build();

        Transaction savedTransaction = transactionRepository.save(transaction);
        
        // Enviar para fila de processamento
        rabbitTemplate.convertAndSend("transactions.exchange", "transactions.transfer", savedTransaction.getId());

        return TransactionResponse.fromEntity(savedTransaction);
    }

    public TransactionResponse deposit(DepositRequest request) {
        log.info("Iniciando depósito de {} na conta {}", request.getAmount(), request.getAccountId());

        Transaction transaction = Transaction.builder()
            .fromAccountId(request.getAccountId())
            .toAccountId(request.getAccountId())
            .amount(request.getAmount())
            .type(TransactionType.DEPOSIT)
            .status(TransactionStatus.PROCESSING)
            .description(request.getDescription())
            .build();

        Transaction savedTransaction = transactionRepository.save(transaction);
        
        // Enviar para fila de processamento
        rabbitTemplate.convertAndSend("transactions.exchange", "transactions.deposit", savedTransaction.getId());

        return TransactionResponse.fromEntity(savedTransaction);
    }

    public TransactionResponse getTransactionById(Long id) {
        Transaction transaction = transactionRepository.findById(id)
            .orElseThrow(() -> new TransactionNotFoundException("Transação não encontrada"));
        return TransactionResponse.fromEntity(transaction);
    }

    public List<TransactionResponse> getAccountTransactionHistory(Long accountId) {
        log.info("Buscando histórico de transações da conta {}", accountId);
        return transactionRepository.findAccountTransactionHistory(accountId).stream()
            .map(TransactionResponse::fromEntity)
            .collect(Collectors.toList());
    }

    public List<TransactionResponse> getPendingTransactions() {
        return transactionRepository.findByStatusOrderByCreatedAtDesc(TransactionStatus.PENDING).stream()
            .map(TransactionResponse::fromEntity)
            .collect(Collectors.toList());
    }

    public void completeTransaction(Long transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
            .orElseThrow(() -> new TransactionNotFoundException("Transação não encontrada"));
        
        transaction.setStatus(TransactionStatus.COMPLETED);
        transaction.setCompletedAt(LocalDateTime.now());
        transactionRepository.save(transaction);
        
        log.info("Transação {} completada com sucesso", transactionId);
    }

    public void failTransaction(Long transactionId, String reason) {
        Transaction transaction = transactionRepository.findById(transactionId)
            .orElseThrow(() -> new TransactionNotFoundException("Transação não encontrada"));
        
        transaction.setStatus(TransactionStatus.FAILED);
        transaction.setDescription(reason);
        transactionRepository.save(transaction);
        
        log.error("Transação {} falhou: {}", transactionId, reason);
    }
}
