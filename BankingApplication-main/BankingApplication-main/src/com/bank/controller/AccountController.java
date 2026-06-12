package com.bank.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bank.controller.dto.AmountRequest;
import com.bank.controller.dto.TransferRequest;
import com.bank.entity.Account;
import com.bank.entity.Transaction;
import com.bank.service.AccountService;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    // ── GET all accounts (admin) ─────────────────────────────────────────────────

    /** GET /api/accounts */
    @GetMapping
    public List<Account> getAllAccounts() {
        return accountService.getAllAccounts();
    }

    // ── GET single account ───────────────────────────────────────────────────────

    /** GET /api/accounts/{accountNo} */
    @GetMapping("/{accountNo}")
    public ResponseEntity<?> getAccount(@PathVariable int accountNo) {
        Account account = accountService.getAccount(accountNo);
        if (account == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Account not found: " + accountNo));
        }
        return ResponseEntity.ok(account);
    }

    /** GET /api/accounts/user/{userId} */
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getAccountByUser(@PathVariable int userId) {
        Account account = accountService.getAccountByUserId(userId);
        if (account == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "No account found for userId: " + userId));
        }
        return ResponseEntity.ok(account);
    }

    // ── Deposit ──────────────────────────────────────────────────────────────────

    /** POST /api/accounts/{accountNo}/deposit
     *  Body: { "amount": 500.0 } */
    @PostMapping("/{accountNo}/deposit")
    public ResponseEntity<?> deposit(@PathVariable int accountNo, @RequestBody AmountRequest req) {
        accountService.deposit(accountNo, req.getAmount());
        Account updated = accountService.getAccount(accountNo);
        return ResponseEntity.ok(Map.of(
                "message", "Deposit successful",
                "newBalance", updated.getBalance()));
    }

    // ── Withdraw ─────────────────────────────────────────────────────────────────

    /** POST /api/accounts/{accountNo}/withdraw
     *  Body: { "amount": 200.0 } */
    @PostMapping("/{accountNo}/withdraw")
    public ResponseEntity<?> withdraw(@PathVariable int accountNo, @RequestBody AmountRequest req) {
        accountService.withdraw(accountNo, req.getAmount());
        Account updated = accountService.getAccount(accountNo);
        return ResponseEntity.ok(Map.of(
                "message", "Withdrawal successful",
                "newBalance", updated.getBalance()));
    }

    // ── Transfer ─────────────────────────────────────────────────────────────────

    /** POST /api/accounts/transfer
     *  Body: { "fromAccount": 1, "toAccount": 2, "amount": 300.0 } */
    @PostMapping("/transfer")
    public ResponseEntity<?> transfer(@RequestBody TransferRequest req) {
        accountService.transfer(req.getFromAccount(), req.getToAccount(), req.getAmount());
        return ResponseEntity.ok(Map.of("message", "Transfer successful"));
    }

    // ── Delete account ───────────────────────────────────────────────────────────

    /** DELETE /api/accounts/{accountNo} */
    @DeleteMapping("/{accountNo}")
    public ResponseEntity<?> deleteAccount(@PathVariable int accountNo) {
        accountService.deleteAccount(accountNo);
        return ResponseEntity.ok(Map.of("message", "Account " + accountNo + " deleted successfully"));
    }

    // ── Transaction history ──────────────────────────────────────────────────────

    /** GET /api/accounts/{accountNo}/transactions */
    @GetMapping("/{accountNo}/transactions")
    public ResponseEntity<?> getTransactionHistory(@PathVariable int accountNo) {
        Account account = accountService.getAccount(accountNo);
        if (account == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Account not found: " + accountNo));
        }
        List<Transaction> history = accountService.getTransactionHistory(accountNo);
        return ResponseEntity.ok(history);
    }
}
