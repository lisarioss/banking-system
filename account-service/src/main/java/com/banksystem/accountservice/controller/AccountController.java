package com.banksystem.accountservice.controller;

import com.banksystem.accountservice.dto.*;
import com.banksystem.accountservice.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(@Valid @RequestBody CreateAccountRequest request) {
        AccountResponse response = accountService.createAccount(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountResponse> getAccountById(@PathVariable Long id) {
        AccountResponse response = accountService.getAccountById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/number/{accountNumber}")
    public ResponseEntity<AccountResponse> getAccountByNumber(@PathVariable String accountNumber) {
        AccountResponse response = accountService.getAccountByNumber(accountNumber);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AccountResponse>> getAccountsByUserId(@PathVariable Long userId) {
        List<AccountResponse> response = accountService.getAccountsByUserId(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}/active")
    public ResponseEntity<List<AccountResponse>> getActiveAccountsByUserId(@PathVariable Long userId) {
        List<AccountResponse> response = accountService.getActiveAccountsByUserId(userId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/deposit")
    public ResponseEntity<AccountOperationResponse> deposit(
        @PathVariable Long id,
        @Valid @RequestBody DepositRequest request) {
        AccountOperationResponse response = accountService.deposit(id, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/withdraw")
    public ResponseEntity<AccountOperationResponse> withdraw(
        @PathVariable Long id,
        @Valid @RequestBody WithdrawRequest request) {
        AccountOperationResponse response = accountService.withdraw(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivateAccount(@PathVariable Long id) {
        accountService.deactivateAccount(id);
        return ResponseEntity.noContent().build();
    }
}
