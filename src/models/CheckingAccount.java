package models;

import config.AppConfig;
import interfaces.Transactable;

public class CheckingAccount extends Account implements Transactable {
    private final double OVERDRAFT_LIMIT = AppConfig.OVERDRAFT_LIMIT_CHECKING_ACCOUNT;
    private double monthlyFee;

    public CheckingAccount(Customer customer, double balance, String status) {
        super(customer, balance, status);
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
    public boolean processTransaction(double amount, String type) {
        if(amount <= 0) return false;

        switch (type.toLowerCase()) {
            case "deposit":
                super.deposit(amount);
                break;
            case "withdraw":
                withdraw(amount);
                break;
            default:
                return false;
        }

        return true;
    }

    @Override
    public void withdraw(double amount) {
        if(amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }

        double currentAccountBalance = super.getBalance();
        double newAccountBalance = currentAccountBalance - amount;

        if(newAccountBalance < -this.OVERDRAFT_LIMIT) {
            throw new IllegalStateException("Withdrawal not allowed: overdraft limit is exceeded");
        }

        super.setBalance(newAccountBalance);
    }

    public void applyMonthlyFee() {
        double newAccountBalance = super.getBalance() - this.monthlyFee;
        if(newAccountBalance < -this.OVERDRAFT_LIMIT) {
            throw new IllegalStateException("Monthly fee cannot be applied: overdraft limit exceeded");
        }
        super.setBalance(newAccountBalance);
    }

    public double getOVERDRAFT_LIMIT() {
        return this.OVERDRAFT_LIMIT;
    }

    public double getMonthlyFee() {
        return this.monthlyFee;
    }

    public void setMonthlyFee(double fee) {
        this.monthlyFee = fee;
    }
}
