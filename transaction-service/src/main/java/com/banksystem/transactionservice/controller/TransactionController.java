package com.banksystem.transactionservice.controller;

import com.banksystem.transactionservice.dto.TransactionRequest;
import com.banksystem.transactionservice.dto.TransactionResponse;
import com.banksystem.transactionservice.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(
        @Valid @RequestBody TransactionRequest request) {
        TransactionResponse response = transactionService.createTransaction(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> getTransaction(@PathVariable Long id) {
        TransactionResponse response = transactionService.getTransactionById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<Page<TransactionResponse>> getAccountTransactions(
        @PathVariable Long accountId,
        Pageable pageable) {
        Page<TransactionResponse> response = transactionService.getTransactionsByFromAccount(accountId, pageable);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<TransactionResponse> completeTransaction(@PathVariable Long id) {
        TransactionResponse response = transactionService.completeTransaction(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<TransactionResponse> cancelTransaction(@PathVariable Long id) {
        TransactionResponse response = transactionService.cancelTransaction(id);
        return ResponseEntity.ok(response);
    }
}
