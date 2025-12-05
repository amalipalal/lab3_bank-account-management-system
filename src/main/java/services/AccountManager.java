package services;

import config.AppConfig;
import models.Account;
import services.exceptions.AccountLimitExceededException;
import services.exceptions.AccountNotFoundException;
import services.exceptions.InvalidAccountNumberException;

import java.util.Arrays;
import java.util.Objects;

public class AccountManager {
    private final Account[] accounts;
    private int accountCount;

    public AccountManager() {
        this.accounts = new Account[AppConfig.MAX_ACCOUNTS];
        this.accountCount = 0;
    }

    public void addAccount(Account account) {
        if(accountCount == accounts.length) {
            throw new AccountLimitExceededException("Cannot add account: maximum number of accounts reached.");
        }
        accounts[accountCount] = account;
        accountCount++;
    }

    public Account findAccount(String accountNumber) throws AccountNotFoundException {
        int accountIndex = extractAccountIndex(accountNumber);
        if( accountIndex < 0 || accountIndex >= accounts.length || accounts[accountIndex] == null) {
            throw new AccountNotFoundException("Cannot find account: account doesn't exist");
        }

        return accounts[accountIndex];
    }

    private int extractAccountIndex(String accountNumber) {
        int startIndex = accountNumber.length() - 3;
        String indexString = accountNumber.substring(startIndex);

        try {
            return Integer.parseInt(indexString) - 1;
        } catch (NumberFormatException e) {
            throw new InvalidAccountNumberException("Account number format is invalid: " + accountNumber);
        }
    }

    public Account[] getAllAccounts() {
        return Arrays.copyOf(accounts, accountCount);
    }

    public double getTotalBalance() {
        return Arrays.stream(accounts)
                    .filter(Objects::nonNull)
                    .mapToDouble(Account::getBalance)
                    .sum();
    }

    public int getAccountCount() {
        return this.accountCount;
    }
}
