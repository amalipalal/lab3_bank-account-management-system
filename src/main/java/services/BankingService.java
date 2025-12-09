package services;

import models.Account;
import models.Customer;
import models.CheckingAccount;
import models.PremiumCustomer;
import models.SavingsAccount;
import models.Transaction;
import models.enums.TransactionType;
import models.exceptions.InsufficientFundsException;
import models.exceptions.OverdraftExceededException;
import services.exceptions.AccountNotFoundException;

public class BankingService {
    private final AccountManager accountManager;
    private final TransactionManager transactionManager;

    public BankingService(AccountManager accountManager, TransactionManager transactionManager) {
        this.accountManager = accountManager;
        this.transactionManager = transactionManager;
    }

    /**
     * Creates a deposit transaction for the given account and amount.
     * <p>
     * Note: This does not update the account balance. Use {@link #confirmTransaction} to apply it.
     *
     * @param account the account to deposit into
     * @param amount  the amount to deposit
     * @return a Transaction object representing the deposit
     */
    public Transaction processDeposit(Account account, double amount) {
        double balanceAfterTransaction = account.getBalance() + amount;
        return this.transactionManager.createTransaction(TransactionType.DEPOSIT, account, amount, balanceAfterTransaction);
    }

    /**
     * Creates a deposit transaction for the given account and amount.
     * <p>
     * Note: This does not update the account balance. Use {@link #confirmTransaction} to apply it.
     *
     * @param account the account to deposit into
     * @param amount  the amount to deposit
     * @return a Transaction object representing the deposit
     */
    public Transaction processWithdrawal(Account account, double amount) {
        double balanceAfterTransaction = account.getBalance() - amount;
        return this.transactionManager.createTransaction(TransactionType.WITHDRAWAL, account, amount, balanceAfterTransaction);
    }

    /**
     * Applies a transaction to the given account by updating the balance and recording it.
     * <p>
     * Throws {@link OverdraftExceededException} or {@link InsufficientFundsException} if the transaction
     * violates account rules.
     *
     * @param account     the account to update
     * @param transaction the transaction to apply
     * @throws OverdraftExceededException if withdrawal exceeds overdraft limit
     * @throws InsufficientFundsException if withdrawal exceeds available balance
     */
    public void confirmTransaction(Account account, Transaction transaction) throws OverdraftExceededException,
            InsufficientFundsException {
        TransactionType type = transaction.getTransactionType();

        switch (type) {
            case TransactionType.WITHDRAWAL -> account.withdraw(transaction.getAmount());
            case TransactionType.DEPOSIT -> account.deposit(transaction.getAmount());
            default -> throw new IllegalArgumentException("Unsupported transaction type: " + type);
        }

        this.transactionManager.addTransaction(transaction);
    }

    /**
     * Creates a new savings account for the given customer with an initial balance of zero.
     * <p>
     * The initial deposit should be processed separately as a transaction using {@link #processDeposit} and
     * {@link #confirmTransaction}.
     *
     * @param customer the customer who owns the account
     * @return the newly created SavingsAccount
     */
    public Account createSavingsAccount(Customer customer) {
        SavingsAccount newAccount = accountManager.createSavingsAccount(customer, 0);
        accountManager.addAccount(newAccount);
        return newAccount;
    }

    /**
     * Creates a new checking account for the given customer with an initial balance of zero.
     * <p>
     * Premium customers have the monthly fee waived. Initial deposit should be processed separately
     * as a transaction using {@link #processDeposit} and {@link #confirmTransaction}.
     *
     * @param customer the customer who owns the account
     * @return the newly created CheckingAccount
     */
    public Account createCheckingAccount(Customer customer) {
        CheckingAccount newAccount = accountManager.createCheckingAccount(customer, 0);

        if(customer instanceof PremiumCustomer) newAccount.setMonthlyFee(0);

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

    public double getTotalBankBalance() {
        return accountManager.getTotalBalance();
    }

    public int getAccountCount() {
        return accountManager.getAccountCount();
    }

    public Account getAccountByNumber(String accountNumber) throws AccountNotFoundException {
        return accountManager.findAccount(accountNumber);
    }
}
