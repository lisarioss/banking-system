package com.banksystem.accountservice.service;

import com.banksystem.accountservice.dto.AccountOperationResponse;
import com.banksystem.accountservice.dto.AccountResponse;
import com.banksystem.accountservice.dto.CreateAccountRequest;
import com.banksystem.accountservice.dto.DepositRequest;
import com.banksystem.accountservice.dto.WithdrawRequest;
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
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
@Transactional
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountResponse createAccount(CreateAccountRequest request) {

        Objects.requireNonNull(request, "Dados da conta são obrigatórios");

        String accountNumber = generateAccountNumber();
        String accountDigit = generateAccountDigit();

        Account account = Account.builder()
            .accountNumber(accountNumber)
            .accountDigit(accountDigit)
            .userId(request.getUserId())
            .accountType(request.getAccountType())
            .balance(BigDecimal.ZERO)
            .creditLimit(
                request.getCreditLimit() != null
                    ? request.getCreditLimit()
                    : BigDecimal.ZERO
            )
            .active(true)
            .build();

        Account savedAccount =
            Objects.requireNonNull(
                accountRepository.save(account),
                "Erro ao salvar a conta"
            );

        return AccountResponse.fromEntity(savedAccount);
    }

    @Transactional(readOnly = true)
    public AccountResponse getAccountById(Long id) {

        Objects.requireNonNull(id, "ID da conta é obrigatório");

        Account account = accountRepository.findById(id)
            .orElseThrow(() ->
                new AccountNotFoundException("Conta não encontrada")
            );

        return AccountResponse.fromEntity(account);
    }

    @Transactional(readOnly = true)
    public AccountResponse getAccountByNumber(String accountNumber) {

        Objects.requireNonNull(
            accountNumber,
            "Número da conta é obrigatório"
        );

        Account account = accountRepository
            .findByAccountNumber(accountNumber)
            .orElseThrow(() ->
                new AccountNotFoundException("Conta não encontrada")
            );

        return AccountResponse.fromEntity(account);
    }

    @Transactional(readOnly = true)
    public List<AccountResponse> getAccountsByUserId(Long userId) {

        Objects.requireNonNull(userId, "ID do usuário é obrigatório");

        return accountRepository.findByUserId(userId)
            .stream()
            .map(AccountResponse::fromEntity)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<AccountResponse> getActiveAccountsByUserId(Long userId) {

        Objects.requireNonNull(userId, "ID do usuário é obrigatório");

        return accountRepository
            .findByUserIdAndActive(userId, true)
            .stream()
            .map(AccountResponse::fromEntity)
            .toList();
    }

    public AccountOperationResponse deposit(
        Long accountId,
        DepositRequest request
    ) {

        Objects.requireNonNull(accountId, "ID da conta é obrigatório");
        Objects.requireNonNull(request, "Dados do depósito são obrigatórios");

        Account account = accountRepository.findById(accountId)
            .orElseThrow(() ->
                new AccountNotFoundException("Conta não encontrada")
            );

        validateActiveAccount(account);

        BigDecimal amount = Objects.requireNonNull(
            request.getAmount(),
            "Valor do depósito é obrigatório"
        );

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidOperationException(
                "O valor do depósito deve ser maior que zero"
            );
        }

        BigDecimal previousBalance = account.getBalance();

        account.setBalance(previousBalance.add(amount));

        Account updatedAccount =
            Objects.requireNonNull(
                accountRepository.save(account),
                "Erro ao atualizar a conta"
            );

        return new AccountOperationResponse(
            "Depósito realizado com sucesso",
            AccountResponse.fromEntity(updatedAccount),
            previousBalance,
            updatedAccount.getBalance(),
            LocalDateTime.now()
        );
    }

    public AccountOperationResponse withdraw(
        Long accountId,
        WithdrawRequest request
    ) {

        Objects.requireNonNull(accountId, "ID da conta é obrigatório");
        Objects.requireNonNull(request, "Dados do saque são obrigatórios");

        Account account = accountRepository.findById(accountId)
            .orElseThrow(() ->
                new AccountNotFoundException("Conta não encontrada")
            );

        validateActiveAccount(account);

        BigDecimal amount = Objects.requireNonNull(
            request.getAmount(),
            "Valor do saque é obrigatório"
        );

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidOperationException(
                "O valor do saque deve ser maior que zero"
            );
        }

        BigDecimal balance = account.getBalance();

        BigDecimal creditLimit =
            account.getCreditLimit() != null
                ? account.getCreditLimit()
                : BigDecimal.ZERO;

        BigDecimal availableFunds =
            balance.add(creditLimit);

        if (amount.compareTo(availableFunds) > 0) {
            throw new InsufficientFundsException(
                "Saldo insuficiente"
            );
        }

        BigDecimal previousBalance = balance;

        account.setBalance(balance.subtract(amount));

        Account updatedAccount =
            Objects.requireNonNull(
                accountRepository.save(account),
                "Erro ao atualizar a conta"
            );

        return new AccountOperationResponse(
            "Saque realizado com sucesso",
            AccountResponse.fromEntity(updatedAccount),
            previousBalance,
            updatedAccount.getBalance(),
            LocalDateTime.now()
        );
    }

    public void deactivateAccount(Long accountId) {

        Objects.requireNonNull(accountId, "ID da conta é obrigatório");

        Account account = accountRepository.findById(accountId)
            .orElseThrow(() ->
                new AccountNotFoundException("Conta não encontrada")
            );

        if (!Boolean.TRUE.equals(account.getActive())) {
            throw new InvalidOperationException(
                "Conta já está inativa"
            );
        }

        account.setActive(false);

        accountRepository.save(account);
    }

    private void validateActiveAccount(Account account) {

        if (!Boolean.TRUE.equals(account.getActive())) {
            throw new InvalidOperationException("Conta inativa");
        }
    }

    private String generateAccountNumber() {

        String accountNumber;

        do {
            accountNumber = String.format(
                "%08d",
                ThreadLocalRandom.current()
                    .nextInt(100_000_000)
            );
        } while (
            accountRepository.existsByAccountNumber(accountNumber)
        );

        return accountNumber;
    }

    private String generateAccountDigit() {

        return String.valueOf(
            ThreadLocalRandom.current().nextInt(10)
        );
    }
}