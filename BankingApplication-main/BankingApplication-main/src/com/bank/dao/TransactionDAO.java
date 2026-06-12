package com.bank.dao;

import java.util.List;
import com.bank.entity.Transaction;

public interface TransactionDAO {

    boolean addTransaction(Transaction transaction);

    List<Transaction> getTransactionsByAccount(int accountNo);

}