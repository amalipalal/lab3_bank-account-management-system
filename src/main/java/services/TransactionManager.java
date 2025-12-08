package services;

import config.AppConfig;
import interfaces.AutoIdGenerator;
import models.Account;
import models.Transaction;
import models.enums.TransactionType;
import services.exceptions.TransactionLimitExceededException;

import java.util.Arrays;
import java.util.Objects;

/**
 * Handles creation, storage, and querying of transactions within the system.
 * Enforces global transaction limits and provides summary utilities.
 */
public class TransactionManager {
    private final AutoIdGenerator idGenerator;
    private final Transaction[] transactions;
    // keeps track of successful transactions since unsuccessful
    // transactions still increase idGenerator transaction count
    private int transactionCount;

    public TransactionManager(AutoIdGenerator idGenerator) {
        this.idGenerator = idGenerator;
        this.transactions = new Transaction[AppConfig.MAX_TRANSACTIONS];
        this.transactionCount = 0;
    }

    /**
     * Builds a transaction object using the provided details.
     *
     * @param transactionType the type of transaction being performed
     * @param account the account involved in the transaction
     * @param amount the transaction amount
     * @param balanceAfterTransaction the account balance after applying the transaction
     * @return a newly created transaction instance
     */
    public Transaction createTransaction(
            TransactionType transactionType, Account account, double amount, double balanceAfterTransaction) {
        String transactionId = idGenerator.generateId();
        return new Transaction(
                transactionId, transactionType, account.getAccountNumber(), amount, balanceAfterTransaction);
    }

    /**
     * Stores a transaction in the system. Fails if the maximum number
     * of allowed transactions has been reached.
     *
     * @param transaction the transaction to store
     */
    public void addTransaction(Transaction transaction) {
        if(this.transactionCount == transactions.length)
            throw new TransactionLimitExceededException("Addition not allowed: maximum number of transactions have been made");

        transactions[this.transactionCount] = transaction;
        this.transactionCount++;
    }

    /**
     * Retrieves all transactions associated with an account.
     *
     * @param accountNumber the account identifier
     * @return an array of empty transactions (empty if none found)
     */
    public Transaction[] viewTransactionsByAccount(String accountNumber) {
        return Arrays.stream(transactions)
                .filter(Objects::nonNull)
                .filter(transaction -> Objects.equals(transaction.getAccountNumber(), accountNumber))
                .toArray(Transaction[]::new);
    }

    /**
     * Computes the total deposit amount for a given account.
     *
     * @param accountNumber the account identifier
     * @return sum of all deposit transactions for the amount
     */
    public double calculateTotalDeposits(String accountNumber) {
        TransactionType transactionType = TransactionType.DEPOSIT;
        return Arrays.stream(transactions, 0, this.transactionCount)
                .filter(Objects::nonNull)
                .filter(transaction -> Objects.equals(transaction.getAccountNumber(), accountNumber))
                .filter(transaction -> transaction.getTransactionType() == transactionType)
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    /**
     * Computes the total withdrawal amount for a given account.
     *
     * @param accountNumber the account identifier
     * @return sum of all withdrawal transactions for the amount
     */
    public double calculateTotalWithdrawals(String accountNumber) {
        TransactionType transactionType = TransactionType.WITHDRAWAL;
        return Arrays.stream(transactions, 0, this.transactionCount)
                .filter(Objects::nonNull)
                .filter(transaction -> Objects.equals(transaction.getAccountNumber(), accountNumber))
                .filter(transaction -> transaction.getTransactionType() == transactionType)
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    /**
     * Returns the number of successfully recorded transactions.
     *
     * @return count of stored transactions
     */
    public int getTransactionCount() {
        return this.transactionCount;
    }
}
