package com.amalitech.bankmanagement.main.service;

import com.amalitech.bankmanagement.main.base.Account;
import com.amalitech.bankmanagement.main.base.Customer;
import com.amalitech.bankmanagement.main.domain.CheckingAccount;
import com.amalitech.bankmanagement.main.domain.SavingsAccount;
import com.amalitech.bankmanagement.main.domain.Transaction;
import com.amalitech.bankmanagement.main.manager.AccountManager;
import com.amalitech.bankmanagement.main.manager.TransactionManager;

public class BankingService {
    private final AccountManager accountManager;
    private final TransactionManager transactionManager;

    public BankingService(AccountManager accountManager, TransactionManager transactionManager) {
        this.accountManager = accountManager;
        this.transactionManager = transactionManager;
    }

    public Transaction processDeposit(Account account, double amount) {
        double balanceAfterTransaction = account.getBalance() + amount;
        return new Transaction("deposit", account.getAccountNumber(), amount, balanceAfterTransaction);
    }

    public Transaction processWithdrawal(Account account, double amount) {
        double balanceAfterTransaction = account.getBalance() - amount;
        return new Transaction("withdraw", account.getAccountNumber(), amount, balanceAfterTransaction);
    }

    public boolean confirmTransaction(Account account, Transaction transaction) {
        transactionManager.addTransaction(transaction);
        account.deposit(transaction.getAmount());
        return true;
    }

    public Account createSavingsAccount(Customer customer, double initialDeposit) {
        Account newAccount = new SavingsAccount(customer, initialDeposit, "active");
        accountManager.addAccount(newAccount);
        return newAccount;
    }

    public Account createCheckingAccount(Customer customer, double initialDeposit) {
        Account newAccount = new CheckingAccount(customer, initialDeposit, "active");
        accountManager.addAccount(newAccount);
        return newAccount;
    }

    public Account[] viewAllAccounts() {
        return accountManager.getAllAccounts();
    }

    public Transaction[] getTransactionsByAccount(String accountNumber) {
        return transactionManager.viewTransactionsByAccount(accountNumber);
    }

    public double getTotalDeposit(String accountNumber) {
        return transactionManager.calculateTotalDeposits(accountNumber);
    }

    public double getTotalWithdrawals(String accountNumber) {
        return transactionManager.calculateTotalWithdrawals(accountNumber);
    }

    public int getAccountCount() {
        return accountManager.getAccountCount();
    }

    public Account getAccountByNumber(String accountNumber) {
        return accountManager.findAccount(accountNumber);
    }
}
