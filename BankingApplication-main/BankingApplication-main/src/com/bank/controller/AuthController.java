package com.bank.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bank.controller.dto.ChangePasswordRequest;
import com.bank.controller.dto.LoginRequest;
import com.bank.controller.dto.RegisterRequest;
import com.bank.dao.UserDAO;
import com.bank.entity.Account;
import com.bank.entity.User;
import com.bank.service.AccountService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserDAO userDAO;
    private final AccountService accountService;

    public AuthController(UserDAO userDAO, AccountService accountService) {
        this.userDAO = userDAO;
        this.accountService = accountService;
    }

    /**
     * POST /api/auth/login
     * Body: { "username": "alice", "password": "pass" }
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        if (req.getUsername() == null || req.getPassword() == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Username and password are required"));
        }

        User user = userDAO.login(req.getUsername(), req.getPassword());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid credentials"));
        }
        return ResponseEntity.ok(user);
    }

    /**
     * POST /api/auth/register
     * Body: { "name": "Alice", "email": "alice@mail.com", "password": "pass",
     *         "accountType": "Savings", "initialBalance": 1000 }
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        if (req.getName() == null || req.getEmail() == null || req.getPassword() == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Name, email, and password are required"));
        }
        if (!req.getEmail().contains("@")) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid email address"));
        }
        if (req.getPassword().length() < 4) {
            return ResponseEntity.badRequest().body(Map.of("error", "Password must be at least 4 characters"));
        }
        if (!"Savings".equalsIgnoreCase(req.getAccountType()) && !"Current".equalsIgnoreCase(req.getAccountType())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Account type must be Savings or Current"));
        }
        if (req.getInitialBalance() < 500) {
            return ResponseEntity.badRequest().body(Map.of("error", "Minimum initial deposit is 500"));
        }

        int userId = userDAO.createUser(req.getName(), req.getEmail(), req.getPassword(), "CUSTOMER");
        if (userId == -1) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Registration failed. Username or email may already exist."));
        }

        Account account = new Account();
        account.setUserId(userId);
        account.setAccountHolderName(req.getName());
        account.setEmail(req.getEmail());
        account.setAccountType(req.getAccountType());
        account.setBalance(req.getInitialBalance());

        if (!accountService.createAccountDirect(account)) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Account creation failed"));
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Account created successfully. Username: " + req.getName()));
    }

    /**
     * POST /api/auth/change-password
     * Body: { "userId": 1, "oldPassword": "old", "newPassword": "new" }
     */
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest req) {
        boolean updated = userDAO.changePassword(req.getUserId(), req.getOldPassword(), req.getNewPassword());
        if (!updated) {
            return ResponseEntity.badRequest().body(Map.of("error", "Password change failed. Check userId and old password."));
        }
        return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
    }
}
