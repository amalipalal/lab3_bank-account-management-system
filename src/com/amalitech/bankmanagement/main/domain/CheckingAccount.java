package com.amalitech.bankmanagement.main.domain;

import com.amalitech.bankmanagement.main.base.Account;
import com.amalitech.bankmanagement.main.base.Customer;

public class CheckingAccount extends Account {
    private final double overDraftLimit;
    private final double monthlyFee;

    public CheckingAccount(String accountNumber, Customer customer, double balance, String status) {
        super(accountNumber, customer, balance, status);
        this.overDraftLimit = 1000;
        this.monthlyFee = 10;
    }

    @Override
    public String displayAccountDetails() {
        return "";
    }

    @Override
    public String getAccountType() {
        return "Checking";
    }

    @Override
    public void withdraw(double amount) {
        if(amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }

        double currentAccountBalance = super.getBalance();
        double newAccountBalance = currentAccountBalance - amount;

        if(newAccountBalance < -this.overDraftLimit) {
            throw new IllegalStateException("Withdrawal not allowed: overdraft limit is exceeded");
        }

        super.setBalance(newAccountBalance);
    }

    public void applyMonthlyFee() {
        double newAccountBalance = super.getBalance() - this.monthlyFee;
        if(newAccountBalance < -this.overDraftLimit) {
            throw new IllegalStateException("Monthly fee cannot be applied: overdraft limit exceeded");
        }
        super.setBalance(newAccountBalance);
    }

    public double getOverDraftLimit() {
        return this.overDraftLimit;
    }

    public double getMonthlyFee() {
        return this.monthlyFee;
    }
}
