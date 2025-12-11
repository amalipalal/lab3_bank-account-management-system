package services;

import interfaces.AutoIdGenerator;
import models.Account;
import models.Transaction;
import models.enums.TransactionType;

import java.time.Instant;
import java.util.*;

/**
 * Handles creation, storage, and querying of transactions within the system.
 * Enforces global transaction limits and provides summary utilities.
 */
public class TransactionManager {
    private final AutoIdGenerator idGenerator;
    private final Map<String, List<Transaction>> transactions;
    // keeps track of successful transactions since unsuccessful
    // transactions still increase idGenerator transaction count
    private int transactionCount;

    public TransactionManager(AutoIdGenerator idGenerator, Map<String, List<Transaction>> transactions) {
        this.idGenerator = idGenerator;
        this.transactions = transactions;
        this.transactionCount = transactions.values().stream().mapToInt(List::size).sum();

        updateIdGenerator();
    }

    private void updateIdGenerator() {
        if(transactions.isEmpty()) return;

        int maxCount = transactions.keySet().stream()
                .map(idGenerator::extractIndex)
                .max(Integer::compareTo)
                .orElse(0);

        idGenerator.setIdCounter(maxCount);
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
                transactionId, transactionType, account.getAccountNumber(), amount, balanceAfterTransaction, generateTimestamp());
    }

    private String generateTimestamp() {
        return Instant.now().toString();
    }

    /**
     * Stores a transaction in the system. Fails if the maximum number
     * of allowed transactions has been reached.
     *
     * @param transaction the transaction to store
     */
    public void addTransaction(Transaction transaction) {
        transactions
                .computeIfAbsent(transaction.getAccountNumber(), key -> new ArrayList<>())
                .add(transaction);
        this.transactionCount++;
    }

    /**
     * Retrieves all transactions associated with an account.
     *
     * @param accountNumber the account identifier
     * @return a List of transactions
     */
    public List<Transaction> viewTransactionsByAccount(String accountNumber) {
        return transactions
                .getOrDefault(accountNumber, Collections.emptyList())
                .stream()
                .sorted(Comparator.comparing(Transaction::getTimestamp).reversed())
                .toList();
    }

    /**
     * Computes the total deposit amount for a given account.
     *
     * @param accountNumber the account identifier
     * @return sum of all deposit transactions for the amount
     */
    public double calculateTotalDeposits(String accountNumber) {
        TransactionType transactionType = TransactionType.DEPOSIT;
        return transactions
                .getOrDefault(accountNumber, Collections.emptyList())
                .stream()
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
        return transactions
                .getOrDefault(accountNumber, Collections.emptyList())
                .stream()
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

    public List<Transaction> getAllTransactions() {
        return this.transactions.values().stream().flatMap(List::stream).toList();
    }
}
