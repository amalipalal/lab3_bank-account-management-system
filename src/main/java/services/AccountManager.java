package services;

import config.AppConfig;
import interfaces.AutoIdGenerator;
import models.Account;
import models.CheckingAccount;
import models.Customer;
import models.SavingsAccount;
import services.exceptions.AccountLimitExceededException;
import services.exceptions.AccountNotFoundException;

import java.util.Arrays;
import java.util.Objects;

/**
 * Manages account creation, storage, and lookup operations.
 * Coordinates ID generation and maintains an in-memory list of accounts.
 *
 * <p>This class represents core business logic and is the main entry point
 * for creating and retrieving accounts during application runtime.</p>
 */
public class AccountManager {
    private final AutoIdGenerator idGenerator;
    private final Account[] accounts;

    public AccountManager(AutoIdGenerator idGenerator) {
        this.idGenerator = idGenerator;
        this.accounts = new Account[AppConfig.MAX_ACCOUNTS];
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
        int accountCount =  this.idGenerator.getCounter();

        if(accountCount == accounts.length) {
            throw new AccountLimitExceededException("Cannot add account: maximum number of accounts reached.");
        }
        // accountCounter is a counter but array is appended
        // based on zero index
        accounts[accountCount - 1] = account;
    }

    /**
     * Finds an account by its generated account number.
     *
     * @param accountNumber the account number to search for
     * @return the matching Account
     * @throws AccountNotFoundException if the account does not exist or the index is invalid
     */
    public Account findAccount(String accountNumber) throws AccountNotFoundException {
        int accountIndex = this.idGenerator.extractIndex(accountNumber);
        if( accountIndex < 0 || accountIndex >= accounts.length || accounts[accountIndex] == null) {
            throw new AccountNotFoundException("Cannot find account: account doesn't exist");
        }

        return accounts[accountIndex];
    }

    public Account[] getAllAccounts() {
        int accountCount = this.idGenerator.getCounter();
        return Arrays.copyOf(accounts, accountCount);
    }

    /**
     * Calculates the total balance across all Accounts.
     *
     * @return the sum of balances of all store accounts
     */
    public double getTotalBalance() {
        return Arrays.stream(accounts)
                    .filter(Objects::nonNull)
                    .mapToDouble(Account::getBalance)
                    .sum();
    }

    public int getAccountCount() {
        return this.idGenerator.getCounter();
    }
}
