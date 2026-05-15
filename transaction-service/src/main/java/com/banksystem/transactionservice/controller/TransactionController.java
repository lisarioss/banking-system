package com.banksystem.transactionservice.controller;

import com.banksystem.transactionservice.dto.DepositRequest;
import com.banksystem.transactionservice.dto.TransactionResponse;
import com.banksystem.transactionservice.dto.TransferRequest;
import com.banksystem.transactionservice.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/transfer")
    public ResponseEntity<TransactionResponse> transfer(@Valid @RequestBody TransferRequest request) {
        TransactionResponse response = transactionService.transfer(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @PostMapping("/deposit")
    public ResponseEntity<TransactionResponse> deposit(@Valid @RequestBody DepositRequest request) {
        TransactionResponse response = transactionService.deposit(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> getTransaction(@PathVariable Long id) {
        TransactionResponse response = transactionService.getTransactionById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/history/{accountId}")
    public ResponseEntity<List<TransactionResponse>> getTransactionHistory(@PathVariable Long accountId) {
        List<TransactionResponse> response = transactionService.getAccountTransactionHistory(accountId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/pending")
    public ResponseEntity<List<TransactionResponse>> getPendingTransactions() {
        List<TransactionResponse> response = transactionService.getPendingTransactions();
        return ResponseEntity.ok(response);
    }
}
