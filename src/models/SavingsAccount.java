package models;

import config.AppConfig;
import interfaces.Transactable;

public class SavingsAccount extends Account implements Transactable {
    private final double INTEREST_RATE = AppConfig.INTEREST_RATE_SAVINGS_ACCOUNT;
    private final double MINIMUM_BALANCE = AppConfig.MINIMUM_INITIAL_DEPOSIT_SAVINGS;

    public SavingsAccount(Customer customer, double balance, String status) {
        super(customer, balance, status);
    }

    @Override
    public String displayAccountDetails() {
        return "";
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
        if(newAccountBalance < 0) {
            throw new IllegalArgumentException("Withdrawal amount exceeds available balance");
        } else if (newAccountBalance < MINIMUM_BALANCE) {
            throw new IllegalStateException("Withdrawal not allowed: balance is at minimum");
        }

        super.setBalance(newAccountBalance);
    }

    public double calculateInterest() {
        return super.getBalance() * this.INTEREST_RATE;
    }

    public String getAccountType() {
        return "Savings";
    }

    public double getINTEREST_RATE() {
        return this.INTEREST_RATE;
    }

    public double getMINIMUM_BALANCE() {
        return this.MINIMUM_BALANCE;
    }
}
