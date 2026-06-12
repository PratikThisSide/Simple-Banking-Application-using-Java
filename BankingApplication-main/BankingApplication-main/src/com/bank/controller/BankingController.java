package com.bank.controller;

import java.util.List;
import java.util.Scanner;

import com.bank.dao.UserDAO;
import com.bank.dao.UserDAOImpl;
import com.bank.entity.Account;
import com.bank.entity.Transaction;
import com.bank.entity.User;
import com.bank.exception.AccountNotFoundException;
import com.bank.exception.BankingException;
import com.bank.exception.InsufficientBalanceException;
import com.bank.service.AccountService;
import com.bank.service.AccountServiceImpl;

public class BankingController {

    private final AccountService service;
    private final UserDAO userDAO;
    private final Scanner sc;

    public BankingController() {
        this.service = new AccountServiceImpl();
        this.userDAO = new UserDAOImpl();
        this.sc = new Scanner(System.in);
    }

    // ─── ENTRY POINT ─────────────────────────────────────────────────────────────

    public void run() {

        boolean running = true;

        while (running) {
            printHeader("BANKING MANAGEMENT SYSTEM");
            System.out.println("  1. Login");
            System.out.println("  2. Create New Account");
            System.out.println("  3. Exit");
            System.out.print("  Enter Choice : ");

            int choice = readInt();

            switch (choice) {
                case 1:
                    handleLogin();
                    break;
                case 2:
                    handleRegistration();
                    break;
                case 3:
                    System.out.println("\n  Thank you for using the Banking System. Goodbye!\n");
                    running = false;
                    break;
                default:
                    System.out.println("  Invalid choice. Please try again.");
            }
        }

        sc.close();
    }

    // ─── REGISTRATION ────────────────────────────────────────────────────────────

    private void handleRegistration() {

        printHeader("CREATE NEW ACCOUNT");

        System.out.print("  Full Name              : ");
        String name = sc.nextLine().trim();
        if (name.isEmpty()) {
            System.out.println("  Name cannot be empty.");
            return;
        }

        System.out.print("  Email                  : ");
        String email = sc.nextLine().trim();
        if (!email.contains("@") || !email.contains(".")) {
            System.out.println("  Invalid email address.");
            return;
        }

        System.out.print("  Password (min 4 chars) : ");
        String password = sc.nextLine().trim();
        if (password.length() < 4) {
            System.out.println("  Password must be at least 4 characters.");
            return;
        }

        System.out.print("  Account Type (Savings/Current) : ");
        String type = sc.nextLine().trim();
        if (!type.equalsIgnoreCase("Savings") && !type.equalsIgnoreCase("Current")) {
            System.out.println("  Invalid account type. Choose Savings or Current.");
            return;
        }

        System.out.print("  Initial Balance (min 500) : ");
        double balance = readDouble();
        if (balance < 500) {
            System.out.println("  Minimum initial deposit is 500.");
            return;
        }

        int userId = userDAO.createUser(name, email, password, "CUSTOMER");
        if (userId == -1) {
            System.out.println("  Registration failed. Username may already exist.");
            return;
        }

        Account account = new Account();
        account.setUserId(userId);
        account.setAccountHolderName(name);
        account.setEmail(email);
        account.setAccountType(type);
        account.setBalance(balance);

        if (service.createAccountDirect(account)) {
            System.out.println("\n  ✔ Account created successfully!");
            System.out.println("  Username : " + name);
            System.out.println("  Please login to continue.");
        } else {
            System.out.println("  Account creation failed. Please try again.");
        }
    }

    // ─── LOGIN ────────────────────────────────────────────────────────────────────

    private void handleLogin() {

        printHeader("LOGIN");

        System.out.print("  Username : ");
        String username = sc.nextLine().trim();

        System.out.print("  Password : ");
        String password = sc.nextLine().trim();

        User user = userDAO.login(username, password);

        if (user == null) {
            System.out.println("  Invalid credentials. Please try again.");
            return;
        }

        System.out.println("\n  Welcome, " + user.getUsername() + "!  [" + user.getRole() + "]");

        if ("ADMIN".equals(user.getRole())) {
            adminMenu(user);
        } else {
            Account acc = service.getAccountByUserId(user.getUserId());
            if (acc != null) {
                printAccountBox(acc);
            }
            userMenu(user);
        }
    }

    // ─── ADMIN MENU ──────────────────────────────────────────────────────────────

    private void adminMenu(User user) {

        int choice;

        do {
            printHeader("ADMIN MENU");
            System.out.println("   1.  Create Account");
            System.out.println("   2.  Deposit");
            System.out.println("   3.  Withdraw");
            System.out.println("   4.  Transfer Money");
            System.out.println("   5.  Check Balance");
            System.out.println("   6.  View Account Details");
            System.out.println("   7.  View All Accounts");
            System.out.println("   8.  Delete Account");
            System.out.println("   9.  Transaction History");
            System.out.println("   10. Logout");
            System.out.print("  Enter Choice : ");

            choice = readInt();

            try {
                switch (choice) {
                    case 1:  adminCreateAccount();         break;
                    case 2:  adminDeposit();               break;
                    case 3:  adminWithdraw();              break;
                    case 4:  adminTransfer();              break;
                    case 5:  adminCheckBalance();          break;
                    case 6:  adminViewAccount();           break;
                    case 7:  adminViewAllAccounts();       break;
                    case 8:  adminDeleteAccount();         break;
                    case 9:  adminViewTransactionHistory(); break;
                    case 10: System.out.println("\n  Logged out successfully."); break;
                    default: System.out.println("  Invalid choice. Try again.");
                }
            } catch (AccountNotFoundException | InsufficientBalanceException | BankingException e) {
                System.out.println("  Error: " + e.getMessage());
            }

        } while (choice != 10);
    }

    private void adminCreateAccount() {
        System.out.print("  Holder Name    : ");
        String name = sc.nextLine().trim();

        System.out.print("  Email          : ");
        String email = sc.nextLine().trim();

        System.out.print("  Account Type (Savings/Current) : ");
        String type = sc.nextLine().trim();

        System.out.print("  Initial Balance: ");
        double balance = readDouble();

        Account account = new Account();
        account.setAccountHolderName(name);
        account.setEmail(email);
        account.setAccountType(type);
        account.setBalance(balance);

        service.createAccount(account);
    }

    private void adminDeposit() {
        System.out.print("  Account Number : ");
        int accNo = readInt();
        System.out.print("  Amount         : ");
        double amount = readDouble();
        service.deposit(accNo, amount);
    }

    private void adminWithdraw() {
        System.out.print("  Account Number : ");
        int accNo = readInt();
        System.out.print("  Amount         : ");
        double amount = readDouble();
        service.withdraw(accNo, amount);
    }

    private void adminTransfer() {
        System.out.print("  Sender Account Number   : ");
        int sender = readInt();
        System.out.print("  Receiver Account Number : ");
        int receiver = readInt();
        System.out.print("  Amount                  : ");
        double amount = readDouble();
        service.transfer(sender, receiver, amount);
    }

    private void adminCheckBalance() {
        System.out.print("  Account Number : ");
        int accNo = readInt();
        Account acc = service.getAccount(accNo);
        if (acc != null) {
            System.out.printf("  Balance: %.2f%n", acc.getBalance());
        } else {
            System.out.println("  Account not found.");
        }
    }

    private void adminViewAccount() {
        System.out.print("  Account Number : ");
        int accNo = readInt();
        Account acc = service.getAccount(accNo);
        if (acc != null) {
            printAccountBox(acc);
        } else {
            System.out.println("  Account not found.");
        }
    }

    private void adminViewAllAccounts() {
        List<Account> accounts = service.getAllAccounts();
        if (accounts.isEmpty()) {
            System.out.println("  No accounts found.");
        } else {
            System.out.println("\n  ===== ALL ACCOUNTS (" + accounts.size() + ") =====");
            for (Account a : accounts) {
                printAccountBox(a);
            }
        }
    }

    private void adminDeleteAccount() {
        System.out.print("  Account Number : ");
        int accNo = readInt();
        System.out.print("  Confirm delete? (yes/no) : ");
        String confirm = sc.nextLine().trim();
        if ("yes".equalsIgnoreCase(confirm)) {
            service.deleteAccount(accNo);
        } else {
            System.out.println("  Delete cancelled.");
        }
    }

    private void adminViewTransactionHistory() {
        System.out.print("  Account Number : ");
        int accNo = readInt();
        printTransactionHistory(service.getTransactionHistory(accNo));
    }

    // ─── USER MENU ────────────────────────────────────────────────────────────────

    private void userMenu(User user) {

        Account userAcc = service.getAccountByUserId(user.getUserId());
        if (userAcc == null) {
            System.out.println("  No account linked to your profile.");
            return;
        }

        int choice;

        do {
            printHeader("USER MENU  |  " + user.getUsername());
            System.out.println("  1. View Account Details");
            System.out.println("  2. Check Balance");
            System.out.println("  3. Deposit Money");
            System.out.println("  4. Withdraw Money");
            System.out.println("  5. Transfer Money");
            System.out.println("  6. Transaction History");
            System.out.println("  7. Change Password");
            System.out.println("  8. Logout");
            System.out.print("  Enter Choice : ");

            choice = readInt();

            try {
                switch (choice) {

                    case 1:
                        printAccountBox(service.getAccountByUserId(user.getUserId()));
                        break;

                    case 2:
                        System.out.printf("  Current Balance: %.2f%n",
                                service.getAccountByUserId(user.getUserId()).getBalance());
                        break;

                    case 3: {
                        System.out.print("  Amount : ");
                        double amount = readDouble();
                        service.deposit(userAcc.getAccountNo(), amount);
                        userAcc = service.getAccountByUserId(user.getUserId());
                        break;
                    }

                    case 4: {
                        System.out.print("  Amount : ");
                        double amount = readDouble();
                        service.withdraw(userAcc.getAccountNo(), amount);
                        userAcc = service.getAccountByUserId(user.getUserId());
                        break;
                    }

                    case 5: {
                        System.out.print("  Receiver Account Number : ");
                        int receiver = readInt();
                        System.out.print("  Amount                  : ");
                        double amount = readDouble();
                        service.transfer(userAcc.getAccountNo(), receiver, amount);
                        userAcc = service.getAccountByUserId(user.getUserId());
                        break;
                    }

                    case 6:
                        printTransactionHistory(service.getTransactionHistory(userAcc.getAccountNo()));
                        break;

                    case 7:
                        changePassword(user.getUserId());
                        break;

                    case 8:
                        System.out.println("\n  Logged out successfully. Goodbye!");
                        break;

                    default:
                        System.out.println("  Invalid choice.");
                }

            } catch (AccountNotFoundException | InsufficientBalanceException | BankingException e) {
                System.out.println("  Error: " + e.getMessage());
            }

        } while (choice != 8);
    }

    // ─── CHANGE PASSWORD ─────────────────────────────────────────────────────────

    private void changePassword(int userId) {

        System.out.print("  Current Password : ");
        String oldPass = sc.nextLine().trim();

        System.out.print("  New Password     : ");
        String newPass = sc.nextLine().trim();

        if (newPass.length() < 4) {
            System.out.println("  Password must be at least 4 characters.");
            return;
        }

        System.out.print("  Confirm Password : ");
        String confirmPass = sc.nextLine().trim();

        if (!newPass.equals(confirmPass)) {
            System.out.println("  Passwords do not match.");
            return;
        }

        if (userDAO.changePassword(userId, oldPass, newPass)) {
            System.out.println("  Password changed successfully.");
        } else {
            System.out.println("  Current password is incorrect.");
        }
    }

    // ─── DISPLAY HELPERS ─────────────────────────────────────────────────────────

    private void printTransactionHistory(List<Transaction> transactions) {
        if (transactions.isEmpty()) {
            System.out.println("  No transactions found.");
            return;
        }
        System.out.println("\n  ┌─────────────────────────────────────────────────────────┐");
        System.out.println("  │                  TRANSACTION HISTORY                   │");
        System.out.println("  ├──────┬──────────┬──────────┬──────────────┬────────────┤");
        System.out.printf( "  │ %-4s │ %-8s │ %-8s │ %-12s │ %-10s │%n",
                "ID", "From Acc", "To Acc", "Type", "Amount");
        System.out.println("  ├──────┼──────────┼──────────┼──────────────┼────────────┤");
        for (Transaction t : transactions) {
            String toAcc = t.getToAccount() == 0 ? "-" : String.valueOf(t.getToAccount());
            System.out.printf("  │ %-4d │ %-8d │ %-8s │ %-12s │ %10.2f │%n",
                    t.getTransactionId(), t.getFromAccount(), toAcc,
                    t.getTransactionType(), t.getAmount());
        }
        System.out.println("  └──────┴──────────┴──────────┴──────────────┴────────────┘");
    }

    private void printAccountBox(Account acc) {
        System.out.println("\n  ┌─────────────────────────────┐");
        System.out.println("  │      ACCOUNT DETAILS        │");
        System.out.println("  ├─────────────────────────────┤");
        System.out.printf( "  │  Account No  : %-12d │%n", acc.getAccountNo());
        System.out.printf( "  │  Holder Name : %-12s │%n", acc.getAccountHolderName());
        System.out.printf( "  │  Type        : %-12s │%n", acc.getAccountType());
        System.out.printf( "  │  Balance     : %-12.2f │%n", acc.getBalance());
        System.out.println("  └─────────────────────────────┘");
    }

    private void printHeader(String title) {
        int width = 44;
        System.out.println();
        System.out.println("  +" + "=".repeat(width) + "+");
        System.out.printf( "  |  %-" + (width - 2) + "s |%n", title);
        System.out.println("  +" + "=".repeat(width) + "+");
    }

    // ─── INPUT HELPERS ───────────────────────────────────────────────────────────

    private int readInt() {
        while (true) {
            try {
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("  Invalid input. Enter a number : ");
            }
        }
    }

    private double readDouble() {
        while (true) {
            try {
                double val = Double.parseDouble(sc.nextLine().trim());
                if (val <= 0) {
                    System.out.print("  Amount must be positive. Enter again : ");
                    continue;
                }
                return val;
            } catch (NumberFormatException e) {
                System.out.print("  Invalid input. Enter a number : ");
            }
        }
    }
}
