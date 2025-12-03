package models;

import models.exceptions.InsufficientFundsException;
import models.exceptions.OverdraftExceededException;

import java.text.DecimalFormat;

public abstract class Account {
    private static final DecimalFormat FORMATTER = new DecimalFormat("000");
    private final String accountNumber;
    private final Customer customer;
    private double balance;
    private final String status;
    private static int accountCounter = 0;

    public Account(Customer customer, double balance, String status) {
        increaseAccountCount();

        this.accountNumber = generateAccountNumber();
        this.customer = customer;
        this.balance = balance;
        this.status = status;
    }

    private String generateAccountNumber() {
        String idString = FORMATTER.format(Account.accountCounter);
        return "ACC" + idString;
    }

    private void increaseAccountCount() {
        accountCounter++;
    }

    public abstract String displayAccountDetails();

    public abstract String displayNewAccountDetails();

    public abstract String getAccountType();

    public abstract void withdraw(double amount) throws OverdraftExceededException, InsufficientFundsException;

    public void deposit(double amount) {
        this.balance += amount;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public Customer getCustomer() {
        return customer;
    }

    public double getBalance() {
        return balance;
    }

    public String getStatus() {
        return status;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

}
