package services;

import config.AppConfig;
import interfaces.AutoIdGenerator;
import models.Account;
import models.CheckingAccount;
import models.Customer;
import models.SavingsAccount;
import services.exceptions.AccountLimitExceededException;
import services.exceptions.AccountNotFoundException;
import services.exceptions.InvalidAccountNumberException;

import java.util.*;

/**
 * Manages account creation, storage, and lookup operations.
 * Coordinates ID generation and maintains an in-memory list of accounts.
 *
 * <p>This class represents core business logic and is the main entry point
 * for creating and retrieving accounts during application runtime.</p>
 */
public class AccountManager {
    private final AutoIdGenerator idGenerator;
    private final Map<String, Account> accounts;

    public AccountManager(AutoIdGenerator idGenerator) {
        this.idGenerator = idGenerator;
        this.accounts = new HashMap<>();
    }

    /**
     * Creates a new savings account but does not store it.
     *
     * @param customer the account owner
     * @param balance initial account balance
     * @return the newly created SavingsAccount
     */
    public SavingsAccount createSavingsAccount(Customer customer, double balance) {
        String accountNumber = idGenerator.generateId();
        return new SavingsAccount(accountNumber, customer, balance, "active");
    }

    /**
     * Create a new checking account but does not store it.
     *
     * @param customer the account owner
     * @param balance initial account balance
     * @return the newly create CheckingAccount
     */
    public CheckingAccount createCheckingAccount(Customer customer, double balance) {
        String accountNumber = idGenerator.generateId();
        return new CheckingAccount(accountNumber, customer, balance, "active");
    }

    /**
     * Adds an account to the internal store.
     *
     * @param account the account to add
     */
    public void addAccount(Account account) {
        if(accounts.containsKey(account.getAccountNumber())) {
            throw new InvalidAccountNumberException("This account Id already exists");
        }
        accounts.put(account.getAccountNumber(), account);
    }

    /**
     * Finds an account by its account number.
     *
     * @param accountNumber the account number to search for
     * @return the matching Account
     * @throws AccountNotFoundException if the account does not exist
     */
    public Account findAccount(String accountNumber) throws AccountNotFoundException {
        Account account = accounts.get(accountNumber);
        if(account == null) {
            throw new AccountNotFoundException("Cannot find account: Account doesn't exist");
        }
        return account;
    }

    public List<Account> getAllAccounts() {
        return List.copyOf(accounts.values());
    }

    /**
     * Calculates the total balance across all Accounts.
     *
     * @return the sum of balances of all store accounts
     */
    public double getTotalBalance() {
        return accounts.values()
                .stream()
                .mapToDouble(Account::getBalance)
                .sum();
    }

    public int getAccountCount() {
        return this.idGenerator.getCounter();
    }
}
