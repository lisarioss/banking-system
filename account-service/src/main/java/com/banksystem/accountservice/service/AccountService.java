package com.banksystem.accountservice.service;

import com.banksystem.accountservice.dto.AccountOperationResponse;
import com.banksystem.accountservice.dto.CreateAccountRequest;
import com.banksystem.accountservice.dto.DepositRequest;
import com.banksystem.accountservice.dto.WithdrawRequest;
import com.banksystem.accountservice.dto.AccountResponse;
import com.banksystem.accountservice.entity.Account;
import com.banksystem.accountservice.exception.AccountNotFoundException;
import com.banksystem.accountservice.exception.InsufficientFundsException;
import com.banksystem.accountservice.exception.InvalidOperationException;
import com.banksystem.accountservice.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AccountService {

    private final AccountRepository accountRepository;
    private static final Random random = new Random();

    public AccountResponse createAccount(CreateAccountRequest request) {
        String accountNumber = generateAccountNumber();
        String accountDigit = generateAccountDigit();

        Account account = Account.builder()
            .accountNumber(accountNumber)
            .accountDigit(accountDigit)
            .userId(request.getUserId())
            .accountType(request.getAccountType())
            .balance(BigDecimal.ZERO)
            .creditLimit(request.getCreditLimit())
            .active(true)
            .build();

        Account savedAccount = accountRepository.save(account);
        return AccountResponse.fromEntity(savedAccount);
    }

    public AccountResponse getAccountById(Long id) {
        Account account = accountRepository.findById(id)
            .orElseThrow(() -> new AccountNotFoundException("Conta não encontrada"));
        return AccountResponse.fromEntity(account);
    }

    public AccountResponse getAccountByNumber(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
            .orElseThrow(() -> new AccountNotFoundException("Conta não encontrada"));
        return AccountResponse.fromEntity(account);
    }

    public List<AccountResponse> getAccountsByUserId(Long userId) {
        return accountRepository.findByUserId(userId).stream()
            .map(AccountResponse::fromEntity)
            .collect(Collectors.toList());
    }

    public List<AccountResponse> getActiveAccountsByUserId(Long userId) {
        return accountRepository.findByUserIdAndActive(userId, true).stream()
            .map(AccountResponse::fromEntity)
            .collect(Collectors.toList());
    }

    public AccountOperationResponse deposit(Long accountId, DepositRequest request) {
        Account account = accountRepository.findById(accountId)
            .orElseThrow(() -> new AccountNotFoundException("Conta não encontrada"));

        if (!account.getActive()) {
            throw new InvalidOperationException("Conta inativa");
        }

        BigDecimal previousBalance = account.getBalance();
        account.setBalance(account.getBalance().add(request.getAmount()));
        Account updatedAccount = accountRepository.save(account);

        return new AccountOperationResponse(
            "Depósito realizado com sucesso",
            AccountResponse.fromEntity(updatedAccount),
            previousBalance,
            updatedAccount.getBalance(),
            LocalDateTime.now()
        );
    }

    public AccountOperationResponse withdraw(Long accountId, WithdrawRequest request) {
        Account account = accountRepository.findById(accountId)
            .orElseThrow(() -> new AccountNotFoundException("Conta não encontrada"));

        if (!account.getActive()) {
            throw new InvalidOperationException("Conta inativa");
        }

        BigDecimal availableFunds = account.getBalance().add(account.getCreditLimit());
        if (request.getAmount().compareTo(availableFunds) > 0) {
            throw new InsufficientFundsException("Saldo insuficiente");
        }

        BigDecimal previousBalance = account.getBalance();
        account.setBalance(account.getBalance().subtract(request.getAmount()));
        Account updatedAccount = accountRepository.save(account);

        return new AccountOperationResponse(
            "Saque realizado com sucesso",
            AccountResponse.fromEntity(updatedAccount),
            previousBalance,
            updatedAccount.getBalance(),
            LocalDateTime.now()
        );
    }

    public void deactivateAccount(Long accountId) {
        Account account = accountRepository.findById(accountId)
            .orElseThrow(() -> new AccountNotFoundException("Conta não encontrada"));
        account.setActive(false);
        accountRepository.save(account);
    }

    private String generateAccountNumber() {
        String accountNumber;
        do {
            accountNumber = String.format("%08d", random.nextInt(100000000));
        } while (accountRepository.existsByAccountNumber(accountNumber));
        return accountNumber;
    }

    private String generateAccountDigit() {
        return String.valueOf(random.nextInt(10));
    }
}
