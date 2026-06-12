package com.bank.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.bank.dao.AccountDAO;
import com.bank.dao.TransactionDAO;
import com.bank.dao.UserDAO;
import com.bank.entity.Account;
import com.bank.entity.Transaction;
import com.bank.exception.AccountNotFoundException;
import com.bank.exception.BankingException;
import com.bank.exception.ErrorCode;
import com.bank.exception.InsufficientBalanceException;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountDAO dao;
    private final UserDAO userDAO;
    private final TransactionDAO transactionDAO;

    public AccountServiceImpl(AccountDAO dao, UserDAO userDAO, TransactionDAO transactionDAO) {
        this.dao = dao;
        this.userDAO = userDAO;
        this.transactionDAO = transactionDAO;
    }

    @Override
    public void createAccount(Account account) {

        int userId = userDAO.createUser(
                account.getAccountHolderName(),
                account.getEmail(),
                account.getEmail(),
                "CUSTOMER");

        if (userId == -1) {
            throw new BankingException("User creation failed.", ErrorCode.USER_CREATION_FAILED);
        }

        account.setUserId(userId);

        if (!dao.createAccount(account)) {
            throw new BankingException("Account creation failed.", ErrorCode.ACCOUNT_CREATION_FAILED);
        }
    }

    @Override
    public boolean createAccountDirect(Account account) {
        return dao.createAccount(account);
    }

    @Override
    public Account getAccount(int accountNo) {
        return dao.findById(accountNo);
    }

    @Override
    public void deposit(int accountNo, double amount) {

        if (amount <= 0) {
            throw new BankingException("Deposit amount must be greater than zero.", ErrorCode.INVALID_AMOUNT);
        }

        Account account = dao.findById(accountNo);
        if (account == null) {
            throw new AccountNotFoundException("Account not found: " + accountNo);
        }

        double newBalance = account.getBalance() + amount;
        if (!dao.updateBalance(accountNo, newBalance)) {
            throw new BankingException("Deposit failed.", ErrorCode.TRANSFER_FAILED);
        }

        transactionDAO.addTransaction(new Transaction(accountNo, 0, "DEPOSIT", amount));
    }

    @Override
    public void withdraw(int accountNo, double amount) {

        if (amount <= 0) {
            throw new BankingException("Withdrawal amount must be greater than zero.", ErrorCode.INVALID_AMOUNT);
        }

        Account account = dao.findById(accountNo);
        if (account == null) {
            throw new AccountNotFoundException("Account not found: " + accountNo);
        }

        if (account.getBalance() < amount) {
            throw new InsufficientBalanceException(
                    "Insufficient balance. Available: " + String.format("%.2f", account.getBalance()));
        }

        double newBalance = account.getBalance() - amount;
        if (!dao.updateBalance(accountNo, newBalance)) {
            throw new BankingException("Withdrawal failed.", ErrorCode.TRANSFER_FAILED);
        }

        transactionDAO.addTransaction(new Transaction(accountNo, 0, "WITHDRAWAL", amount));
    }

    @Override
    public void transfer(int sender, int receiver, double amount) {

        if (amount <= 0) {
            throw new BankingException("Transfer amount must be greater than zero.", ErrorCode.INVALID_AMOUNT);
        }

        Account senderAcc = dao.findById(sender);
        if (senderAcc == null) {
            throw new AccountNotFoundException("Sender account not found: " + sender);
        }

        Account receiverAcc = dao.findById(receiver);
        if (receiverAcc == null) {
            throw new AccountNotFoundException("Receiver account not found: " + receiver);
        }

        if (senderAcc.getBalance() < amount) {
            throw new InsufficientBalanceException(
                    "Insufficient balance. Available: " + String.format("%.2f", senderAcc.getBalance()));
        }

        dao.updateBalance(sender, senderAcc.getBalance() - amount);
        dao.updateBalance(receiver, receiverAcc.getBalance() + amount);
        transactionDAO.addTransaction(new Transaction(sender, receiver, "TRANSFER", amount));
    }

    @Override
    public void deleteAccount(int accountNo) {

        if (!dao.deleteAccount(accountNo)) {
            throw new AccountNotFoundException("Account not found: " + accountNo);
        }
    }

    @Override
    public List<Account> getAllAccounts() {
        return dao.getAllAccounts();
    }

    @Override
    public Account getAccountByUserId(int userId) {
        return dao.findByUserId(userId);
    }

    @Override
    public List<Transaction> getTransactionHistory(int accountNo) {
        return transactionDAO.getTransactionsByAccount(accountNo);
    }
}