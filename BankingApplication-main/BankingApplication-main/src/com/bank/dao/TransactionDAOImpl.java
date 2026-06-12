package com.bank.dao;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.bank.entity.Transaction;

@Repository
public class TransactionDAOImpl implements TransactionDAO {

    private final JdbcTemplate jdbc;

    public TransactionDAOImpl(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static final RowMapper<Transaction> TX_MAPPER = (rs, rowNum) -> {
        Transaction t = new Transaction();
        t.setTransactionId(rs.getInt("transaction_id"));
        t.setFromAccount(rs.getInt("from_account"));
        t.setToAccount(rs.getInt("to_account"));
        t.setTransactionType(rs.getString("transaction_type"));
        t.setAmount(rs.getDouble("amount"));
        return t;
    };

    @Override
    public boolean addTransaction(Transaction transaction) {
        String sql = "INSERT INTO transactions(from_account, to_account, transaction_type, amount) VALUES(?,?,?,?)";
        return jdbc.update(sql,
                transaction.getFromAccount(),
                transaction.getToAccount(),
                transaction.getTransactionType(),
                transaction.getAmount()) > 0;
    }

    @Override
    public List<Transaction> getTransactionsByAccount(int accountNo) {
        String sql = "SELECT * FROM transactions WHERE from_account=? OR to_account=? ORDER BY transaction_id DESC";
        return jdbc.query(sql, TX_MAPPER, accountNo, accountNo);
    }
}