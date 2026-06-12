package com.bank.dao;

import java.util.List;

import com.bank.entity.Account;

public interface AccountDAO {

	boolean createAccount(Account account);

	Account findById(int accountNo);

	Account findByUserId(int userId);

	boolean updateBalance(int accountNo, double balance);
// Create stored procedure
	boolean deleteAccount(int accountNo);

	boolean login(String username, String password);

	List<Account> getAllAccounts();
}
