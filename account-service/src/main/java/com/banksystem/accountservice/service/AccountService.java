package com.banksystem.accountservice.service;

import com.banksystem.accountservice.dto.AccountResponse;
import com.banksystem.accountservice.dto.CreateAccountRequest;
import com.banksystem.accountservice.entity.Account;
import com.banksystem.accountservice.exception.AccountNotFoundException;
import com.banksystem.accountservice.exception.InvalidOperationException;
import com.banksystem.accountservice.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountResponse createAccount(CreateAccountRequest request) {
        String accountNumber = generateAccountNumber();

        Account account = Account.builder()
            .userId(request.getUserId())
            .accountNumber(accountNumber)
            .accountType(request.getAccountType())
            .balance(request.getInitialBalance() != null ? request.getInitialBalance() : BigDecimal.ZERO)
            .limit(request.getLimit() != null ? request.getLimit() : BigDecimal.ZERO)
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

    public AccountResponse deposit(Long accountId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidOperationException("Valor do depósito deve ser maior que zero");
        }

        Account account = accountRepository.findById(accountId)
            .orElseThrow(() -> new AccountNotFoundException("Conta não encontrada"));

        account.setBalance(account.getBalance().add(amount));
        Account updatedAccount = accountRepository.save(account);
        return AccountResponse.fromEntity(updatedAccount);
    }

    public AccountResponse withdraw(Long accountId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidOperationException("Valor do saque deve ser maior que zero");
        }

        Account account = accountRepository.findById(accountId)
            .orElseThrow(() -> new AccountNotFoundException("Conta não encontrada"));

        BigDecimal availableBalance = account.getBalance().add(account.getLimit());
        if (amount.compareTo(availableBalance) > 0) {
            throw new InvalidOperationException("Saldo insuficiente");
        }

        account.setBalance(account.getBalance().subtract(amount));
        Account updatedAccount = accountRepository.save(account);
        return AccountResponse.fromEntity(updatedAccount);
    }

    public AccountResponse updateLimit(Long accountId, BigDecimal newLimit) {
        Account account = accountRepository.findById(accountId)
            .orElseThrow(() -> new AccountNotFoundException("Conta não encontrada"));

        account.setLimit(newLimit);
        Account updatedAccount = accountRepository.save(account);
        return AccountResponse.fromEntity(updatedAccount);
    }

    public void deactivateAccount(Long accountId) {
        Account account = accountRepository.findById(accountId)
            .orElseThrow(() -> new AccountNotFoundException("Conta não encontrada"));
        account.setActive(false);
        accountRepository.save(account);
    }

    private String generateAccountNumber() {
        return UUID.randomUUID().toString().substring(0, 10).toUpperCase();
    }

    protected Account findAccountById(Long id) {
        return accountRepository.findById(id)
            .orElseThrow(() -> new AccountNotFoundException("Conta não encontrada"));
    }
}
