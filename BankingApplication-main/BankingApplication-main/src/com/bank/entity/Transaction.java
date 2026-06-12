package com.bank.entity;

public class Transaction {

	private int transactionId;
	private int fromAccount;
	private int toAccount;
	private String transactionType;
	private double amount;

	public Transaction() {
	}

	public Transaction(int fromAccount, int toAccount, String transactionType, double amount) {

		this.fromAccount = fromAccount;
		this.toAccount = toAccount;
		this.transactionType = transactionType;
		this.amount = amount;
	}

	public int getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(int transactionId) {
		this.transactionId = transactionId;
	}

	public int getFromAccount() {
		return fromAccount;
	}

	public void setFromAccount(int fromAccount) {
		this.fromAccount = fromAccount;
	}

	public int getToAccount() {
		return toAccount;
	}

	public void setToAccount(int toAccount) {
		this.toAccount = toAccount;
	}

	public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	@Override
	public String toString() {
		return String.format("TxnID: %-5d | From: %-6d | To: %-6d | %-12s | Amount: %.2f",
				transactionId, fromAccount, toAccount, transactionType, amount);
	}
}