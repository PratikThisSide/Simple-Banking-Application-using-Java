package com.bank.entity;

public class Account {

	private int accountNo;
	private int userId;
	private String accountHolderName;
	private String email;
	private String accountType;
	private double balance;
	
	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Account() {
	}

	public Account(int accountNo, String accountHolderName, String accountType, double balance) {

		this.accountNo = accountNo;
		this.accountHolderName = accountHolderName;
		this.accountType = accountType;
		this.balance = balance;
	}

	public int getAccountNo() {
		return accountNo;
	}

	public void setAccountNo(int accountNo) {
		this.accountNo = accountNo;
	}

	public String getAccountHolderName() {
		return accountHolderName;
	}

	public void setAccountHolderName(String accountHolderName) {
		this.accountHolderName = accountHolderName;
	}

	public String getAccountType() {
		return accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	@Override
	public String toString() {
		return "\nAccount No : " + accountNo + "\nHolder Name : " + accountHolderName + "\nAccount Type : "
				+ accountType + "\nBalance : " + balance;
	}
}