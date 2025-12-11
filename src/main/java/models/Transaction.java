package models;

import models.enums.TransactionType;

import java.text.DecimalFormat;
import java.time.Instant;

public class Transaction {
    private final String transactionId;
    private final TransactionType transactionType;
    private final String accountNumber;
    private final double amount;
    private final double balanceAfter;
    private final String timestamp;

    public Transaction(
            String transactionId,
            TransactionType transactionType,
            String accountNumber,
            double amount,
            double balanceAfter,
            String timestamp
    )
    {
        this.transactionId = transactionId;
        this.transactionType = transactionType;
        this.accountNumber = accountNumber;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.timestamp = timestamp;
    }

    public String displayTransactionDetails() {
        return "";
    }

    public String getTransactionId() {
        return this.transactionId;
    }

    public TransactionType getTransactionType() {
        return this.transactionType;
    }

    public String getAccountNumber() {
        return this.accountNumber;
    }

    public double getAmount() {
        return this.amount;
    }

    public double getBalanceAfter() {
        return this.balanceAfter;
    }

    public String getTimestamp() {
        return this.timestamp;
    }
}
