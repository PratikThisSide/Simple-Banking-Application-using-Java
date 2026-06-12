package com.bank.dao;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.bank.entity.Account;

@Repository
public class AccountDAOImpl implements AccountDAO {

    private final JdbcTemplate jdbc;

    public AccountDAOImpl(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static final RowMapper<Account> ACCOUNT_MAPPER = (rs, rowNum) -> {
        Account a = new Account();
        a.setAccountNo(rs.getInt("account_no"));
        a.setUserId(rs.getInt("user_id"));
        a.setAccountHolderName(rs.getString("account_holder_name"));
        a.setAccountType(rs.getString("account_type"));
        a.setBalance(rs.getDouble("balance"));
        return a;
    };

    @Override
    public boolean createAccount(Account account) {
        String sql = "INSERT INTO accounts(user_id, account_holder_name, account_type, balance) VALUES(?,?,?,?)";
        return jdbc.update(sql,
                account.getUserId(),
                account.getAccountHolderName(),
                account.getAccountType(),
                account.getBalance()) > 0;
    }

    @Override
    public Account findById(int accountNo) {
        String sql = "SELECT * FROM accounts WHERE account_no=?";
        List<Account> list = jdbc.query(sql, ACCOUNT_MAPPER, accountNo);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public boolean updateBalance(int accountNo, double balance) {
        String sql = "UPDATE accounts SET balance=? WHERE account_no=?";
        return jdbc.update(sql, balance, accountNo) > 0;
    }

    @Override
    public boolean deleteAccount(int accountNo) {
        String sql = "DELETE FROM accounts WHERE account_no=?";
        return jdbc.update(sql, accountNo) > 0;
    }

	@Override
    @Override
    public boolean login(String username, String password) {
        String sql = "SELECT COUNT(*) FROM users WHERE username=? AND password=?";
        Integer count = jdbc.queryForObject(sql, Integer.class, username, password);
        return count != null && count > 0;
    }

    @Override
    public Account findByUserId(int userId) {
        String sql = "SELECT * FROM accounts WHERE user_id=?";
        List<Account> list = jdbc.query(sql, ACCOUNT_MAPPER, userId);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public List<Account> getAllAccounts() {
        String sql = "SELECT * FROM accounts";
        return jdbc.query(sql, ACCOUNT_MAPPER);
    }
}