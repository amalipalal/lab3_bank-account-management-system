package services;

import config.AppConfig;
import models.Transaction;

import java.util.Arrays;
import java.util.Objects;

public class TransactionManager {
    private final Transaction[] transactions;
    private int transactionCount;

    public TransactionManager() {
        this.transactions = new Transaction[AppConfig.MAX_TRANSACTIONS];
        this.transactionCount = 0;
    }

    public void addTransaction(Transaction transaction) {
        if(this.transactionCount == transactions.length) {
            throw new IllegalStateException("Addition not allowed: maximum number of transactions have been made");
        }

        transactions[this.transactionCount] = transaction;
        this.transactionCount++;
    }

    public Transaction[] viewTransactionsByAccount(String accountNumber) {
        return Arrays.stream(transactions)
                .filter(Objects::nonNull)
                .filter(transaction -> Objects.equals(transaction.getAccountNumber(), accountNumber))
                .toArray(Transaction[]::new);
    }

    public double calculateTotalDeposits(String accountNumber) {
        String transactionType = "deposit";
        return Arrays.stream(transactions, 0, this.transactionCount)
                .filter(Objects::nonNull)
                .filter(transaction -> Objects.equals(transaction.getAccountNumber(), accountNumber))
                .filter(transaction -> Objects.equals(transaction.getTransactionType(), transactionType))
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    public double calculateTotalWithdrawals(String accountNumber) {
        String transactionType = "withdraw";
        return Arrays.stream(transactions, 0, this.transactionCount)
                .filter(Objects::nonNull)
                .filter(transaction -> Objects.equals(transaction.getAccountNumber(), accountNumber))
                .filter(transaction -> Objects.equals(transaction.getTransactionType(), transactionType))
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    public int getTransactionCount() {
        return this.transactionCount;
    }
}
