package com.bank.service;

import java.util.List;

import com.bank.entity.Account;
import com.bank.entity.Transaction;

public interface AccountService {

    void createAccount(Account account);

    boolean createAccountDirect(Account account);

    Account getAccount(int accountNo);

    void deposit(int accountNo, double amount);

    void withdraw(int accountNo, double amount);

    void transfer(int sender, int receiver, double amount);

    void deleteAccount(int accountNo);

    List<Account> getAllAccounts();

    Account getAccountByUserId(int userId);

    List<Transaction> getTransactionHistory(int accountNo);

}