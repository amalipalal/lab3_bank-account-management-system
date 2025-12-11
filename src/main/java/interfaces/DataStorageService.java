package interfaces;

import models.Account;
import models.Transaction;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Represents a service responsible for persisting and retrieving account
 * and transaction data. This abstraction allows for different storage
 * implementations, such as file-based storage or database storage.
 */
public interface DataStorageService {

    /**
     * Loads all accounts from the storage.
     *
     * @return a Map of account numbers to Account objects representing
     *         all accounts currently stored.
     * @throws IOException if there is an error reading from the storage.
     */
    Map<String, Account> loadAccounts() throws IOException;

    /**
     * Saves all current accounts to the persisting storage.
     *
     * @throws IOException if there is an error writing to the storage.
     */
    void saveAccounts(List<Account> accounts) throws IOException;

    /**
     * Loads all transactions from the storage.
     *
     * @return a Map where the key is the account number and the value is a
     *         list of transactions associated with that account.
     * @throws IOException if there is an error reading from the storage.
     */
    Map<String, List<Transaction>> loadTransactions() throws IOException;

    /**
     * Saves all current transactions to the storage.
     *
     * @throws IOException if there is an error writing to the storage.
     */
    void saveTransactions(List<Transaction> transactions) throws IOException;
}
