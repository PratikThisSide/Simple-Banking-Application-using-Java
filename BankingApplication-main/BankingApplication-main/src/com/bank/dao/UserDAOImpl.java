package com.bank.dao;

import java.sql.PreparedStatement;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.bank.entity.User;

@Repository
public class UserDAOImpl implements UserDAO {

    private final JdbcTemplate jdbc;

    public UserDAOImpl(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static final RowMapper<User> USER_MAPPER = (rs, rowNum) -> {
        User u = new User();
        u.setUserId(rs.getInt("user_id"));
        u.setUsername(rs.getString("username"));
        u.setRole(rs.getString("role"));
        return u;
    };

    @Override
    public User login(String username, String password) {
        String sql = "SELECT * FROM users WHERE username=? AND password=?";
        List<User> list = jdbc.query(sql, USER_MAPPER, username, password);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public int createUser(String username, String email, String password, String role) {
        String sql = "INSERT INTO users(username, email, password, role) VALUES(?,?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, new String[]{"user_id"});
            ps.setString(1, username);
            ps.setString(2, email);
            ps.setString(3, password);
            ps.setString(4, role);
            return ps;
        }, keyHolder);
        Number key = keyHolder.getKey();
        return key != null ? key.intValue() : -1;
    }

    @Override
    public boolean changePassword(int userId, String oldPassword, String newPassword) {
        String sql = "UPDATE users SET password=? WHERE user_id=? AND password=?";
        return jdbc.update(sql, newPassword, userId, oldPassword) > 0;
    }
}