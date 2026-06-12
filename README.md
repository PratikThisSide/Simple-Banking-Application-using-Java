# Simple-Banking-Application-using-Java
A Spring Boot REST API banking application with a browser-based UI.

Tech Stack
Backend: Java 21, Spring Boot 3.2.5, Spring JDBC
Database: MySQL
Frontend: HTML + CSS + Vanilla JavaScript (served as static content)
Build: Maven
Project Structure
src/
├── com/bank/
│   ├── controller/       # REST controllers + GlobalExceptionHandler
│   │   └── dto/          # Request DTOs
│   ├── dao/              # Data access layer (JdbcTemplate)
│   ├── entity/           # User, Account, Transaction entities
│   ├── exception/        # Custom exceptions
│   ├── service/          # Business logic
│   └── main/             # Spring Boot entry point
└── main/resources/
    ├── application.properties
    └── static/index.html  # Browser UI
Database Setup
Create a MySQL database and run the following SQL:

CREATE DATABASE banking_db;
USE banking_db;

CREATE TABLE users (
    user_id   INT AUTO_INCREMENT PRIMARY KEY,
    username  VARCHAR(100) NOT NULL UNIQUE,
    email     VARCHAR(150) NOT NULL UNIQUE,
    password  VARCHAR(255) NOT NULL,
    role      VARCHAR(20)  NOT NULL DEFAULT 'CUSTOMER'
);

CREATE TABLE accounts (
    account_number      INT AUTO_INCREMENT PRIMARY KEY,
    user_id             INT NOT NULL,
    account_holder_name VARCHAR(150) NOT NULL,
    email               VARCHAR(150),
    account_type        VARCHAR(50),
    balance             DOUBLE DEFAULT 0,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE transactions (
    transaction_id   INT AUTO_INCREMENT PRIMARY KEY,
    account_number   INT NOT NULL,
    transaction_type VARCHAR(50),
    amount           DOUBLE,
    transaction_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    description      VARCHAR(255),
    FOREIGN KEY (account_number) REFERENCES accounts(account_number)
);
Configuration
Update src/main/resources/application.properties:

spring.datasource.url=jdbc:mysql://localhost:3306/banking_db
spring.datasource.username=root
spring.datasource.password=your_password
server.port=8081
Running the App
mvn spring-boot:run
Open http://localhost:8081 in your browser.

API Endpoints
Method	Endpoint	Description
POST	/api/auth/register	Register new user + create account
POST	/api/auth/login	Login
POST	/api/auth/change-password	Change password
GET	/api/accounts	Get all accounts
GET	/api/accounts/{accountNo}	Get account by number
GET	/api/accounts/user/{userId}	Get account by user ID
POST	/api/accounts/{accountNo}/deposit	Deposit funds
POST	/api/accounts/{accountNo}/withdraw	Withdraw funds
POST	/api/accounts/transfer	Transfer between accounts
GET	/api/accounts/{accountNo}/transactions	Transaction history
DELETE	/api/accounts/{accountNo}	Delete account
Browser UI Features
Register and login from the browser
View account summary (balance, type, account number)
Deposit, withdraw, and transfer funds
View full transaction history
Change account password
