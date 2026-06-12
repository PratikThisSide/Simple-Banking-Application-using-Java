package com.bank.dao;

import com.bank.entity.User;

public interface UserDAO {

    User login(String username, String password);

    int createUser(String username, String email, String password, String role);

    boolean changePassword(int userId, String oldPassword, String newPassword);

}