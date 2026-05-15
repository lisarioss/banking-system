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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public TransactionResponse createTransaction(TransactionRequest request) {
        log.info("Criando nova transação: {} -> {}", request.getFromAccountId(), request.getToAccountId());

        Transaction transaction = Transaction.builder()
            .fromAccountId(request.getFromAccountId())
            .toAccountId(request.getToAccountId())
            .amount(request.getAmount())
            .type(request.getType())
            .status(TransactionStatus.PENDING)
            .description(request.getDescription())
            .build();

        Transaction savedTransaction = transactionRepository.save(transaction);

        // Publicar evento no Kafka
        kafkaTemplate.send("transaction-events", 
            "Transação criada: " + savedTransaction.getId());

        log.info("Transação criada com sucesso: {}", savedTransaction.getId());
        return TransactionResponse.fromEntity(savedTransaction);
    }

    public TransactionResponse getTransactionById(Long id) {
        Transaction transaction = transactionRepository.findById(id)
            .orElseThrow(() -> new TransactionNotFoundException("Transação não encontrada"));
        return TransactionResponse.fromEntity(transaction);
    }

    public Page<TransactionResponse> getTransactionsByFromAccount(Long fromAccountId, Pageable pageable) {
        Page<Transaction> transactions = transactionRepository.findByFromAccountId(fromAccountId, pageable);
        return new PageImpl<>(
            transactions.getContent().stream()
                .map(TransactionResponse::fromEntity)
                .collect(Collectors.toList()),
            pageable,
            transactions.getTotalElements()
        );
    }

    public Page<Transaction> getTransactionsByToAccount(Long toAccountId, Pageable pageable) {
        return transactionRepository.findByToAccountId(toAccountId, pageable);
    }

    public TransactionResponse completeTransaction(Long id) {
        Transaction transaction = transactionRepository.findById(id)
            .orElseThrow(() -> new TransactionNotFoundException("Transação não encontrada"));

        transaction.setStatus(TransactionStatus.COMPLETED);
        Transaction updatedTransaction = transactionRepository.save(transaction);

        kafkaTemplate.send("transaction-events", 
            "Transação completada: " + updatedTransaction.getId());

        log.info("Transação completada: {}", updatedTransaction.getId());
        return TransactionResponse.fromEntity(updatedTransaction);
    }

    public TransactionResponse cancelTransaction(Long id) {
        Transaction transaction = transactionRepository.findById(id)
            .orElseThrow(() -> new TransactionNotFoundException("Transação não encontrada"));

        transaction.setStatus(TransactionStatus.CANCELLED);
        Transaction updatedTransaction = transactionRepository.save(transaction);

        kafkaTemplate.send("transaction-events", 
            "Transação cancelada: " + updatedTransaction.getId());

        log.info("Transação cancelada: {}", updatedTransaction.getId());
        return TransactionResponse.fromEntity(updatedTransaction);
    }
}
